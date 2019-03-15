package torcherino.block.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;

public abstract class StateButtonWidget extends AbstractButtonWidget
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

	@Override public void draw(int cursorX, int cursorY, float unused)
	{
		super.draw(cursorX, cursorY, unused);
		itemRenderer.renderGuiItem(getStateItem(state).getDefaultStack(), x + 2, y + 2);
		if(this.isHovered()) screen.drawTooltip(this.getStateName(state),x + 14, y + 18);
		GlStateManager.disableRescaleNormal();
		GuiLighting.disable();
		GlStateManager.disableLighting();
		GlStateManager.disableDepthTest();
	}

	@Override protected String getNarrationString() { return I18n.translate("gui.narrate.button", this.getStateName(state)); }

	public void method_1826() { onStateChange(state = (byte) ((state+1) % MAX_STATES)); }
}
