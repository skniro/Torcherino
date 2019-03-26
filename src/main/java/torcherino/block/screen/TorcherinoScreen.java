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
		POS = pos;
		MAX_SPEED = maxSpeed;
		this.speed = speed;
		this.mode = mode;
		this.redstoneInteractionMode = redstoneInteractionMode;
	}

	@Override protected void init()
	{
		BLOCK_NAME = I18n.translate(minecraft.world.getBlockState(POS).getBlock().getTranslationKey());
		LEFT = (width - WIDTH) / 2;
		TOP = (height - HEIGHT) / 2;

		this.addButton(new StateButtonWidget(this, width/2+95, height/2-40, redstoneInteractionMode, new Integer(STATE_ITEMS.length).byteValue(), "screen.torcherino.narrate.redstoneinteraction")
		{
			@Override protected Item getStateItem(byte state) { return STATE_ITEMS[state]; }
			@Override protected String getStateName(byte state) { return I18n.translate(STATE_NAMES[state]); }
			@Override protected void onStateChange(byte state) { redstoneInteractionMode = state; }
		});

		this.addButton(new SliderWidget(width/2-115, height/2-15, 230, 20, ((double) speed)/((double) MAX_SPEED), MAX_SPEED + 1)
		{
			@Override protected void updateMessage() { setMessage(I18n.translate("screen.torcherino.speed", 100*speed)); }

			@Override protected void applyValue()
			{
				speed = (int) Math.round(MAX_SPEED * value);
				value = (double) speed / (double) MAX_SPEED;
			}
		});

		this.addButton(new SliderWidget(width/2-115, height/2+10, 230, 20, ((double) mode) / ((double) MODES.length - 1), MODES.length)
		{
			@Override protected void updateMessage()
			{
				setMessage(I18n.translate("screen.torcherino."+MODES[mode], 2*mode + 1));
				narrationMessage= I18n.translate("screen.torcherino.narrate."+MODES[mode], 2*mode + 1);
			}

			@Override protected void applyValue()
			{
				mode = (byte) Math.round((MODES.length-1) * value);
				value = (double) mode / ((double) MODES.length - 1);
			}
		});
	}

	@Override public void onClose()
	{
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer()).writeBlockPos(POS);
		packetByteBuf.writeInt(speed);
		packetByteBuf.writeByte(mode);
		packetByteBuf.writeByte(redstoneInteractionMode);
		ClientSidePacketRegistryImpl.INSTANCE.sendToServer(Utils.getId("updatetorcherinostate"), packetByteBuf);
		super.onClose();
	}

	@Override public void render(int cursorX, int cursorY, float float_1)
	{
		renderBackground();
		minecraft.getTextureManager().bindTexture(SCREEN_TEXTURE);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		blit(LEFT, TOP, 0, 0, WIDTH, HEIGHT);
		font.draw(BLOCK_NAME, (float)(width / 2 - font.getStringWidth(BLOCK_NAME) / 2), height/2.0F - 35, 4210752);
		super.render(cursorX, cursorY, float_1);
	}

	@Override public boolean isPauseScreen(){ return false; }
}
