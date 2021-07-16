package torcherino.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoAPI;
import torcherino.config.Config;
import torcherino.network.OpenScreenMessage;

import javax.annotation.Nullable;

import static torcherino.ModContent.TORCHERINO_TILE_ENTITY;

public class TorcherinoTileEntity extends BlockEntity implements Nameable, TickableBlockEntity
{
    private Component customName;
    private int xRange, yRange, zRange, speed, redstoneMode, randomTicks;
    private boolean active;
    private Iterable<BlockPos> area;
    private ResourceLocation tierName;

    public TorcherinoTileEntity() { super(TORCHERINO_TILE_ENTITY); }

    @Override @SuppressWarnings("ConstantConditions")
    public Component getName()
    { return hasCustomName() ? customName : new TranslatableComponent(level.getBlockState(worldPosition).getBlock().getDescriptionId()); }

    @Override
    public boolean hasCustomName() { return customName != null; }

    @Nullable @Override
    public Component getCustomName() { return customName; }

    public void setCustomName(@Nullable final Component name) { customName = name; }

    @SuppressWarnings("ConstantConditions")
    public ResourceLocation getTierName()
    {
        if (tierName == null)
        {
            final Block block = level.getBlockState(worldPosition).getBlock();
            if (block instanceof TierSupplier) { tierName = ((TierSupplier) block).getTierName(); }
        }
        return tierName;
    }

    public OpenScreenMessage createOpenMessage() { return new OpenScreenMessage(worldPosition, getName(), xRange, zRange, yRange, speed, redstoneMode); }

    @Override
    public void load(final BlockState state, final CompoundTag tag)
    {
        super.load(state, tag);
        if (tag.contains("CustomName", 8)) { setCustomName(Component.Serializer.fromJson(tag.getString("CustomName"))); }
        this.xRange = tag.getInt("XRange");
        this.zRange = tag.getInt("ZRange");
        this.yRange = tag.getInt("YRange");
        this.speed = tag.getInt("Speed");
        this.redstoneMode = tag.getInt("RedstoneMode");
    }

    @Override
    public CompoundTag save(final CompoundTag tag)
    {
        super.save(tag);
        if (hasCustomName()) { tag.putString("CustomName", Component.Serializer.toJson(getCustomName())); }
        tag.putInt("XRange", this.xRange);
        tag.putInt("ZRange", this.zRange);
        tag.putInt("YRange", this.yRange);
        tag.putInt("Speed", this.speed);
        tag.putInt("RedstoneMode", this.redstoneMode);
        return tag;
    }

    @SuppressWarnings("ConstantConditions")
    public void readClientData(final int xRange, final int zRange, final int yRange, final int speed, final int redstoneMode)
    {
        this.xRange = xRange;
        this.zRange = zRange;
        this.yRange = yRange;
        area = BlockPos.betweenClosed(worldPosition.getX() - xRange, worldPosition.getY() - yRange, worldPosition.getZ() - zRange, worldPosition.getX() + xRange, worldPosition.getY() + yRange,
                                      worldPosition.getZ() + zRange);
        this.speed = speed;
        this.redstoneMode = redstoneMode;
        final BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(BlockStateProperties.POWERED)) { setPoweredByRedstone(state.getValue(BlockStateProperties.POWERED)); }
        this.setChanged();
    }

    @Override @SuppressWarnings("ConstantConditions")
    public void tick()
    {
        if (level.isClientSide) { return; }
        if (!active || speed == 0 || (xRange == 0 && yRange == 0 && zRange == 0)) { return; }
        randomTicks = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        area.forEach(this::tickBlock);
    }

    @SuppressWarnings({ "ConstantConditions", "deprecation" })
    private void tickBlock(final BlockPos blockPos)
    {
        final BlockState blockState = level.getBlockState(blockPos);
        final Block block = blockState.getBlock();
        if (TorcherinoAPI.INSTANCE.isBlockBlacklisted(block)) { return; }
        if (block.isRandomlyTicking(blockState) &&
                level.getRandom().nextInt(Mth.clamp(4096 / (speed * Config.INSTANCE.random_tick_rate), 1, 4096)) < randomTicks)
        { block.randomTick(blockState, (ServerLevel) level, blockPos, level.getRandom()); }
        if (!block.hasTileEntity(blockState)) { return; }
        final BlockEntity tileEntity = level.getBlockEntity(blockPos);
        if (tileEntity == null || tileEntity.isRemoved() || TorcherinoAPI.INSTANCE.isTileEntityBlacklisted(tileEntity.getType()) ||
                !(tileEntity instanceof TickableBlockEntity)) { return; }
        for (int i = 0; i < speed; i++)
        {
            if (tileEntity.isRemoved()) { break; }
            ((TickableBlockEntity) tileEntity).tick();
        }
    }

    public void setPoweredByRedstone(final boolean powered)
    {
        if (redstoneMode == 0) { active = !powered; }
        else if (redstoneMode == 1) {active = powered; }
        else if (redstoneMode == 2) {active = true;}
        else if (redstoneMode == 3) { active = false; }
    }

    @Override @SuppressWarnings("ConstantConditions")
    public void onLoad()
    {
        super.onLoad();
        if (level.isClientSide) { return; }
        area = BlockPos.betweenClosed(worldPosition.getX() - xRange, worldPosition.getY() - yRange, worldPosition.getZ() - zRange, worldPosition.getX() + xRange, worldPosition.getY() + yRange,
                worldPosition.getZ() + zRange);
        level.getServer().tell(new TickTask(level.getServer().getTickCount(),
                () -> setPoweredByRedstone(level.getBlockState(worldPosition).getValue(BlockStateProperties.POWERED))));
    }
}
