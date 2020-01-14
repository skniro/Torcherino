package torcherino.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.blocks.tile.TorcherinoTileEntity;
import torcherino.client.gui.buttons.FixedSliderButton;
import torcherino.client.gui.buttons.StateButton;
import torcherino.network.Networker;
import torcherino.network.OpenScreenMessage;
import torcherino.network.ValueUpdateMessage;

@OnlyIn(Dist.CLIENT)
public class TorcherinoScreen extends Screen
{
    private static final ResourceLocation BACKGROUND_TEXTURE = Torcherino.resloc("textures/gui/container/torcherino.png");
    private static final int xSize = 245;
    private static final int ySize = 123;
    private final TorcherinoTileEntity tileEntity;
    private final Tier tier;
    private final String title;
    private int guiLeft, guiTop, xRange, zRange, yRange, speed, redstoneMode;

    public TorcherinoScreen(TorcherinoTileEntity tileEntity, ITextComponent title, int xRange, int zRange, int yRange, int speed, int redstoneMode)
    {
        super(tileEntity.getName());
        this.tileEntity = tileEntity;
        this.tier = TorcherinoAPI.INSTANCE.getTier(tileEntity.getTierName());
        this.title = title.getString();
        this.xRange = xRange;
        this.zRange = zRange;
        this.yRange = yRange;
        this.speed = speed;
        this.redstoneMode = redstoneMode;
    }

    public static void open(OpenScreenMessage msg)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.deferTask(() ->
        {
            World world = minecraft.player.world;
            TileEntity tileEntity = world.getTileEntity(msg.pos);
            if (tileEntity instanceof TorcherinoTileEntity)
            {
                TorcherinoScreen screen = new TorcherinoScreen((TorcherinoTileEntity) tileEntity, msg.title, msg.xRange, msg.zRange, msg.yRange, msg.speed,
                        msg.redstoneMode);
                Minecraft.getInstance().mouseHelper.ungrabMouse();
                Minecraft.getInstance().displayGuiScreen(screen);
            }
        });
    }

    @Override
    protected void init()
    {
        super.init();
        guiLeft = (this.width - xSize) / 2;
        guiTop = (this.height - ySize) / 2;
        if (speed == 0) speed = 1;
        this.addButton(new FixedSliderButton(guiLeft + 8, guiTop + 20, 205, (double) (speed - 1) / (tier.getMaxSpeed() - 1), tier.getMaxSpeed() - 1)
        {
            @Override
            protected void updateMessage()
            {
                this.setMessage(new TranslationTextComponent("gui.torcherino.speed", 100 * TorcherinoScreen.this.speed).getString());
            }

            @Override
            protected void applyValue()
            {
                TorcherinoScreen.this.speed = 1 + (int) Math.round(value * (TorcherinoScreen.this.tier.getMaxSpeed() - 1));
                this.value = (double) (speed - 1) / (tier.getMaxSpeed() - 1);
            }
        });
        this.addButton(new FixedSliderButton(guiLeft + 8, guiTop + 45, 205, (double) xRange / tier.getXZRange(), tier.getXZRange())
        {
            @Override
            protected void updateMessage()
            {
                this.setMessage(new TranslationTextComponent("gui.torcherino.x_range", TorcherinoScreen.this.xRange * 2 + 1).getString());
            }

            @Override
            protected void applyValue()
            {
                TorcherinoScreen.this.xRange = (int) Math.round(value * TorcherinoScreen.this.tier.getXZRange());
                this.value = (double) xRange / tier.getXZRange();
            }
        });
        this.addButton(new FixedSliderButton(guiLeft + 8, guiTop + 70, 205, (double) zRange / tier.getXZRange(), tier.getXZRange())
        {
            @Override
            protected void updateMessage()
            {
                this.setMessage(new TranslationTextComponent("gui.torcherino.z_range", TorcherinoScreen.this.zRange * 2 + 1).getString());
            }

            @Override
            protected void applyValue()
            {
                TorcherinoScreen.this.zRange = (int) Math.round(value * TorcherinoScreen.this.tier.getXZRange());
                this.value = (double) zRange / tier.getXZRange();
            }
        });
        this.addButton(new FixedSliderButton(guiLeft + 8, guiTop + 95, 205, (double) yRange / tier.getYRange(), tier.getYRange())
        {
            @Override
            protected void updateMessage()
            {
                this.setMessage(new TranslationTextComponent("gui.torcherino.y_range", TorcherinoScreen.this.yRange * 2 + 1).getString());
            }

            @Override
            protected void applyValue()
            {
                TorcherinoScreen.this.yRange = (int) Math.round(value * TorcherinoScreen.this.tier.getYRange());
                this.value = (double) yRange / tier.getYRange();
            }
        });
        this.addButton(new StateButton(guiLeft + 217, guiTop + 20, width, height, this.redstoneMode)
        {
            private ItemStack renderStack;

            @Override
            protected void setState(int state)
            {
                TranslationTextComponent textComponent;
                switch (state)
                {
                    case 0:
                        renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "redstone")));
                        textComponent = new TranslationTextComponent("gui.torcherino.mode.normal");
                        break;
                    case 1:
                        renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "redstone_torch")));
                        textComponent = new TranslationTextComponent("gui.torcherino.mode.inverted");
                        break;
                    case 2:
                        renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "gunpowder")));
                        textComponent = new TranslationTextComponent("gui.torcherino.mode.ignored");
                        break;
                    case 3:
                        renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "redstone_lamp")));
                        textComponent = new TranslationTextComponent("gui.torcherino.mode.off");
                        break;
                    default:
                        renderStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "furnace")));
                        textComponent = new TranslationTextComponent("gui.torcherino.mode.error");
                        break;
                }
                setNarrationMessage(new TranslationTextComponent("gui.torcherino.mode", textComponent).getString());
                this.nextNarration = Util.milliTime() + 250L;
                TorcherinoScreen.this.redstoneMode = state;
            }

            @Override
            protected int getMaxStates()
            {
                return 4;
            }

            @Override
            protected ItemStack getButtonIcon()
            {
                return renderStack;
            }
        });
    }

    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        blit(guiLeft, guiTop, 0, 0, xSize, ySize);
        font.drawString(title, guiLeft + (xSize - font.getStringWidth(title)) / 2, guiTop + 6, 4210752);
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose()
    {
        Networker.INSTANCE.torcherinoChannel
                .sendToServer(new ValueUpdateMessage(this.tileEntity.getPos(), this.xRange, this.zRange, this.yRange, this.speed, this.redstoneMode));
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() { return false; }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256 || minecraft.gameSettings.keyBindInventory.isActiveAndMatches(InputMappings.getInputByCode(keyCode, scanCode))) { this.onClose(); }
        else { super.keyPressed(keyCode, scanCode, modifiers); }
        return true;
    }
}
