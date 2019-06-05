package torcherino.blocks.misc;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import torcherino.Utils;

public class TorcherinoScreen extends GuiContainer
{

	private static final ResourceLocation BACKGROUND_TEXTURE = Utils.getId("textures/gui/container/torcherino.png");

	public TorcherinoScreen(TorcherinoTileEntity tileEntity)
	{
		super(new TorcherinoContainer(tileEntity));
	}

	@Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}
}
