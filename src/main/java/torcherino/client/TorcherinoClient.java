package torcherino.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.network.FMLPlayMessages;
import torcherino.blocks.miscellaneous.TorcherinoContainer;
import torcherino.blocks.miscellaneous.TorcherinoTileEntity;
import torcherino.client.gui.TorcherinoScreen;

@OnlyIn(Dist.CLIENT)
public class TorcherinoClient
{

	public static void registerGUIExtensionPoint()
	{
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> TorcherinoClient::openScreenClient);
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
