package torcherino.block.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;

public abstract class StateButtonWidget extends ButtonWidget
{
	private byte state;
	private final byte MAX_STATES;
	private final Screen screen;
	private static ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

	public StateButtonWidget(Screen screen, int x, int y, byte state, byte maxStates)
	{
		super(x, y, 20, 20, "");
		this.screen = screen;
		this.state = state;
		this.MAX_STATES = maxStates;
	}

	protected abstract Item getStateItem(byte state);
	protected abstract String getStateName(byte state);
	protected abstract void onStateChange(byte state);

	@Override
	public void draw(int cursorX, int cursorY, float unused)
	{
		super.draw(cursorX, cursorY, unused);

		GuiLighting.enableForItems();
		GlStateManager.disableLighting();
		itemRenderer.renderGuiItem(getStateItem(state).getDefaultStack(), x + 2, y + 2);
		GlStateManager.enableLighting();
		GuiLighting.disable();

		if(this.isHovered()) screen.drawTooltip(this.getStateName(state),x + 14, y + 18);
	}

	@Override
	public void onPressed(double cursorX, double cursorY)
	{
		state++;
		if(state == MAX_STATES){ state = 0; }
		onStateChange(state);
	}
}
