package torcherino.block.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class SliderWidget extends ButtonWidget {
	double progress;

	SliderWidget(int int_1, int int_2, int int_3, int int_4, double double_1)
	{
		super(int_1, int_2, int_3, int_4, "");
		this.progress=double_1;
		this.updateText();
	}

	protected int getTextureId(boolean boolean_1) { return 0; }

	protected void drawBackground(MinecraftClient client, int int_1, int int_2) {
		if(this.visible)
		{
			client.getTextureManager().bindTexture(WIDGET_TEX);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedRect(this.x+(int) (progress*(double) (this.width-8)), this.y, 0, 66, 4, 20);
			this.drawTexturedRect(this.x+(int) (progress*(double) (this.width-8))+4, this.y, 196, 66, 4, 20);
		}
	}

	public final void onPressed(double ax, double ay) { this.changeProgress(ax); }

	public void setProgress(double double_1)
	{
		double double_2 = this.progress;
		this.progress = MathHelper.clamp(double_1, 0.0D, 1.0D);
		if (double_2 != this.progress) this.onProgressChanged();
		this.updateText();
	}

	private void changeProgress(double double_1)
	{
		double double_2 = this.progress;
		this.progress = MathHelper.clamp((double_1 - (double)(this.x + 4)) / (double)(this.width - 8), 0.0D, 1.0D);
		if (double_2 != this.progress) this.onProgressChanged();
		this.updateText();
	}

	protected void onDragged(double double_1, double double_2, double double_3, double double_4)
	{
		this.changeProgress(double_1);
		super.onDragged(double_1, double_2, double_3, double_4);
	}

	public void onReleased(double double_1, double double_2) { super.playPressedSound(MinecraftClient.getInstance().getSoundLoader()); }

	protected abstract void updateText();
	protected abstract void onProgressChanged();
}