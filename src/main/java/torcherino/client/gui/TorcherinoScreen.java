package torcherino.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MathUtil;
import torcherino.Utilities;
import torcherino.blocks.miscellaneous.TorcherinoContainer;
import torcherino.blocks.miscellaneous.TorcherinoTileEntity;
import torcherino.client.gui.widgets.FixedSliderButton;

@OnlyIn(Dist.CLIENT)
public class TorcherinoScreen extends GuiContainer
{
	private static final ResourceLocation BACKGROUND_TEXTURE = Utilities.resloc("textures/gui/container/torcherino.png");
	private final TorcherinoContainer container;
	private final TorcherinoTileEntity tileEntity;

	public TorcherinoScreen(TorcherinoTileEntity tileEntity)
	{
		super(new TorcherinoContainer(tileEntity));
		container = (TorcherinoContainer) inventorySlots;
		this.tileEntity = tileEntity;
		xSize = 256;
		ySize = 133;
	}

	@Override protected void initGui()
	{
		super.initGui();
		int buttonId = 0;
		this.addButton(new FixedSliderButton(buttonId++, guiLeft + xSize/2 - 100, guiTop + 20, 205)
		{
			int speed;
			double MAX_SPEED;
			@Override protected void initialise()
			{
				speed = TorcherinoScreen.this.tileEntity.getSpeed();
				MAX_SPEED = TorcherinoScreen.this.tileEntity.getTier().MAX_SPEED;
				this.progress = speed / MAX_SPEED;
				this.displayString = new TextComponentTranslation("gui.torcherino.speed_slider", 100 * speed).getFormattedText();
			}

			@Override protected void onValueChange()
			{
				speed = (int) Math.round(progress * MAX_SPEED);
				this.progress = speed / MAX_SPEED;
				this.displayString = new TextComponentTranslation("gui.torcherino.speed_slider", 100 * speed).getFormattedText();
			}
		});

		this.addButton(new FixedSliderButton(buttonId++, guiLeft + xSize/2 - 100, guiTop + 45, 205)
		{
			int xRange;
			int XZ_RANGE;
			@Override protected void initialise()
			{
				xRange = TorcherinoScreen.this.tileEntity.getxRange();
				XZ_RANGE = TorcherinoScreen.this.tileEntity.getTier().XZ_RANGE;
				this.progress = (double) xRange / XZ_RANGE;
				this.displayString = new TextComponentTranslation("gui.torcherino.x_range", 1+2*xRange).getFormattedText();
			}

			@Override protected void onValueChange()
			{
				xRange = (int) Math.round(progress * XZ_RANGE);
				this.progress = (double) xRange / XZ_RANGE;
				this.displayString = new TextComponentTranslation("gui.torcherino.x_range", 1+2*xRange).getFormattedText();
			}
		});

		this.addButton(new FixedSliderButton(buttonId++, guiLeft + xSize/2 - 100, guiTop + 70, 205)
		{
			int zRange;
			int XZ_RANGE;
			@Override protected void initialise()
			{
				zRange = TorcherinoScreen.this.tileEntity.getzRange();
				XZ_RANGE = TorcherinoScreen.this.tileEntity.getTier().XZ_RANGE;
				this.progress = (double) zRange / XZ_RANGE;
				this.displayString = new TextComponentTranslation("gui.torcherino.z_range", 1+2*zRange).getFormattedText();
			}

			@Override protected void onValueChange()
			{
				zRange = (int) Math.round(progress * XZ_RANGE);
				this.progress = (double) zRange / XZ_RANGE;
				this.displayString = new TextComponentTranslation("gui.torcherino.z_range", 1+2*zRange).getFormattedText();
			}
		});

		this.addButton(new FixedSliderButton(buttonId++, guiLeft + xSize/2 - 100, guiTop + 95, 205)
		{
			int yRange;
			int Y_RANGE;
			@Override protected void initialise()
			{
				yRange = TorcherinoScreen.this.tileEntity.getyRange();
				Y_RANGE = TorcherinoScreen.this.tileEntity.getTier().Y_RANGE;
				this.progress = (double) yRange / Y_RANGE;
				this.displayString = new TextComponentTranslation("gui.torcherino.y_range", 1+2*yRange).getFormattedText();
			}

			@Override protected void onValueChange()
			{
				yRange = (int) Math.round(progress * Y_RANGE);
				this.progress = (double) yRange / Y_RANGE;
				this.displayString = new TextComponentTranslation("gui.torcherino.y_range", 1+2*yRange).getFormattedText();
			}
		});
	}

	public void render(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		super.render(mouseX, mouseY, partialTicks);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRenderer.drawString(container.getTileEntity().getName().getFormattedText(), 8.0F, 6.0F, 4210752);
		//this.fontRenderer.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
	}

	@Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}

	@Override public void onResize(Minecraft mcIn, int w, int h)
	{
		super.onResize(mcIn, w, h);
	}

	@Override public void onGuiClosed()
	{
		super.onGuiClosed();
	}
}
