package torcherino.blocks.miscellaneous;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import torcherino.Utilities;

@OnlyIn(Dist.CLIENT)
public class TorcherinoScreen extends GuiContainer
{
	private static final ResourceLocation BACKGROUND_TEXTURE = Utilities.resloc("textures/gui/container/torcherino.png");
	private final TorcherinoContainer container;

	public TorcherinoScreen(TorcherinoTileEntity tileEntity)
	{
		super(new TorcherinoContainer(tileEntity));
		container = (TorcherinoContainer) inventorySlots;
		xSize = 256;
		ySize = 88;
	}

	@Override protected void initGui()
	{
		super.initGui();
		//this.addButton(new SliderButton(0, guiLeft, guiTop)
		//{
		//	@Override protected void initialise()
		//	{
		//		this.progress = 0;
		//		this.displayString = new TextComponentTranslation("gui.torcherino.speed_slider", this.progress).getFormattedText();
		//	}
		//
		//	@Override protected void onValueChange()
		//	{
		//		this.displayString = new TextComponentTranslation("gui.torcherino.speed_slider", this.progress).getFormattedText();
		//	}
		//});
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
