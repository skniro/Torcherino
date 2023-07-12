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

    public StateButtonWidget(Screen screen, int x, int y) {
        super(x, y, 20, 20, Component.empty(), (b) -> {}, Button.DEFAULT_NARRATION);
        this.screen = screen;
        this.initialize();
    }

    protected abstract void initialize();

    protected abstract void nextState();

    protected abstract ItemStack getButtonIcon();

    public void render(GuiGraphics context, int mouseX, int mouseY, float partialTicks, Font font) {
        if (visible) {
            super.render(context, mouseX, mouseY, partialTicks);
            context.renderItemDecorations(font ,this.getButtonIcon(),getX() + 2, getY() + 2);
            if (this.isHovered) {
                screen.render(context, getX() + 14, getY() + 18, partialTicks);
                screen.getNarrationMessage();
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
