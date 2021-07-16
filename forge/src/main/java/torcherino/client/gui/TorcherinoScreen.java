package torcherino.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.block.tile.TorcherinoTileEntity;
import torcherino.client.gui.widget.GradatedSlider;
import torcherino.client.gui.widget.StateButton;
import torcherino.network.Networker;
import torcherino.network.OpenScreenMessage;
import torcherino.network.ValueUpdateMessage;

@OnlyIn(Dist.CLIENT)
public class TorcherinoScreen extends Screen
{
    private static final ResourceLocation BACKGROUND_TEXTURE = Torcherino.getRl("textures/gui/container/torcherino.png");
    private static final int xSize = 245;
    private static final int ySize = 123;
    private final TorcherinoTileEntity tileEntity;
    private final Tier tier;
    private final Component title;
    private int guiLeft, guiTop, xRange, zRange, yRange, speed, redstoneMode;

    public TorcherinoScreen(final TorcherinoTileEntity tileEntity, final Component title, final int xRange, final int zRange, final int yRange,
            final int speed, final int redstoneMode)
    {
        super(tileEntity.getName());
        this.tileEntity = tileEntity;
        tier = TorcherinoAPI.INSTANCE.getTier(tileEntity.getTierName());
        this.title = title;
        this.xRange = xRange;
        this.zRange = zRange;
        this.yRange = yRange;
        this.speed = speed;
        this.redstoneMode = redstoneMode;
    }

    @SuppressWarnings("ConstantConditions")
    public static void open(final OpenScreenMessage msg)
    {
        final Minecraft minecraft = Minecraft.getInstance();
        minecraft.submitAsync(() ->
        {
            final BlockEntity tileEntity = minecraft.player.level.getBlockEntity(msg.pos);
            if (tileEntity instanceof TorcherinoTileEntity)
            {
                final TorcherinoScreen screen = new TorcherinoScreen((TorcherinoTileEntity) tileEntity, msg.title, msg.xRange, msg.zRange, msg.yRange,
                        msg.speed, msg.redstoneMode);
                minecraft.setScreen(screen);
            }
        });
    }

    @Override
    protected void init()
    {
        super.init();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        if (speed == 0) { speed = 1; }
        addButton(new GradatedSlider(guiLeft + 8, guiTop + 20, 205, (double) (speed - 1) / (tier.MAX_SPEED - 1), tier.MAX_SPEED - 1)
        {
            @Override
            protected void updateMessage() { setMessage(new TranslatableComponent("gui.torcherino.speed", 100 * TorcherinoScreen.this.speed)); }

            @Override
            protected void applyValue()
            {
                TorcherinoScreen.this.speed = 1 + (int) Math.round(value * (TorcherinoScreen.this.tier.MAX_SPEED - 1));
                value = (double) (speed - 1) / (tier.MAX_SPEED - 1);
            }
        });
        addButton(new GradatedSlider(guiLeft + 8, guiTop + 45, 205, (double) xRange / tier.XZ_RANGE, tier.XZ_RANGE)
        {
            @Override
            protected void updateMessage() { setMessage(new TranslatableComponent("gui.torcherino.x_range", TorcherinoScreen.this.xRange * 2 + 1)); }

            @Override
            protected void applyValue()
            {
                TorcherinoScreen.this.xRange = (int) Math.round(value * TorcherinoScreen.this.tier.XZ_RANGE);
                value = (double) xRange / tier.XZ_RANGE;
            }
        });
        addButton(new GradatedSlider(guiLeft + 8, guiTop + 70, 205, (double) zRange / tier.XZ_RANGE, tier.XZ_RANGE)
        {
            @Override
            protected void updateMessage() { setMessage(new TranslatableComponent("gui.torcherino.z_range", TorcherinoScreen.this.zRange * 2 + 1)); }

            @Override
            protected void applyValue()
            {
                TorcherinoScreen.this.zRange = (int) Math.round(value * TorcherinoScreen.this.tier.XZ_RANGE);
                value = (double) zRange / tier.XZ_RANGE;
            }
        });
        addButton(new GradatedSlider(guiLeft + 8, guiTop + 95, 205, (double) yRange / tier.Y_RANGE, tier.Y_RANGE)
        {
            @Override
            protected void updateMessage() { setMessage(new TranslatableComponent("gui.torcherino.y_range", TorcherinoScreen.this.yRange * 2 + 1)); }

            @Override
            protected void applyValue()
            {
                TorcherinoScreen.this.yRange = (int) Math.round(value * TorcherinoScreen.this.tier.Y_RANGE);
                value = (double) yRange / tier.Y_RANGE;
            }
        });
        addButton(new StateButton(guiLeft + 217, guiTop + 20, redstoneMode)
        {
            private ItemStack renderStack;

            @Override
            protected void setState(final int state)
            {
                final TranslatableComponent modeText;
                switch (state)
                {
                    case 0:
                        renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "redstone")));
                        modeText = new TranslatableComponent("gui.torcherino.mode.normal");
                        break;
                    case 1:
                        renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "redstone_torch")));
                        modeText = new TranslatableComponent("gui.torcherino.mode.inverted");
                        break;
                    case 2:
                        renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "gunpowder")));
                        modeText = new TranslatableComponent("gui.torcherino.mode.ignored");
                        break;
                    case 3:
                        renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "redstone_lamp")));
                        modeText = new TranslatableComponent("gui.torcherino.mode.off");
                        break;
                    default:
                        renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "furnace")));
                        modeText = new TranslatableComponent("gui.torcherino.mode.error");
                        break;
                }
                setNarrationMessage(new TranslatableComponent("gui.torcherino.mode", modeText));
                nextNarration = Util.getMillis() + 250L;
                TorcherinoScreen.this.redstoneMode = state;
            }

            @Override
            protected int getMaxStates() { return 4; }

            @Override
            protected void drawHoveringText(final PoseStack stack, final Component text, final int width, final int height)
            {
                TorcherinoScreen.this.renderTooltip(stack, text, width, height);
            }

            @Override
            protected ItemStack getButtonIcon() { return renderStack; }
        });
    }

    @SuppressWarnings({ "ConstantConditions", "deprecation" })
    public void render(final PoseStack stack, final int mouseX, final int mouseY, final float partialTicks)
    {
        renderBackground(stack);
        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
        blit(stack, guiLeft, guiTop, 0, 0, xSize, ySize);
        font.draw(stack, title, guiLeft + (xSize - font.width(title)) / 2.0F, guiTop + 6, 4210752);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void removed()
    {
        Networker.INSTANCE.torcherinoChannel.sendToServer(new ValueUpdateMessage(tileEntity.getBlockPos(), xRange, zRange, yRange, speed, redstoneMode));
        super.removed();
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @SuppressWarnings("ConstantConditions")
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers)
    {
        if (keyCode == 256 || minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode)))
        { onClose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
