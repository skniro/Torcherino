package torcherino.block.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
	private String[] MODES = {"screen.torcherino.area.stopped", "screen.torcherino.area.n", "screen.torcherino.area.n",
			"screen.torcherino.area.n", "screen.torcherino.area.n"};
	private SliderWidget speedSlider;

	public TorcherinoScreen(BlockPos pos, int speed, int maxSpeed, byte mode, byte redstoneInteractionMode)
	{
		this.POS = pos;
		this.speed = speed;
		this.MAX_SPEED = maxSpeed;
		this.mode = mode;
		this.redstoneInteractionMode = redstoneInteractionMode;
	}

	@Override
	protected void onInitialized()
	{
		BLOCK_NAME = I18n.translate(this.client.world.getBlockState(POS).getBlock().getTranslationKey());
		LEFT = (screenWidth - WIDTH) / 2;
		TOP = (screenHeight - HEIGHT) / 2;
		speedSlider = this.addButton(new SliderWidget(screenWidth/2-115, screenHeight/2-15, 230, 20, ((double) speed)/((double) MAX_SPEED))
		{
			@Override protected void onProgressChanged()
			{
				speed = (int) Math.round(MAX_SPEED * this.progress);
				this.setProgress(((double) speed) / ((double) MAX_SPEED));
				this.setText(I18n.translate("screen.torcherino.speed", 100*speed));
			}
		});
		this.addButton(new SliderWidget(screenWidth/2-115, screenHeight/2+10, 230, 20, ((double) mode) / ((double) MODES.length - 1))
		{
			@Override protected void onProgressChanged()
			{
				mode = (byte) Math.round((MODES.length-1) * this.progress);
				this.setProgress(((double) mode) / ((double) MODES.length - 1));
				this.setText(I18n.translate(MODES[mode], 2*mode + 1));
			}
		});

		this.addButton(new StateButtonWidget(this, screenWidth/2+95, screenHeight/2-40, redstoneInteractionMode, new Integer(STATE_ITEMS.length).byteValue())
		{
			@Override protected Item getStateItem(byte state) { return STATE_ITEMS[state]; }
			@Override protected String getStateName(byte state) { return I18n.translate(STATE_NAMES[state]); }
			@Override protected void onStateChange(byte state) { redstoneInteractionMode = state; }
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

	public void draw(int cursorX, int cursorY, float float_1)
	{
		this.drawBackground();
		this.client.getTextureManager().bindTexture(SCREEN_TEXTURE);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedRect(LEFT, TOP, 0, 0, WIDTH, HEIGHT);
		this.fontRenderer.draw(BLOCK_NAME, (float)(screenWidth / 2 - this.fontRenderer.getStringWidth(BLOCK_NAME) / 2), screenHeight/2.0F - 35, 4210752);
		super.draw(cursorX, cursorY, float_1);
	}

	@Override public boolean isPauseScreen() { return false; }

	@Override public boolean keyPressed(int key, int scanCode, int modifierBits)
	{
		if (key == 69) { this.close(); return true; }
		else if (key == 262) { speedSlider.setProgress(((double) speed+1) / MAX_SPEED); return true; }
		else if (key == 263) { speedSlider.setProgress(((double) speed-1) / MAX_SPEED); return true; }
		else { return super.keyPressed(key, scanCode, modifierBits); }
	}
}
