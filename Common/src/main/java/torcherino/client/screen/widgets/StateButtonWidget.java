package torcherino.client.screen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public abstract class StateButtonWidget extends Button {
    private static final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    private final Screen screen;
    private Component narrationMessage;
    private Font font;

    public StateButtonWidget(Screen screen, int x, int y, Font font) {
        super(x, y, 20, 20, Component.empty(), (b) -> {}, Button.DEFAULT_NARRATION);
        this.screen = screen;
        this.font = font;
        this.initialize();
    }

    public Font getFont() {
        return this.font;
    }

    protected abstract void initialize();

    protected abstract void nextState();

    protected abstract ItemStack getButtonIcon();

    public void render(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            super.render(context, mouseX, mouseY, partialTicks);
            context.renderItemDecorations(this.getFont() ,this.getButtonIcon(),getX() + 2, getY() + 2);
            if (this.isHovered) {
                context.renderTooltip(this.getFont(), narrationMessage ,getX() + 14, getY() + 18);
            }
        }
    }

    @Override
    public void onPress() {
        this.nextState();
    }

    @Override
    public MutableComponent createNarrationMessage() {
        return Component.translatable("gui.narrate.button", narrationMessage);
    }

    protected void setNarrationMessage(Component narrationMessage) {
        this.narrationMessage = narrationMessage;
    }
}
