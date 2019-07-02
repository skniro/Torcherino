package torcherino.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import torcherino.Utils;
import torcherino.client.screen.buttons.SliderButton;
import torcherino.client.screen.buttons.StateButtonWidget;

public class TorcherinoScreen extends Screen
{
    private static final Item[] STATE_ITEMS = { Items.REDSTONE, Items.REDSTONE_TORCH, Items.GUNPOWDER };
    private static final String[] STATE_NAMES = { "screen.torcherino.redstoneinteraction.normal", "screen.torcherino.redstoneinteraction.inverted", "screen.torcherino.redstoneinteraction.ignore" };
    private static final Identifier SCREEN_TEXTURE = Utils.getId("textures/screens/torcherino.png");
    private static final int WIDTH = 256, HEIGHT = 88;
    private static String[] MODES = { "area.stopped", "area.n", "area.n", "area.n", "area.n" };
    private final BlockPos POS;
    private final int MAX_SPEED;
    private String BLOCK_NAME;
    private int speed, mode, redstoneInteractionMode, LEFT, TOP;

    public TorcherinoScreen(BlockPos pos, int speed, int maxSpeed, int mode, int redstoneInteractionMode)
    {
        super(new LiteralText(""));
        POS = pos;
        MAX_SPEED = maxSpeed;
        this.speed = speed;
        this.mode = mode;
        this.redstoneInteractionMode = redstoneInteractionMode;
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    protected void init()
    {
        BLOCK_NAME = new TranslatableText(minecraft.world.getBlockState(POS).getBlock().getTranslationKey()).asFormattedString();
        LEFT = (width - WIDTH) / 2;
        TOP = (height - HEIGHT) / 2;
        this.addButton(new StateButtonWidget(this, width / 2 + 95, height / 2 - 40, redstoneInteractionMode, STATE_ITEMS.length, "screen.torcherino.narrate.redstoneinteraction")
        {
            @Override
            protected Item getStateItem(int state) { return STATE_ITEMS[state]; }

            @Override
            protected String getStateName(int state) { return new TranslatableText(STATE_NAMES[state]).asFormattedString(); }

            @Override
            protected void onStateChange(int state) { redstoneInteractionMode = state; }
        });
        this.addButton(new SliderButton(width / 2 - 115, height / 2 - 15, 230, 20, ((double) speed) / ((double) MAX_SPEED), MAX_SPEED + 1)
        {
            @Override
            protected void updateMessage() { setMessage(new TranslatableText("screen.torcherino.speed", 100 * speed).asFormattedString()); }

            @Override
            protected void applyValue()
            {
                speed = (int) Math.round(MAX_SPEED * value);
                value = (double) speed / (double) MAX_SPEED;
            }
        });
        this.addButton(new SliderButton(width / 2 - 115, height / 2 + 10, 230, 20, ((double) mode) / ((double) MODES.length - 1), MODES.length)
        {
            @Override
            protected void updateMessage()
            {
                setMessage(new TranslatableText("screen.torcherino." + MODES[mode], 2 * mode + 1).asFormattedString());
                narrationMessage = new TranslatableText("screen.torcherino.narrate." + MODES[mode], 2 * mode + 1).asFormattedString();
            }

            @Override
            protected void applyValue()
            {
                mode = (int) Math.round((MODES.length - 1) * value);
                value = (double) mode / ((double) MODES.length - 1);
            }
        });
    }

    @Override
    public void onClose()
    {
        PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer()).writeBlockPos(POS);
        packetByteBuf.writeInt(speed);
        packetByteBuf.writeInt(mode);
        packetByteBuf.writeInt(redstoneInteractionMode);
        ClientSidePacketRegistryImpl.INSTANCE.sendToServer(Utils.getId("updatetorcherinostate"), packetByteBuf);
        super.onClose();
    }

    @Override
    public void render(int cursorX, int cursorY, float float_1)
    {
        renderBackground();
        minecraft.getTextureManager().bindTexture(SCREEN_TEXTURE);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        blit(LEFT, TOP, 0, 0, WIDTH, HEIGHT);
        font.draw(BLOCK_NAME, (width - font.getStringWidth(BLOCK_NAME)) / 2.0f, height / 2.0F - 35, 4210752);
        super.render(cursorX, cursorY, float_1);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier)
    {
        if (keyCode == 256 || minecraft.options.keyInventory.matchesKey(keyCode, 0))
        {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifier);
    }
}
