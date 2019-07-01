package torcherino.client.screen.buttons;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public abstract class SliderButton extends net.minecraft.client.gui.widget.SliderWidget
{
	private double arrowNudgeAmount = 1;
	protected String narrationMessage = "";

	protected SliderButton(int int_1, int int_2, int int_3, int int_4, double double_1, int permutations)
	{
		super(int_1, int_2, int_3, int_4, double_1);
		this.arrowNudgeAmount /= permutations;
		this.updateMessage();
	}

	@Override public boolean keyPressed(int key, int scanCode, int modifierBits)
	{
		if (key == 263)
		{
			double old_progress = this.value;
			this.value = MathHelper.clamp(this.value - this.arrowNudgeAmount, 0.0D, 1.0D);
			if (old_progress != this.value)
			{
				this.applyValue();
				this.updateMessage();
			}
		}
		else if (key == 262)
		{
			double old_progress = this.value;
			this.value = MathHelper.clamp(this.value + this.arrowNudgeAmount, 0.0D, 1.0D);
			if (old_progress != this.value)
			{
				this.applyValue();
				this.updateMessage();
			}
		}
		return false;
	}

	@Override protected String getNarrationMessage(){ return new TranslatableText("gui.narrate.slider", narrationMessage.equals("") ? this.getMessage() : narrationMessage).asFormattedString(); }
}
