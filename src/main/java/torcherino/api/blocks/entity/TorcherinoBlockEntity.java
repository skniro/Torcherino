package torcherino.api.blocks.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoAPI;
import torcherino.config.Config;

@SuppressWarnings("SpellCheckingInspection")
public class TorcherinoBlockEntity extends BlockEntity implements Nameable, Tickable, TierSupplier
{
    private static final String onlineMode = Config.INSTANCE.online_mode;
    public static int randomTicks;
    private Text customName;
    private int xRange, yRange, zRange, speed, redstoneMode;
    private Iterable<BlockPos> area;
    private boolean active;
    private boolean loaded = false;
    private Identifier tierID;
    private String uuid = "";

    public TorcherinoBlockEntity() { super(Registry.BLOCK_ENTITY.get(new Identifier("torcherino", "torcherino"))); }

    @Override
    public boolean hasCustomName() { return customName != null; }

    @Override
    public Text getCustomName() { return customName; }

    public void setCustomName(Text name) { customName = name; }

    private String getOwner() { return uuid; }

    public void setOwner(String s) { uuid = s; }

    @Override
    public Text getName() { return hasCustomName() ? customName : new TranslatableText(getCachedState().getBlock().getTranslationKey()); }

    @Override
    public void tick()
    {
        if (!loaded)
        {
            area = BlockPos.iterate(pos.getX() - xRange, pos.getY() - yRange, pos.getZ() - zRange,
                    pos.getX() + xRange, pos.getY() + yRange, pos.getZ() + zRange);
            getCachedState().getBlock().neighborUpdate(getCachedState(), world, pos, null, null, false);
            randomTicks = world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED); // update via mixin
            loaded = true;
        }
        if (!active || speed == 0 || (xRange == 0 && yRange == 0 && zRange == 0)) return;
        if (!onlineMode.equals(""))
        {
            if (!Torcherino.hasIsOnline(getOwner())) return;

        }
        area.forEach(this::tickBlock);
    }

    private void tickBlock(BlockPos pos)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (TorcherinoAPI.INSTANCE.isBlockBlacklisted(block)) return;
        if (block.hasRandomTicks(blockState) &&
                world.getRandom().nextInt(MathHelper.clamp(4096 / (speed * Config.INSTANCE.random_tick_rate), 1, 4096)) < randomTicks)
        {
            block.onRandomTick(blockState, world, pos, world.getRandom());
        }
        if (!block.hasBlockEntity()) return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.isInvalid() || TorcherinoAPI.INSTANCE.isBlockEntityBlacklisted(blockEntity.getType()) ||
                !(blockEntity instanceof Tickable)) { return; }
        for (int i = 0; i < speed; i++)
        {
            if (blockEntity.isInvalid()) break;
            ((Tickable) blockEntity).tick();
        }
    }

    public void writeClientData(PacketByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeText(getName());
        buffer.writeInt(xRange);
        buffer.writeInt(zRange);
        buffer.writeInt(yRange);
        buffer.writeInt(speed);
        buffer.writeInt(redstoneMode);
    }

    public void readClientData(PacketByteBuf buffer)
    {
        Tier tier = TorcherinoAPI.INSTANCE.getTiers().get(getTier());
        this.xRange = MathHelper.clamp(buffer.readInt(), 0, tier.getXZRange());
        this.zRange = MathHelper.clamp(buffer.readInt(), 0, tier.getXZRange());
        this.yRange = MathHelper.clamp(buffer.readInt(), 0, tier.getYRange());
        this.speed = MathHelper.clamp(buffer.readInt(), 1, tier.getMaxSpeed());
        this.redstoneMode = MathHelper.clamp(buffer.readInt(), 0, 3);
        loaded = false;
    }

    @Override
    public Identifier getTier()
    {
        if (tierID == null)
        {
            Block block = getCachedState().getBlock();
            if (block instanceof TierSupplier) { tierID = ((TierSupplier) block).getTier(); }
        }
        return tierID;
    }

    public void setPoweredByRedstone(boolean powered)
    {
        switch (redstoneMode)
        {
            case 0:
                active = !powered;
                break;
            case 1:
                active = powered;
                break;
            case 2:
                active = true;
                break;
            case 3:
                active = false;
                break;
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag)
    {
        super.toTag(tag);
        if (hasCustomName()) tag.putString("CustomName", Text.Serializer.toJson(getCustomName()));
        tag.putInt("XRange", xRange);
        tag.putInt("ZRange", zRange);
        tag.putInt("YRange", yRange);
        tag.putInt("Speed", speed);
        tag.putInt("RedstoneMode", redstoneMode);
        tag.putBoolean("Active", active);
        tag.putString("Owner", getOwner() == null ? "" : getOwner());
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag)
    {
        super.fromTag(tag);
        if (tag.containsKey("CustomName", 8)) setCustomName(Text.Serializer.fromJson(tag.getString("CustomName")));
        xRange = tag.getInt("XRange");
        zRange = tag.getInt("ZRange");
        yRange = tag.getInt("YRange");
        speed = tag.getInt("Speed");
        redstoneMode = tag.getInt("RedstoneMode");
        active = tag.getBoolean("Active");
        uuid = tag.getString("Owner");
    }
}
