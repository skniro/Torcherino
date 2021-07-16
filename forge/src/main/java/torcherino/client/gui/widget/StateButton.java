package torcherino.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public abstract class StateButton extends AbstractButton
{
    private int state;
    private Component narrationMessage;

    public StateButton(final int x, final int y, final int state)
    {
        super(x, y, 20, 20, TextComponent.EMPTY);
        setInternalState(state);
    }

    protected abstract ItemStack getButtonIcon();

    protected abstract void setState(final int state);

    protected abstract int getMaxStates();

    private void setInternalState(final int stateIn)
    {
        state = (stateIn >= getMaxStates() ? 0 : stateIn);
        setState(state);
    }

    @Override
    public void renderButton(final PoseStack stack, final int mouseX, final int mouseY, final float partialTicks)
    {
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        if (active)
        {
            Minecraft.getInstance().getItemRenderer().renderGuiItem(getButtonIcon(), x + 2, y + 2);
            if (isHovered())
            {
                drawHoveringText(stack, narrationMessage, mouseX, mouseY);
            }
        }
    }

    protected abstract void drawHoveringText(final PoseStack stack, final Component text, final int width, final int height);

    @Override
    public void onPress() { setInternalState(++state); }

    public BaseComponent createNarrationMessage() { return new TranslatableComponent("gui.narrate.button", narrationMessage); }

    public void setNarrationMessage(final Component narrationMessage) { this.narrationMessage = narrationMessage; }
}
