package torcherino.client.gui.widgets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import java.util.ArrayList;
import java.util.List;

public abstract class StateButton extends GuiButton
{
	private int state;
	private GuiContainer containerScreen;

	public StateButton(int buttonId, int x, int y, int state, GuiContainer container)
	{
		super(buttonId, x, y, 20, 20, "");
		this.containerScreen = container;
		setInternalState(state);
	}

	protected abstract ItemStack getButtonIcon();

	protected abstract List<ITextComponent> populateToolTip();

	protected abstract void setState(int state);

	protected abstract int getMaxStates();

	private void setInternalState(int state)
	{
		if (state >= getMaxStates()) state = 0;
		this.state = state;
		setState(state);
	}

	private List<String> getToolTip()
	{
		List<ITextComponent> tooltip = populateToolTip();
		List<String> stringToolTip = new ArrayList<>();
		for (ITextComponent itextcomponent : tooltip) stringToolTip.add(itextcomponent.getFormattedText());
		return stringToolTip;
	}

	@Override public void render(int mouseX, int mouseY, float partialTicks)
	{
		super.render(mouseX, mouseY, partialTicks);
		if (visible)
		{
			RenderHelper.enableGUIStandardItemLighting();
			Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(getButtonIcon(), x + 2, y + 2);
			RenderHelper.disableStandardItemLighting();
			if (hovered)
			{
				containerScreen.drawHoveringText(getToolTip(), mouseX, mouseY);
			}
		}
	}

	@Override public void onClick(double mouseX, double mouseY)
	{
		super.onClick(mouseX, mouseY);
		setInternalState(++state);
	}
}
