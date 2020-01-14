package torcherino.blocks.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoAPI;
import torcherino.config.Config;
import torcherino.network.OpenScreenMessage;

import javax.annotation.Nullable;

import static torcherino.ModContent.TORCHERINO_TILE_ENTITY;

public class TorcherinoTileEntity extends TileEntity implements INameable, ITickableTileEntity
{
    private ITextComponent customName;
    private int xRange, yRange, zRange, speed, redstoneMode, randomTicks;
    private boolean active;
    private Iterable<BlockPos> area;
    private ResourceLocation tierName;

    public TorcherinoTileEntity() { super(TORCHERINO_TILE_ENTITY); }

    @Override
    public ITextComponent getName()
    {
        return hasCustomName() ? customName : new TranslationTextComponent(world.getBlockState(pos).getBlock().getTranslationKey());
    }

    @Override
    public boolean hasCustomName() { return customName != null; }

    @Nullable
    @Override
    public ITextComponent getCustomName() { return customName; }

    public void setCustomName(@Nullable ITextComponent name) { customName = name; }

    public ResourceLocation getTierName()
    {
        if (tierName == null)
        {
            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof TierSupplier) { tierName = ((TierSupplier) block).getTierName(); }
        }
        return tierName;
    }

    public OpenScreenMessage createOpenMessage() { return new OpenScreenMessage(pos, getName(), xRange, zRange, yRange, speed, redstoneMode); }

    public void read(CompoundNBT compound)
    {
        super.read(compound);
        if (compound.contains("CustomName", 8)) setCustomName(ITextComponent.Serializer.fromJson(compound.getString("CustomName")));
        this.xRange = compound.getInt("XRange");
        this.zRange = compound.getInt("ZRange");
        this.yRange = compound.getInt("YRange");
        this.speed = compound.getInt("Speed");
        this.redstoneMode = compound.getInt("RedstoneMode");
    }

    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        if (hasCustomName()) compound.putString("CustomName", ITextComponent.Serializer.toJson(getCustomName()));
        compound.putInt("XRange", this.xRange);
        compound.putInt("ZRange", this.zRange);
        compound.putInt("YRange", this.yRange);
        compound.putInt("Speed", this.speed);
        compound.putInt("RedstoneMode", this.redstoneMode);
        return compound;
    }

    public void readClientData(int xRange, int zRange, int yRange, int speed, int redstoneMode)
    {
        this.xRange = xRange;
        this.zRange = zRange;
        this.yRange = yRange;
        area = BlockPos.getAllInBoxMutable(pos.getX() - xRange, pos.getY() - yRange, pos.getZ() - zRange, pos.getX() + xRange, pos.getY() + yRange,
                pos.getZ() + zRange);
        this.speed = speed;
        this.redstoneMode = redstoneMode;
        BlockState state = world.getBlockState(pos);
        if (state.has(BlockStateProperties.POWERED)) setPoweredByRedstone(state.get(BlockStateProperties.POWERED));
        this.markDirty();
    }

    @Override
    public void tick()
    {
        if (world.isRemote) return;
        if (!active || speed == 0 || (xRange == 0 && yRange == 0 && zRange == 0)) return;
        randomTicks = world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
        area.forEach(this::tickBlock);
    }

    private void tickBlock(BlockPos blockPos)
    {
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (TorcherinoAPI.INSTANCE.isBlockBlacklisted(block)) return;
        if (block.ticksRandomly(blockState) &&
                world.getRandom().nextInt(MathHelper.clamp(4096 / (speed * Config.INSTANCE.random_tick_rate), 1, 4096)) < randomTicks)
        { block.randomTick(blockState, (ServerWorld) world, blockPos, world.getRandom()); }
        if (!block.hasTileEntity(blockState)) return;
        TileEntity tileEntity = world.getTileEntity(blockPos);
        if (tileEntity == null || tileEntity.isRemoved() || TorcherinoAPI.INSTANCE.isTileEntityBlacklisted(tileEntity.getType()) ||
                !(tileEntity instanceof ITickableTileEntity)) { return; }
        for (int i = 0; i < speed; i++)
        {
            if (tileEntity.isRemoved()) break;
            ((ITickableTileEntity) tileEntity).tick();
        }
    }

    public void setPoweredByRedstone(boolean powered)
    {
        switch (redstoneMode)
        {
            case 0:
                this.active = !powered;
                break;
            case 1:
                this.active = powered;
                break;
            case 2:
                this.active = true;
                break;
            case 3:
                this.active = false;
                break;
        }
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (world.isRemote) return;
        area = BlockPos.getAllInBoxMutable(pos.getX() - xRange, pos.getY() - yRange, pos.getZ() - zRange,
                pos.getX() + xRange, pos.getY() + yRange, pos.getZ() + zRange);
        world.getServer().enqueue(new TickDelayedTask(world.getServer().getTickCounter(),
                () -> setPoweredByRedstone(world.getBlockState(pos).get(BlockStateProperties.POWERED))));
    }
}
