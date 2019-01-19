package torcherino.block;

import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import torcherino.block.entity.TorcherinoBlockEntity;

import java.util.Random;

public class TorcherinoWallBlock extends WallTorchBlock implements BlockEntityProvider
{
    private int maxSpeed;
    TorcherinoWallBlock(int speed, Block TorchBlock)
    {
        super(FabricBlockSettings.of(Material.PART).lightLevel(14).noCollision().sounds(BlockSoundGroup.WOOD).breakInstantly().dropsLike(TorchBlock).build());
        maxSpeed = speed;
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult hitResult)
    {
        if(hand == Hand.OFF) return true;
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if(!(blockEntity instanceof TorcherinoBlockEntity)) return true;
        if(!world.isClient)
        {
            TorcherinoBlockEntity torch = (TorcherinoBlockEntity) blockEntity;
            torch.changeMode(playerEntity.isSneaking());
            playerEntity.addChatMessage(torch.getDescription(), true);
        }
        return true;
    }

    public BlockEntity createBlockEntity(BlockView blockView)
    {
        return new TorcherinoBlockEntity(maxSpeed);
    }

    @Override
    public void scheduledTick(BlockState blockState, World world, BlockPos pos, Random rand)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof Tickable) ((Tickable) blockEntity).tick();
    }

    @Override
    public void onBlockRemoved(BlockState blockState1, World world, BlockPos blockPos, BlockState blockState2, boolean bool)
    {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if(blockEntity != null) blockEntity.invalidate();
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState blockState_1)
    {
        return PistonBehavior.IGNORE;
    }
}
