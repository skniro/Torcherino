package torcherino;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.blocks.miscellaneous.TorcherinoContainer;
import torcherino.blocks.miscellaneous.TorcherinoTileEntity;
import torcherino.client.gui.TorcherinoScreen;

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
		NetworkHooks.openGui((EntityPlayerMP) player, torcherinoTileEntity, torcherinoTileEntity::writeClientData);
		return true;
	}

	public static GuiScreen openScreenClient(FMLPlayMessages.OpenContainer openContainer)
	{
		PacketBuffer additionalData = openContainer.getAdditionalData();
		BlockPos pos = additionalData.readBlockPos();
		TileEntity tile = Minecraft.getInstance().player.world.getTileEntity(pos);
		if (tile instanceof TorcherinoTileEntity)
		{
			ITextComponent containerName = additionalData.readTextComponent();
			TorcherinoContainer container = new TorcherinoContainer((TorcherinoTileEntity) tile, containerName);
			container.setXRange(additionalData.readInt());
			container.setZRange(additionalData.readInt());
			container.setYRange(additionalData.readInt());
			container.setSpeed(additionalData.readInt());
			container.setRedstoneMode(additionalData.readInt());
			return new TorcherinoScreen(container);
		}
		return null;
	}
}
