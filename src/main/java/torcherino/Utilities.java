package torcherino;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.blocks.miscellaneous.TorcherinoTileEntity;
import torcherino.network.Networker;
import torcherino.network.OpenScreenMessage;

public class Utilities
{
	public static final Logger LOGGER = LogManager.getLogger(Torcherino.class);
	public static final String MOD_ID = "torcherino";

	public static ResourceLocation resloc(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}

	public static boolean openScreenServer(World world, EntityPlayer player, BlockPos pos)
	{
		if (world.isRemote) return true;
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TorcherinoTileEntity)) return true;
		TorcherinoTileEntity torcherinoTileEntity = (TorcherinoTileEntity) tile;
		Networker.INSTANCE.torcherinoChannel.sendToServer(new OpenScreenMessage(torcherinoTileEntity.getPos(), torcherinoTileEntity.getName(), torcherinoTileEntity.getxRange(), torcherinoTileEntity.getzRange(), torcherinoTileEntity.getyRange(), torcherinoTileEntity.getSpeed(), torcherinoTileEntity.getRedstoneMode()));
		return true;
	}
}
