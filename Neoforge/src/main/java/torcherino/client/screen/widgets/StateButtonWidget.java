package torcherino.client.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;

public abstract class StateButtonWidget extends Button {
    private static final BlockEntityRenderDispatcher itemRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    private static final WidgetSprites SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("widget/button"), Identifier.withDefaultNamespace("widget/button_disabled"), Identifier.withDefaultNamespace("widget/button_highlighted"));
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


    @Override
    protected final void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks){
        if (visible) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
            context.item(this.getButtonIcon(),getX() + 2, getY() + 2);
            if (this.isHovered) {
                context.setTooltipForNextFrame(this.getFont(), narrationMessage,getX() + 14, getY() + 18);
            }
        }
    }

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
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
