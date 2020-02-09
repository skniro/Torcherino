package torcherino.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.StringUtils;
import torcherino.Torcherino;
import torcherino.api.TierSupplier;
import torcherino.blocks.tile.TorcherinoTileEntity;
import torcherino.config.Config;
import torcherino.network.Networker;

import java.util.Random;

import static net.minecraft.state.properties.BlockStateProperties.POWERED;

public class LanterinoBlock extends LanternBlock implements TierSupplier
{
    private final ResourceLocation tierName;

    public LanterinoBlock(ResourceLocation tierName)
    {
        super(Block.Properties.from(Blocks.LANTERN));
        this.tierName = tierName;
    }

    @Override
    public ResourceLocation getTierName() { return tierName; }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TorcherinoTileEntity(); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(POWERED);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        if (!world.isRemote) Networker.INSTANCE.openScreenServer(world, (ServerPlayerEntity) player, pos);
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if (world.isRemote) return;
        if (stack.hasDisplayName())
        {
            TileEntity tile = world.getTileEntity(pos);
            if (!(tile instanceof TorcherinoTileEntity)) return;
            ((TorcherinoTileEntity) tile).setCustomName(stack.getDisplayName());
        }
        if (Config.INSTANCE.log_placement)
        {
            String prefix = "Something";
            if (placer != null) prefix = placer.getDisplayName().getString() + "(" + placer.getCachedUniqueIdString() + ")";
            Torcherino.LOGGER.info("[Torcherino] {} placed a {} at {} {} {}.", prefix,
                    StringUtils.capitalize(getTranslationKey().replace("block.torcherino.", "")
                                                              .replace("_", " ")), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TorcherinoTileEntity) ((TorcherinoTileEntity) tileEntity).tick();
    }

    @Override
    public PushReaction getPushReaction(BlockState state) { return PushReaction.IGNORE; }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean b)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TorcherinoTileEntity) ((TorcherinoTileEntity) tileEntity).setPoweredByRedstone(state.get(POWERED));
    }

    @Override
    public ResourceLocation getLootTable()
    {
        ResourceLocation registryName = getRegistryName();
        return new ResourceLocation(registryName.getNamespace(), "blocks/" + registryName.getPath());
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean b)
    {
        if (world.isRemote) return;
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TorcherinoTileEntity)
        {
            if (state == null) return;
            boolean powered;
            if (state.get(BlockStateProperties.HANGING).equals(true))
            {
                powered = world.isSidePowered(pos.up(), Direction.UP);
            }
            else
            {
                powered = isEmittingStrongRedstonePower(world, pos.west(), Direction.WEST) ||
                        isEmittingStrongRedstonePower(world, pos.east(), Direction.EAST) ||
                        isEmittingStrongRedstonePower(world, pos.south(), Direction.SOUTH) ||
                        isEmittingStrongRedstonePower(world, pos.north(), Direction.NORTH);
            }
            if (state.get(POWERED) != powered)
            {
                world.setBlockState(pos, state.with(POWERED, powered));
                ((TorcherinoTileEntity) tileEntity).setPoweredByRedstone(powered);
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        boolean powered;
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {state = getDefaultState(); }
        if (state.get(BlockStateProperties.HANGING).equals(true))
        {
            powered = context.getWorld().isSidePowered(context.getPos().up(), Direction.UP);
        }
        else
        {
            World world = context.getWorld();
            BlockPos pos = context.getPos();
            powered = isEmittingStrongRedstonePower(world, pos.west(), Direction.WEST) ||
                    isEmittingStrongRedstonePower(world, pos.east(), Direction.EAST) ||
                    isEmittingStrongRedstonePower(world, pos.south(), Direction.SOUTH) ||
                    isEmittingStrongRedstonePower(world, pos.north(), Direction.NORTH);
        }
        return state.with(POWERED, powered);
    }

    private static boolean isEmittingStrongRedstonePower(World world, BlockPos pos, Direction direction)
    {
        BlockState state = world.getBlockState(pos);
        return state.getStrongPower(world, pos, direction) > 0;
    }
}
