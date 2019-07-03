package torcherino.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;

@Environment(EnvType.CLIENT)
public class TorcherinoScreen extends Screen
{
    private static final Identifier SCREEN_TEXTURE = new Identifier(Torcherino.MOD_ID, "textures/screens/torcherino.png");
    private static final int screenWidth = 245;
    private static final int screenHeight = 123;

    private final BlockPos blockPos;
    private final Tier tier;
    private final String cached_title;
    private int xRange, zRange, yRange, speed, redstoneMode, left, top;

    public TorcherinoScreen(Text title, int xRange, int zRange, int yRange, int speed, int redstoneMode, BlockPos pos, Identifier tierID)
    {
        super(title);
        this.tier = TorcherinoAPI.INSTANCE.getTier(tierID);
        this.blockPos = pos;
        this.xRange = xRange;
        this.zRange = zRange;
        this.yRange = yRange;
        this.speed = speed;
        this.redstoneMode = redstoneMode;
        this.cached_title = title.asString();
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    protected void init()
    {
        left = (width - screenWidth) / 2;
        top = (height - screenHeight) / 2;
    }

    @Override
    public void render(int x, int y, float partialTicks)
    {
        fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        minecraft.getTextureManager().bindTexture(SCREEN_TEXTURE);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        blit(left, top, 0, 0, screenWidth, screenHeight);
        font.draw(title.asString(), (width - font.getStringWidth(title.asString())) / 2.0f, top + 6, 4210752);
        super.render(x, y, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256 || minecraft.options.keyInventory.matchesKey(keyCode, 0))
        {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose()
    {
        PacketByteBuf packetBuffer = new PacketByteBuf(Unpooled.buffer());
        packetBuffer.writeBlockPos(blockPos);
        packetBuffer.writeInt(xRange);
        packetBuffer.writeInt(zRange);
        packetBuffer.writeInt(yRange);
        packetBuffer.writeInt(speed);
        packetBuffer.writeInt(redstoneMode);
        ClientSidePacketRegistry.INSTANCE.sendToServer(new Identifier(Torcherino.MOD_ID, "utv"), packetBuffer);
        super.onClose();
    }
}
