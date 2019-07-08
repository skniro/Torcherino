package torcherino.api.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import torcherino.Torcherino;
import torcherino.config.Config;
import torcherino.network.Networker;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("deprecation")
public class TorcherinoBlock extends TorchBlock
{
    // Variables
    private static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private final ResourceLocation tierName;
    // Constructors
    public TorcherinoBlock(ResourceLocation tierName)
    {
        super(Properties.from(Blocks.TORCH));
        this.tierName = tierName;
    }

    // Methods
    public ResourceLocation getTierName() { return tierName; }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TorcherinoTileEntity(); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(POWERED);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        if (world.isRemote) return true;
        return Networker.INSTANCE.openScreenServer(world, (ServerPlayerEntity) player, pos);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
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
                    StringUtils.capitalize(getTranslationKey().replace("block.torcherino.", "").replace("_", " ")), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public void tick(BlockState state, World world, BlockPos pos, Random random)
    {
        if (world.isRemote) return;
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

    // Unique Methods ( can't be copy / pasted between torcherino classes )
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        boolean powered = context.getWorld().isBlockPowered(context.getPos().down());
        return getDefaultState().with(POWERED, powered);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean b)
    {
        if (world.isRemote) return;
        boolean powered = world.isBlockPowered(pos.down());
        if (state.get(POWERED) != powered)
        {
            world.setBlockState(pos, state.with(POWERED, powered));
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TorcherinoTileEntity)
            {
                ((TorcherinoTileEntity) tileEntity).setPoweredByRedstone(powered);
            }
        }
    }
}
