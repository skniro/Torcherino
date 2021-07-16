package torcherino.client.gui.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;

public abstract class GradatedSlider extends AbstractSliderButton {
    private final float nudgeAmount;

    public GradatedSlider(final int x, final int y, final int width, final double progress, final int permutations) {
        super(x, y, width, 20, TextComponent.EMPTY, progress);
        nudgeAmount = 1.0F / permutations;
        applyValue();
        updateMessage();
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        final boolean leftArrowKeyDown = keyCode == 263;
        if (leftArrowKeyDown || keyCode == 262) {
            setValue(value + (leftArrowKeyDown ? -nudgeAmount : nudgeAmount));
            return true;
        }
        return false;
    }

    private void setValue(final double valueIn) {
        final double oldValue = valueIn;
        value = Mth.clamp(valueIn, 0, 1);
        if (oldValue != value) {
            applyValue();
        }
        updateMessage();
    }
}
