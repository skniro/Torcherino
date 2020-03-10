package torcherino.api.blocks;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import torcherino.Torcherino;
import torcherino.api.TierSupplier;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;
import torcherino.config.Config;

import java.util.Random;

@SuppressWarnings({ "SpellCheckingInspection", "deprecation" })
public class WallTorcherinoBlock extends WallTorchBlock implements BlockEntityProvider, TierSupplier
{
    private final Identifier tierID;
    private final DefaultParticleType flameParticle;

    public WallTorcherinoBlock(Identifier tier, Identifier dropID)
    {
        super(FabricBlockSettings.copy(Blocks.WALL_TORCH).drops(dropID).build());
        this.tierID = tier;
        String path = tier.getPath() + "_flame";
        if (path.equals("normal_flame")) path = "flame";
        flameParticle = (DefaultParticleType) Registry.PARTICLE_TYPE.get(new Identifier(tier.getNamespace(), path));
    }

    @Override
    public Identifier getTier() { return tierID; }

    @Override
    public BlockEntity createBlockEntity(BlockView view) { return new TorcherinoBlockEntity(); }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) { return PistonBehavior.IGNORE; }

    @Override
    public void onBlockAdded(BlockState newState, World world, BlockPos pos, BlockState state, boolean boolean_1)
    {
        neighborUpdate(newState, world, pos, null, null, false);
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult)
    {
        if (world.isClient || hand == Hand.OFF_HAND) return true;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity)
        {
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            ((TorcherinoBlockEntity) blockEntity).writeClientData(buffer);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new Identifier(Torcherino.MOD_ID, "ots"), buffer);
        }
        return true;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1)
    {
        if (world.isClient) return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity)
        {
            ((TorcherinoBlockEntity) blockEntity).setPoweredByRedstone(world.isEmittingRedstonePower(
                    pos.offset(state.get(Properties.HORIZONTAL_FACING).getOpposite()), state.get(Properties.HORIZONTAL_FACING)));
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if (world.isClient) return;
        if (stack.hasCustomName())
        {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TorcherinoBlockEntity) ((TorcherinoBlockEntity) blockEntity).setCustomName(stack.getName());
        }
        if (Config.INSTANCE.log_placement)
        {
            String prefix = placer == null ? "Something" : placer.getDisplayName().getString() + "(" + placer.getUuidAsString() + ")";
            Torcherino.LOGGER.info("[Torcherino] {} placed a {} at {}, {}, {}.", prefix, Registry.BLOCK.getId(this), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rnd) {
        Direction direction = state.get(FACING).getOpposite();
        double d = pos.getX() + 0.5D;
        double e = pos.getY() + 0.92D;
        double f = pos.getZ() + 0.5D;
        world.addParticle(ParticleTypes.SMOKE, d + 0.27D * direction.getOffsetX(), e, f + 0.27D * direction.getOffsetZ(), 0, 0, 0);
        world.addParticle(flameParticle, d + 0.27D * direction.getOffsetX(), e, f + 0.27D * direction.getOffsetZ(), 0, 0, 0);
    }
}
