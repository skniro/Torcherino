package torcherino.api.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
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
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.config.Config;

public class TorcherinoBlockEntity extends BlockEntity implements Nameable, Tickable
{
    private Text customName;
    private int xRange, yRange, zRange, speed, redstoneMode, randomTicks;
    private Iterable<BlockPos> area;
    private boolean active, loaded = false;
    private Identifier tierID;

    public TorcherinoBlockEntity() { super(Registry.BLOCK_ENTITY.get(new Identifier("torcherino", "torcherino"))); }

    @Override
    public boolean hasCustomName() { return customName != null; }

    @Override
    public Text getCustomName() { return customName; }

    public void setCustomName(Text name) { customName = name; }

    @Override
    public Text getName() { return hasCustomName() ? customName : new TranslatableText(world.getBlockState(pos).getBlock().getTranslationKey()); }

    @Override
    public void tick()
    {
        if (!loaded)
        {
            area = BlockPos.iterate(pos.getX() - xRange, pos.getY() - yRange, pos.getZ() - zRange,
                    pos.getX() + xRange, pos.getY() + yRange, pos.getZ() + zRange);
            setPoweredByRedstone(world.getBlockState(pos).get(Properties.POWERED));
            loaded = true;
        }
        if (!active || speed == 0 || (xRange == 0 && yRange == 0 && zRange == 0)) return;
        randomTicks = world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
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

    private void writeClientData(PacketByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeText(getName());
        buffer.writeInt(xRange);
        buffer.writeInt(zRange);
        buffer.writeInt(yRange);
        buffer.writeInt(speed);
        buffer.writeInt(redstoneMode);
    }

    private void readClientData(PacketByteBuf buffer)
    {
        Tier tier = TorcherinoAPI.INSTANCE.getTiers().get(getTierID());
        this.xRange = MathHelper.clamp(buffer.readInt(), 0, tier.getXZRange());
        this.zRange = MathHelper.clamp(buffer.readInt(), 0, tier.getXZRange());
        this.yRange = MathHelper.clamp(buffer.readInt(), 0, tier.getYRange());
        this.speed = MathHelper.clamp(buffer.readInt(), 1, tier.getMaxSpeed());
        this.redstoneMode = MathHelper.clamp(buffer.readInt(), 0, 3);
    }

    public Identifier getTierID()
    {
        if (tierID == null)
        {
            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof LanterinoBlock) { tierID = ((LanterinoBlock) block).getTierID(); }
            else if (block instanceof TorcherinoBlock) { tierID = ((TorcherinoBlock) block).getTierID(); }
            else if (block instanceof WallTorcherinoBlock) tierID = ((WallTorcherinoBlock) block).getTierID();
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
    }
}
