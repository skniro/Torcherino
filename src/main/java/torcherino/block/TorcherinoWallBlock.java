package torcherino.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorchWall;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import torcherino.Utils;
import torcherino.block.tile.TorcherinoTileEntity;
import java.util.Random;

public class TorcherinoWallBlock extends BlockTorchWall implements ITileEntityProvider
{
    private int maxSpeed;
    TorcherinoWallBlock(int speed)
    {
        super(Block.Properties.create(Material.CIRCUITS).doesNotBlockMovement().zeroHardnessAndResistance().lightValue(14).sound(SoundType.WOOD));
        maxSpeed = speed;
    }

    @Override
    public void neighborChanged(IBlockState selfState, World world, BlockPos selfPos, Block neighborBlock, BlockPos neighborPos)
    {
        if (world.isRemote) return;
        TileEntity tileEntity = world.getTileEntity(selfPos);
        if (tileEntity == null) return;
        EnumFacing oppositeFacing = selfState.get(HORIZONTAL_FACING).getOpposite();
        ((TorcherinoTileEntity) tileEntity).setPoweredByRedstone(world.isSidePowered(selfPos.offset(oppositeFacing), oppositeFacing));
    }

    @Override
    public void onBlockAdded(IBlockState state, World world, BlockPos blockPos, IBlockState oldState)
    {
        this.neighborChanged(state, world, blockPos, null, null);
    }

    @Override
    public EnumPushReaction getPushReaction(IBlockState state)
    {
        return EnumPushReaction.IGNORE;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader blockReader)
    {
        return new TorcherinoTileEntity(maxSpeed);
    }

    @Override
    public void tick(IBlockState blockState, World world, BlockPos pos, Random rand)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof ITickable) ((ITickable) tileEntity).tick();
    }

    @Override
    public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState state1, boolean b)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity != null) tileEntity.remove();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos blockPos, IBlockState oldState, EntityLivingBase placingEntity, ItemStack handItemStack)
    {
        if(world.isRemote) return;
        String prefix = "Something";
        if(placingEntity != null) prefix = placingEntity.getDisplayName().getString() + "(" + placingEntity.getCachedUniqueIdString() + ")";
        Utils.logger.info("[Torcherino] {} placed a {} at {} {} {}.", prefix, StringUtils.capitalize(getTranslationKey().replace("block.torcherino.", "").replace("_", " ")), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public boolean onBlockActivated(IBlockState blockState, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float relX, float relY, float relZ)
    {
        if (world.isRemote) return true;
        if (hand == EnumHand.OFF_HAND) return true;
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TorcherinoTileEntity)) return true;
        TorcherinoTileEntity torch = (TorcherinoTileEntity) tile;
        torch.changeMode(player.isSneaking());
        player.sendStatusMessage(torch.getDescription(), true);
        return true;
    }
}
