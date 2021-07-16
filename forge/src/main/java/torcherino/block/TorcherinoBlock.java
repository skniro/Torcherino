package torcherino.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.StringUtils;
import torcherino.Torcherino;
import torcherino.api.TierSupplier;
import torcherino.block.tile.TorcherinoTileEntity;
import torcherino.config.Config;
import torcherino.network.Networker;

import javax.annotation.Nullable;
import java.util.Random;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

public class TorcherinoBlock extends TorchBlock implements TierSupplier
{
    private final ResourceLocation tierName;

    public TorcherinoBlock(final ResourceLocation tierName, final ParticleOptions flameParticle)
    {
        super(Properties.copy(Blocks.TORCH), flameParticle);
        this.tierName = tierName;
    }

    @Override
    public ResourceLocation getTierName() { return tierName; }

    @Override
    public boolean hasTileEntity(final BlockState state) { return true; }

    @Override
    public BlockEntity createTileEntity(final BlockState state, final BlockGetter world) { return new TorcherinoTileEntity(); }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override @SuppressWarnings("deprecation")
    public InteractionResult use(final BlockState state, final Level world, final BlockPos pos, final Player player, final InteractionHand hand,
                                 final BlockHitResult hit)
    {
        if (!world.isClientSide) { Networker.INSTANCE.openScreenServer(world, (ServerPlayer) player, pos); }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(final Level world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack stack)
    {
        if (world.isClientSide) { return; }
        if (stack.hasCustomHoverName())
        {
            final BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TorcherinoTileEntity) { ((TorcherinoTileEntity) tile).setCustomName(stack.getDisplayName()); }
        }
        if (Config.INSTANCE.log_placement)
        {
            String prefix = "Something";
            if (placer != null) { prefix = placer.getDisplayName().getString() + "(" + placer.getStringUUID() + ")"; }
            Torcherino.LOGGER.info("[Torcherino] {} placed a {} at {} {} {}.", prefix,
                    StringUtils.capitalize(getDescriptionId().replace("block.torcherino.", "").replace("_", " ")), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override @SuppressWarnings("deprecation")
    public void tick(final BlockState state, final ServerLevel world, final BlockPos pos, final Random random)
    {
        final BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TorcherinoTileEntity) { ((TorcherinoTileEntity) tileEntity).tick(); }
    }

    @Override @SuppressWarnings("deprecation")
    public PushReaction getPistonPushReaction(final BlockState state) { return PushReaction.IGNORE; }

    @Override @SuppressWarnings("deprecation")
    public void onPlace(final BlockState state, final Level world, final BlockPos pos, final BlockState oldState, final boolean b)
    {
        final BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TorcherinoTileEntity) { ((TorcherinoTileEntity) tileEntity).setPoweredByRedstone(state.getValue(POWERED)); }
    }

    @Override @SuppressWarnings("ConstantConditions")
    public BlockState getStateForPlacement(final BlockPlaceContext context)
    { return super.getStateForPlacement(context).setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos().below())); }

    @Override @SuppressWarnings("deprecation")
    public void neighborChanged(final BlockState state, final Level world, final BlockPos pos, final Block block, final BlockPos fromPos, final boolean b)
    {
        if (world.isClientSide) { return; }
        final boolean powered = world.hasNeighborSignal(pos.below());
        if (state.getValue(POWERED) != powered)
        {
            world.setBlockAndUpdate(pos, state.setValue(POWERED, powered));
            final BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof TorcherinoTileEntity) { ((TorcherinoTileEntity) tileEntity).setPoweredByRedstone(powered); }
        }
    }
}
