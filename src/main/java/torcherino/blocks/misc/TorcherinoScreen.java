package torcherino.blocks.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import torcherino.Utils;

@OnlyIn(Dist.CLIENT)
public class TorcherinoScreen extends GuiContainer
{
	private static final ResourceLocation BACKGROUND_TEXTURE = Utils.getId("textures/gui/container/torcherino.png");
	private TorcherinoContainer container;

	public TorcherinoScreen(TorcherinoTileEntity tileEntity)
	{
		super(new TorcherinoContainer(tileEntity));
		container = (TorcherinoContainer) inventorySlots;
		System.out.println(tileEntity.getName().toString());
		xSize = 256;
		ySize = 88;
	}

	@Override protected void initGui()
	{
		super.initGui();
		this.addButton(new SliderButton(0, guiLeft, guiTop)
		{
			@Override protected void initialise()
			{
				this.progress = 0;
				this.displayString = new TextComponentTranslation("gui.torcherino.speed_slider", this.progress).getFormattedText();
			}

			@Override protected void onValueChange()
			{
				this.displayString = new TextComponentTranslation("gui.torcherino.speed_slider", this.progress).getFormattedText();
			}
		});
	}

	@Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}

	@OnlyIn(Dist.CLIENT)
	abstract class SliderButton extends GuiButton
	{
		private double oldProgress;
		public double progress;
		public boolean pressed;

		public SliderButton(int buttonId, int x, int y)
		{
			super(buttonId, x, y, 200, 20, "");
			this.initialise();
		}

		protected abstract void initialise();

		protected int getHoverState(boolean mouseOver)
		{
			return 0;
		}

		protected void renderBg(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				if (this.pressed)
				{
					this.oldProgress = progress;
					this.progress = (double) ((float) (mouseX - (this.x + 4)) / (float) (this.width - 8));
					this.checkValueChange();
					// update text
				}
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.drawTexturedModalRect(this.x + (int) (this.progress * (double) (this.width - 8)), this.y, 0, 66, 4, 20);
				this.drawTexturedModalRect(this.x + (int) (this.progress * (double) (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
			}
		}

		private void checkValueChange()
		{
			this.progress = MathHelper.clamp(this.progress, 0.0D, 1.0D);
			if(oldProgress != progress)
			{
				this.onValueChange();
			}
		}

		public void onClick(double mouseX, double mouseY)
		{
			this.oldProgress = progress;
			this.progress = (mouseX - (double) (this.x + 4)) / (double) (this.width - 8);
			this.checkValueChange();
			this.pressed = true;
		}

		@Override public void playPressSound(SoundHandler soundHandlerIn){ }

		public void onRelease(double mouseX, double mouseY)
		{
			if (this.pressed)
			{
				// play pressed sound.
				TorcherinoScreen.this.mc.getSoundHandler().play(SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			}
			this.pressed = false;
		}

		protected abstract void onValueChange();
	}
}
