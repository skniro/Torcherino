package torcherino.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import torcherino.Utilities;
import torcherino.api.blocks.TorcherinoTileEntity;

public class Networker
{
	public static final Networker INSTANCE = new Networker();

	public SimpleChannel torcherinoChannel;

	public void initialise()
	{
		int id = 0;
		torcherinoChannel = NetworkRegistry.newSimpleChannel(Utilities.resloc("channel"), () -> "1", version -> version.equals("1"), version -> version.equals("1"));
		torcherinoChannel.registerMessage(id++, ValueUpdateMessage.class, ValueUpdateMessage::encode, ValueUpdateMessage::decode, ValueUpdateMessage::handle);
		torcherinoChannel.registerMessage(id++, OpenScreenMessage.class, OpenScreenMessage::encode, OpenScreenMessage::decode, OpenScreenMessage::handle);
	}

	public boolean openScreenServer(World world, EntityPlayer player, BlockPos pos)
	{
		if (world.isRemote) return true;
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TorcherinoTileEntity)) return true;
		TorcherinoTileEntity tileEntity = (TorcherinoTileEntity) tile;
		torcherinoChannel.sendTo(new OpenScreenMessage(tileEntity.getPos(), tileEntity.getName(), tileEntity.getxRange(), tileEntity.getzRange(), tileEntity.getyRange(), tileEntity.getSpeed(), tileEntity.getRedstoneMode()), ((EntityPlayerMP) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		return true;
	}
}
