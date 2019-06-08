package torcherino.blocks;

import net.minecraft.block.BlockCarvedPumpkin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import torcherino.blocks.miscellaneous.TorcherinoTileEntity;
import javax.annotation.Nullable;

public class LanterinoBlock extends BlockCarvedPumpkin
{
	public LanterinoBlock(){ super(Properties.from(Blocks.JACK_O_LANTERN)); }

	@Override public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Nullable @Override public TileEntity createTileEntity(IBlockState state, IBlockReader world)
	{
		return new TorcherinoTileEntity();
	}

	@Override public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote || hand == EnumHand.OFF_HAND) return true;
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TorcherinoTileEntity)) return true;
		NetworkHooks.openGui((EntityPlayerMP) player, (TorcherinoTileEntity) tile, pos);
		return true;
	}
}
