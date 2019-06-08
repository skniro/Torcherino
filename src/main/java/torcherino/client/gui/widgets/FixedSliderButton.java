package torcherino.client.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;

public abstract class FixedSliderButton extends GuiButton
{
	private double oldProgress;
	public double progress;
	public boolean pressed;

	public FixedSliderButton(int buttonId, int x, int y, int width)
	{
		super(buttonId, x, y, width, 20, "");
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
		if (oldProgress != progress)
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
			Minecraft.getInstance().getSoundHandler().play(SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			this.pressed = false;
		}
	}

	protected abstract void onValueChange();
}