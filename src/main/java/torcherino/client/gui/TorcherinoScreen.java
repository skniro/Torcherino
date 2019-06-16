package torcherino.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import torcherino.Utilities;
import torcherino.blocks.miscellaneous.TorcherinoContainer;
import torcherino.client.gui.widgets.FixedSliderButton;
import torcherino.client.gui.widgets.StateButton;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TorcherinoScreen extends GuiContainer
{
	private static final ResourceLocation BACKGROUND_TEXTURE = Utilities.resloc("textures/gui/container/torcherino.png");
	private final TorcherinoContainer container;

	public TorcherinoScreen(TorcherinoContainer torcherinoContainer)
	{
		super(torcherinoContainer);
		container = torcherinoContainer;
		xSize = 245;
		ySize = 123;
	}

	@Override protected void initGui()
	{
		super.initGui();
		int buttonId = 0;
		this.addButton(new FixedSliderButton(buttonId++, guiLeft + 8, guiTop + 20, 205)
		{
			int speed;
			double MAX_SPEED;

			@Override protected void initialise()
			{
				speed = container.getSpeed();
				MAX_SPEED = container.getMaxSpeed();
				this.progress = speed / MAX_SPEED;
				this.displayString = new TextComponentTranslation("gui.torcherino.speed_slider", 100 * speed).getFormattedText();
			}

			@Override protected void onValueChange()
			{
				speed = (int) Math.round(progress * MAX_SPEED);
				container.setSpeed(speed);
				this.progress = speed / MAX_SPEED;
				this.displayString = new TextComponentTranslation("gui.torcherino.speed_slider", 100 * speed).getFormattedText();
			}
		});
		this.addButton(new FixedSliderButton(buttonId++, guiLeft + 8, guiTop + 45, 205)
		{
			int xRange;
			int XZ_RANGE;

			@Override protected void initialise()
			{
				xRange = container.getXRange();
				XZ_RANGE = container.getMaxXZRange();
				this.progress = (double) xRange / XZ_RANGE;
				this.displayString = new TextComponentTranslation("gui.torcherino.x_range", 1 + 2 * xRange).getFormattedText();
			}

			@Override protected void onValueChange()
			{
				xRange = (int) Math.round(progress * XZ_RANGE);
				container.setXRange(xRange);
				this.progress = (double) xRange / XZ_RANGE;
				this.displayString = new TextComponentTranslation("gui.torcherino.x_range", 1 + 2 * xRange).getFormattedText();
			}
		});
		this.addButton(new FixedSliderButton(buttonId++, guiLeft + 8, guiTop + 70, 205)
		{
			int zRange;
			int XZ_RANGE;

			@Override protected void initialise()
			{
				zRange = container.getZRange();
				XZ_RANGE = container.getMaxXZRange();
				this.progress = (double) zRange / XZ_RANGE;
				this.displayString = new TextComponentTranslation("gui.torcherino.z_range", 1 + 2 * zRange).getFormattedText();
			}

			@Override protected void onValueChange()
			{
				zRange = (int) Math.round(progress * XZ_RANGE);
				container.setZRange(zRange);
				this.progress = (double) zRange / XZ_RANGE;
				this.displayString = new TextComponentTranslation("gui.torcherino.z_range", 1 + 2 * zRange).getFormattedText();
			}
		});
		this.addButton(new FixedSliderButton(buttonId++, guiLeft + 8, guiTop + 95, 205)
		{
			int yRange;
			int Y_RANGE;

			@Override protected void initialise()
			{
				yRange = container.getYRange();
				Y_RANGE = container.getMaxYRange();
				this.progress = (double) yRange / Y_RANGE;
				this.displayString = new TextComponentTranslation("gui.torcherino.y_range", 1 + 2 * yRange).getFormattedText();
			}

			@Override protected void onValueChange()
			{
				yRange = (int) Math.round(progress * Y_RANGE);
				container.setYRange(yRange);
				this.progress = (double) yRange / Y_RANGE;
				this.displayString = new TextComponentTranslation("gui.torcherino.y_range", 1 + 2 * yRange).getFormattedText();
			}
		});
		this.addButton(new StateButton(buttonId++, guiLeft + 217, guiTop + 20, container.getRedstoneMode(), this)
		{
			ItemStack renderStack;
			List<ITextComponent> tooltip;

			@Override protected void setState(int state)
			{
				tooltip = new ArrayList<>();
				TextComponentTranslation modeTranslationKey;
				switch (state)
				{
					case 0:
						renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "redstone")));
						modeTranslationKey = new TextComponentTranslation("gui.torcherino.mode.normal");
						break;
					case 1:
						renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "redstone_torch")));
						modeTranslationKey = new TextComponentTranslation("gui.torcherino.mode.inverted");
						break;
					case 2:
						renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "gunpowder")));
						modeTranslationKey = new TextComponentTranslation("gui.torcherino.mode.ignored");
						break;
					case 3:
						renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "redstone_lamp")));
						modeTranslationKey = new TextComponentTranslation("gui.torcherino.mode.off");
						break;
					default:
						renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "furnace")));
						modeTranslationKey = new TextComponentTranslation("gui.torcherino.mode.error");
						break;
				}
				tooltip.add(0, new TextComponentTranslation("gui.torcherino.redstone_mode", modeTranslationKey));
				container.setRedstoneMode(state);
			}

			@Override protected int getMaxStates()
			{
				return 4;
			}

			@Override protected ItemStack getButtonIcon()
			{
				return renderStack;
			}

			@Override protected List<ITextComponent> populateToolTip()
			{
				return tooltip;
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
		String text = container.getDisplayName().getFormattedText();
		fontRenderer.drawString(text, (xSize - fontRenderer.getStringWidth(text)) / 2, 6, 4210752);
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
