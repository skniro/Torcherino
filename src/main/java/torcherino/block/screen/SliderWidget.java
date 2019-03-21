package torcherino.block.screen;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.MathHelper;

public abstract class SliderWidget extends net.minecraft.client.gui.widget.SliderWidget
{
	private double arrowNudgeAmount = 1;
	String narrationMessage = "";

	SliderWidget(int int_1, int int_2, int int_3, int int_4, double double_1, int permutations)
	{
		super( int_1,  int_2,  int_3,  int_4,  double_1);
		this.arrowNudgeAmount /= permutations;
		this.updateText();
	}

	@Override public boolean keyPressed(int key, int scanCode, int modifierBits)
	{
		double old_progress;
		if (key == 263)
		{
			old_progress = this.progress;
			this.progress = MathHelper.clamp(this.progress - this.arrowNudgeAmount, 0.0D, 1.0D);
			if (old_progress != this.progress)
			{
				this.onProgressChanged();
				this.updateText();
			}
		}
		else if (key == 262)
		{
			old_progress = this.progress;
			this.progress = MathHelper.clamp(this.progress + this.arrowNudgeAmount, 0.0D, 1.0D);
			if (old_progress != this.progress)
			{
				this.onProgressChanged();
				this.updateText();
			}
		}

		return false;
	}

	@Override protected String getNarrationMessage()
	{
		if(!narrationMessage.equals("")) return I18n.translate("gui.narrate.slider", narrationMessage);
		return I18n.translate("gui.narrate.slider", this.getMessage());
	}
}
