package torcherino.block.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import torcherino.Utils;

public class TorcherinoScreen extends Screen
{
	private static final Item[] STATE_ITEMS = new Item[]{Items.REDSTONE, Items.REDSTONE_TORCH, Items.GUNPOWDER};
	private static final String[] STATE_NAMES = new String[]{"screen.torcherino.redstoneinteraction.normal",
			"screen.torcherino.redstoneinteraction.inverted", "screen.torcherino.redstoneinteraction.ignore"};
	private static final Identifier SCREEN_TEXTURE = Utils.getId("textures/screens/torcherino.png");
	private String BLOCK_NAME;
	private final BlockPos POS;
	private byte mode, redstoneInteractionMode;
	private int speed, LEFT, TOP;
	private final int MAX_SPEED;
	private static final int WIDTH = 256, HEIGHT = 88;
	private String[] MODES = {"area.stopped", "area.n", "area.n", "area.n", "area.n"};

	public TorcherinoScreen(BlockPos pos, int speed, int maxSpeed, byte mode, byte redstoneInteractionMode)
	{
		super(new StringTextComponent(""));
		this.POS = pos;
		this.speed = speed;
		this.MAX_SPEED = maxSpeed;
		this.mode = mode;
		this.redstoneInteractionMode = redstoneInteractionMode;
	}

	@Override protected void onInitialized()
	{
		BLOCK_NAME = I18n.translate(this.client.world.getBlockState(POS).getBlock().getTranslationKey());
		LEFT = (screenWidth - WIDTH) / 2;
		TOP = (screenHeight - HEIGHT) / 2;

		this.addButton(new StateButtonWidget(this, screenWidth/2+95, screenHeight/2-40, redstoneInteractionMode, new Integer(STATE_ITEMS.length).byteValue(), "screen.torcherino.narrate.redstoneinteraction")
		{
			@Override protected Item getStateItem(byte state) { return STATE_ITEMS[state]; }
			@Override protected String getStateName(byte state) { return I18n.translate(STATE_NAMES[state]); }
			@Override protected void onStateChange(byte state) { redstoneInteractionMode = state; }
		});

		this.addButton(new SliderWidget(screenWidth/2-115, screenHeight/2-15, 230, 20, ((double) speed)/((double) MAX_SPEED), MAX_SPEED + 1)
		{
			@Override protected void updateText() { setMessage(I18n.translate("screen.torcherino.speed", 100*speed)); }

			@Override protected void onProgressChanged()
			{
				speed = (int) Math.round(MAX_SPEED * this.progress);
				this.progress = (double) speed / (double) MAX_SPEED;
			}
		});

		this.addButton(new SliderWidget(screenWidth/2-115, screenHeight/2+10, 230, 20, ((double) mode) / ((double) MODES.length - 1), MODES.length)
		{
			@Override protected void updateText()
			{
				setMessage(I18n.translate("screen.torcherino."+MODES[mode], 2*mode + 1));
				narrationMessage= I18n.translate("screen.torcherino.narrate."+MODES[mode], 2*mode + 1);
			}

			@Override protected void onProgressChanged()
			{
				mode = (byte) Math.round((MODES.length-1) * this.progress);
				this.progress = (double) mode / ((double) MODES.length - 1);
			}
		});
	}

	@Override public void close()
	{
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer()).writeBlockPos(POS);
		packetByteBuf.writeInt(speed);
		packetByteBuf.writeByte(mode);
		packetByteBuf.writeByte(redstoneInteractionMode);
		ClientSidePacketRegistryImpl.INSTANCE.sendToServer(Utils.getId("updatetorcherinostate"), packetByteBuf);
		super.close();
	}

	@Override public void render(int cursorX, int cursorY, float float_1)
	{
		this.drawBackground();
		this.client.getTextureManager().bindTexture(SCREEN_TEXTURE);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedRect(LEFT, TOP, 0, 0, WIDTH, HEIGHT);
		this.fontRenderer.draw(BLOCK_NAME, (float)(screenWidth / 2 - this.fontRenderer.getStringWidth(BLOCK_NAME) / 2), screenHeight/2.0F - 35, 4210752);
		super.render(cursorX, cursorY, float_1);
	}

	@Override public boolean isPauseScreen() { return false; }
}
