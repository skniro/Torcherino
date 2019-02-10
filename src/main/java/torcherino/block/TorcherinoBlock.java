package torcherino.block;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import torcherino.Utils;
import torcherino.block.entity.TorcherinoBlockEntity;
import java.util.Random;

public class TorcherinoBlock extends TorchBlock implements BlockEntityProvider
{
    private int maxSpeed;
    TorcherinoBlock(int speed, Identifier id)
    {
        super(FabricBlockSettings.of(Material.PART).lightLevel(14).noCollision().sounds(BlockSoundGroup.WOOD).breakInstantly().drops(id).build());
        maxSpeed = speed;
    }

    @Override
    public void neighborUpdate(BlockState selfState, World world, BlockPos selfPos, Block neighborBlock, BlockPos neighborPos)
    {
        if (world.isClient) return;
        BlockEntity blockEntity = world.getBlockEntity(selfPos);
        if (blockEntity == null) return;
        ((TorcherinoBlockEntity) blockEntity).setPoweredByRedstone(world.isEmittingRedstonePower(selfPos.down(), Direction.DOWN));
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState oldState)
    {
        this.neighborUpdate(blockState, world, blockPos, null, null);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState blockState)
    {
        return PistonBehavior.IGNORE;
    }

    public BlockEntity createBlockEntity(BlockView blockView)
    {
        return new TorcherinoBlockEntity(maxSpeed);
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos pos, Random rand)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof Tickable) ((Tickable) blockEntity).tick();
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState newBlockState, boolean bool)
    {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if(blockEntity != null) blockEntity.invalidate();
    }

    @Override
    public void onPlaced(World world, BlockPos blockPos, BlockState oldState, LivingEntity placingEntity, ItemStack handItemStack)
    {
        if(world.isClient) return;
        String prefix = "Something";
        if(placingEntity != null) prefix = placingEntity.getDisplayName().getText() + "(" + placingEntity.getUuidAsString() + ")";
        Utils.logger.info("[Torcherino] {} placed a {} at {} {} {}.", prefix, StringUtils.capitalize(getTranslationKey().replace("block.torcherino.", "").replace("_", " ")), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult hitResult)
    {
        if(world.isClient) return true;
        if(hand == Hand.OFF) return true;
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if(!(blockEntity instanceof TorcherinoBlockEntity)) return true;
        TorcherinoBlockEntity torch = (TorcherinoBlockEntity) blockEntity;
        torch.changeMode(Utils.keyStates.getOrDefault(playerEntity, false));
        playerEntity.addChatMessage(torch.getDescription(), true);
        return true;
    }
}
