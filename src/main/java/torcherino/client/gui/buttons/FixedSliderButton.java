package torcherino.client.gui.buttons;

import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.util.math.MathHelper;

public abstract class FixedSliderButton extends AbstractSlider
{
	private final float nudgeAmount;

	public FixedSliderButton(int x, int y, int width, double progress, int permutations)
	{
		super(x, y, width, 20, progress);
		this.nudgeAmount = 1.0F / permutations;
		this.applyValue();
		this.updateMessage();
	}

	@Override public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		boolean flag = keyCode == 263;
		if (flag || keyCode == 262)
		{
			float f = flag ? -nudgeAmount : nudgeAmount;
			this.setValue(this.value + f);
		}
		return false;
	}

	private void setValue(double value)
	{
		double d0 = this.value;
		this.value = MathHelper.clamp(value, 0, 1);
		if (d0 != this.value) this.applyValue();
		this.updateMessage();
	}
}
