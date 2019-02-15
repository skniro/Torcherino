package torcherino.block.screen;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SliderWidget extends ButtonWidget
{
	private AtomicDouble sliderPos;
	private boolean dragging;

	SliderWidget(int int_1, int int_2, int int_3, int int_4, int int_5, AtomicDouble double_1)
	{
		super(int_1, int_2, int_3, int_4, int_5, "");
		this.sliderPos = double_1;
	}

	protected int getTextureId(boolean boolean_1) { return 0; }

	protected void drawBackground(MinecraftClient client, int int_1, int int_2)
	{
		if (this.visible)
		{
			if (this.dragging)
			{
				this.sliderPos.set(MathHelper.clamp(((float)(int_1 - (this.x + 4)) / (float)(this.width - 8)), 0.0D, 1.0D));
			}

			client.getTextureManager().bindTexture(WIDGET_TEX);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedRect(this.x + (int)(this.sliderPos.get()* (double)(this.width - 8)), this.y, 0, 66, 4, 20);
			this.drawTexturedRect(this.x + (int)(this.sliderPos.get()* (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
		}
	}

	public final void onPressed(double ax, double ay)
	{
		this.sliderPos.set(MathHelper.clamp((ax - (double)(this.x + 4)) / (double)(this.width - 8), 0.0D, 1.0D));
		this.dragging = true;
	}

	public void onReleased(double double_1, double double_2) { this.dragging = false; }
}