package torcherino.client.screen.buttons;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;

public abstract class StateButtonWidget extends ButtonWidget
{
    private static ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    private final int MAX_STATES;
    private final Screen screen;
    private final String narrationMessage;
    private int state;

    public StateButtonWidget(Screen screen, int x, int y, int state, int maxStates, String narrationMessage)
    {
        super(x, y, 20, 20, "", null);
        this.screen = screen;
        this.state = state;
        this.MAX_STATES = maxStates;
        this.narrationMessage = narrationMessage;
    }

    protected abstract Item getStateItem(int state);

    protected abstract String getStateName(int state);

    protected abstract void onStateChange(int state);

    @Override
    public void render(int mouseX, int mouseY, float unused)
    {
        super.render(mouseX, mouseY, unused);
        itemRenderer.renderGuiItem(getStateItem(state).getStackForRender(), x + 2, y + 2);
        if (this.isHovered) screen.renderTooltip(getStateName(state), x + 14, y + 18);
        GlStateManager.disableRescaleNormal();
        GuiLighting.disable();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
    }

    @Override
    protected String getNarrationMessage()
    {
        return new TranslatableText(this.narrationMessage, getStateName(state)).asFormattedString();
    }

    @Override
    public void onPress()
    {
        onStateChange(state = (state + 1) % MAX_STATES);
    }
}
