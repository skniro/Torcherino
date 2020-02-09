package torcherino.client.gui.buttons;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public abstract class StateButton extends AbstractButton
{
    private final int screenWidth, screenHeight;
    private int state;
    private String narrationMessage;

    public StateButton(int x, int y, int screenWidth, int screenHeight, int state)
    {
        super(x, y, 20, 20, "");
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setInternalState(state);
    }

    protected abstract ItemStack getButtonIcon();

    protected abstract void setState(int state);

    protected abstract int getMaxStates();

    private void setInternalState(int state)
    {
        if (state >= getMaxStates()) state = 0;
        this.state = state;
        setState(state);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        super.render(mouseX, mouseY, partialTicks);
        if (visible)
        {
            Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(getButtonIcon(), x + 2, y + 2);
            if (isHovered())
            {
                GuiUtils.drawHoveringText(getButtonIcon(), Lists.asList(narrationMessage, new String[]{}), x + width / 2, y + height / 2,
                        screenWidth, screenHeight, -1, Minecraft.getInstance().fontRenderer);
            }
        }
    }

    @Override
    public void onPress() { setInternalState(++state); }

    @Override
    public String getNarrationMessage() { return new TranslationTextComponent("gui.narrate.button", this.narrationMessage).getFormattedText(); }

    public void setNarrationMessage(String narrationMessage) { this.narrationMessage = narrationMessage; }
}
