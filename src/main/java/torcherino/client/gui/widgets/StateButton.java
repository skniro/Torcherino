package torcherino.client.gui.widgets;
import net.minecraft.client.gui.GuiButton;

public abstract class StateButton extends GuiButton
{

	public StateButton(int buttonId, int x, int y)
	{
		super(buttonId, x, y, 20, 20, "");
	}

	public StateButton(int buttonId, int x, int y, int width, int height)
	{
		super(buttonId, x, y, width, height, "");
	}
}
