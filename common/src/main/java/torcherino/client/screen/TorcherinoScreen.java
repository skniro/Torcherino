package torcherino.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;
import torcherino.TorcherinoImpl;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.client.screen.widgets.GradatedSliderWidget;
import torcherino.client.screen.widgets.StateButtonWidget;
import torcherino.platform.NetworkUtils;

public final class TorcherinoScreen extends Screen {
    private static final ResourceLocation SCREEN_TEXTURE = new ResourceLocation(TorcherinoImpl.MOD_ID, "textures/screens/torcherino.png");
    private static final int screenWidth = 245;
    private static final int screenHeight = 123;

    private final BlockPos blockPos;
    private final Tier tier;
    private final Component cached_title;
    private int xRange, zRange, yRange, speed, redstoneMode, left, top;

    public TorcherinoScreen(Component title, int xRange, int zRange, int yRange, int speed, int redstoneMode, BlockPos pos, ResourceLocation tierID) {
        super(title);
        this.tier = TorcherinoAPI.INSTANCE.getTier(tierID);
        this.blockPos = pos;
        this.xRange = xRange;
        this.zRange = zRange;
        this.yRange = yRange;
        this.speed = speed == 0 ? 1 : speed;
        this.redstoneMode = redstoneMode;
        this.cached_title = title;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        left = (width - screenWidth) / 2;
        top = (height - screenHeight) / 2;
        this.addRenderableWidget(new GradatedSliderWidget(left + 8, top + 20, 205, (double) (speed - 1) / (tier.maxSpeed() - 1), tier.maxSpeed()) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("gui.torcherino.speed", 100 * TorcherinoScreen.this.speed));
            }

            @Override
            protected void applyValue() {
                TorcherinoScreen.this.speed = 1 + (int) Math.round(value * (TorcherinoScreen.this.tier.maxSpeed() - 1));
                value = (double) (speed - 1) / (tier.maxSpeed() - 1);
            }
        });
        this.addRenderableWidget(new GradatedSliderWidget(left + 8, top + 45, 205, (double) xRange / tier.xzRange(), tier.xzRange()) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("gui.torcherino.x_range", TorcherinoScreen.this.xRange * 2 + 1));
            }

            @Override
            protected void applyValue() {
                TorcherinoScreen.this.xRange = (int) Math.round(value * TorcherinoScreen.this.tier.xzRange());
                value = (double) xRange / tier.xzRange();
            }
        });
        this.addRenderableWidget(new GradatedSliderWidget(left + 8, top + 70, 205, (double) zRange / tier.xzRange(), tier.xzRange()) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("gui.torcherino.z_range", TorcherinoScreen.this.zRange * 2 + 1));
            }

            @Override
            protected void applyValue() {
                TorcherinoScreen.this.zRange = (int) Math.round(value * TorcherinoScreen.this.tier.xzRange());
                value = (double) zRange / tier.xzRange();
            }
        });
        this.addRenderableWidget(new GradatedSliderWidget(left + 8, top + 95, 205, (double) yRange / tier.yRange(), tier.yRange()) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("gui.torcherino.y_range", TorcherinoScreen.this.yRange * 2 + 1));
            }

            @Override
            protected void applyValue() {
                TorcherinoScreen.this.yRange = (int) Math.round(value * TorcherinoScreen.this.tier.yRange());
                value = (double) yRange / tier.yRange();
            }
        });
        this.addRenderableWidget(new StateButtonWidget(this, left + 217, top + 20) {
            ItemStack buttonIcon;

            @Override
            protected void initialize() {
                this.setButtonMessage();
                this.setButtonIcon();
            }

            private void setButtonMessage() {
                String translationKey = switch (TorcherinoScreen.this.redstoneMode) {
                    case 0 -> "gui.torcherino.mode.normal";
                    case 1 -> "gui.torcherino.mode.inverted";
                    case 2 -> "gui.torcherino.mode.ignored";
                    case 3 -> "gui.torcherino.mode.off";
                    default -> "gui.torcherino.mode.error";
                };
                this.setNarrationMessage(Component.translatable("gui.torcherino.mode", Component.translatable(translationKey)));
            }

            private void setButtonIcon() {
                switch (TorcherinoScreen.this.redstoneMode) {
                    case 0 -> this.buttonIcon = new ItemStack(Items.REDSTONE);
                    case 1 -> this.buttonIcon = new ItemStack(Items.REDSTONE_TORCH);
                    case 2 -> this.buttonIcon = new ItemStack(Items.GUNPOWDER);
                    case 3 -> this.buttonIcon = new ItemStack(Items.REDSTONE_LAMP);
                    default -> this.buttonIcon = new ItemStack(Items.FURNACE);
                }
            }

            @Override
            protected void nextState() {
                TorcherinoScreen.this.redstoneMode = (TorcherinoScreen.this.redstoneMode + 1) % 4;
                this.initialize();
            }

            @Override
            protected ItemStack getButtonIcon() {
                return buttonIcon;
            }
        });
    }

    @Override
    public void render(PoseStack matrixStack, int x, int y, float partialTicks) {
        this.fillGradient(matrixStack, 0, 0, this.width, this.height, -1072689136, -804253680);
        RenderSystem.setShaderTexture(0, SCREEN_TEXTURE);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        this.blit(matrixStack, left, top, 0, 0, screenWidth, screenHeight);
        font.draw(matrixStack, cached_title.getVisualOrderText(), (width - font.width(cached_title)) / 2.0f, top + 6, 4210752);
        super.render(matrixStack, x, y, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || minecraft.options.keyInventory.matches(keyCode, 0)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        NetworkUtils.getInstance().c2s_updateTorcherinoValues(blockPos, xRange, zRange, yRange, speed, redstoneMode);
        super.onClose();
    }
}
