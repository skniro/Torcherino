package torcherino.client.screen.widgets;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

public abstract class GradatedSliderWidget extends AbstractSliderButton {
    private final float nudgeAmount;

    protected GradatedSliderWidget(int x, int y, int width, double progress, int permutations) {
        super(x, y, width, 20, Component.empty(), progress);
        nudgeAmount = 1.0F / permutations;
        this.updateMessage();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean pressedLeft = keyCode == GLFW.GLFW_KEY_LEFT;
        if (pressedLeft || keyCode == GLFW.GLFW_KEY_RIGHT) {
            this.setValue(this.value + (pressedLeft ? -nudgeAmount : nudgeAmount));
        }
        return false;
    }

    private void setValue(double newValue) {
        double currentValue = this.value;
        value = Mth.clamp(newValue, 0, 1);
        if (currentValue != this.value) {
            this.applyValue();
        }
        this.updateMessage();
    }
}
