package torcherino.block.screen;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import torcherino.Utils;

public class TorcherinoScreen extends Screen
{
	private static final Identifier SCREEN_TEXTURE = Utils.getId("textures/screens/torcherino.png");
	private String BLOCK_NAME;
	private BlockPos POS;
	private byte mode;
	private int speed, MAX_SPEED, WIDTH, HEIGHT, LEFT, TOP;
	private String[] MODES = {"chat.torcherino.hint.area.stopped", "chat.torcherino.hint.area.n",
			"chat.torcherino.hint.area.n", "chat.torcherino.hint.area.n", "chat.torcherino.hint.area.n"};
	private SliderWidget speedSlider, modeSlider;
	public TorcherinoScreen(BlockPos pos, int speed, int maxspeed, byte mode)
	{
		this.POS = pos;
		this.speed = speed;
		this.MAX_SPEED = maxspeed;
		this.mode = mode;
	}

	@Override
	protected void onInitialized()
	{
		BLOCK_NAME = I18n.translate(this.client.world.getBlockState(POS).getBlock().getTranslationKey());
		WIDTH = 256;
		HEIGHT = 88;
		LEFT = (width - WIDTH) / 2;
		TOP = (height - HEIGHT) / 2;
		int sliderWidth = (int) (WIDTH * 0.9);
		speedSlider = this.addButton(new SliderWidget(width/2-sliderWidth/2, height/2-15, sliderWidth, 20, ((double) speed)/((double) MAX_SPEED))
		{
			@Override
			protected void updateText()
			{
				this.setText(I18n.translate("chat.torcherino.hint.speed", 100*speed));
			}

			@Override
			protected void onProgressChanged()
			{
				speed = (int) Math.round(MAX_SPEED * this.progress);
			}
		});
		modeSlider = this.addButton(new SliderWidget(width/2-sliderWidth/2, height/2+10, sliderWidth, 20, ((double) mode) / ((double) MODES.length - 1))
		{
			@Override
			protected void updateText()
			{
				this.setText(I18n.translate(MODES[mode], 2*mode + 1));
			}

			@Override
			protected void onProgressChanged()
			{
				mode = (byte) Math.round((MODES.length-1) * this.progress);
				this.setProgress(((double) mode) / ((double) MODES.length - 1));
			}
		});
	}

	@Override
	public void close()
	{
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeBlockPos(POS);
		packetByteBuf.writeInt(speed);
		packetByteBuf.writeByte(mode);
		ClientSidePacketRegistryImpl.INSTANCE.sendToServer(Utils.getId("updatetorcherinostate"), packetByteBuf);
		super.close();
	}

	public void method_18326(int int_1, int int_2, float float_1)
	{
		this.drawBackground();
		this.client.getTextureManager().bindTexture(SCREEN_TEXTURE);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedRect(LEFT, TOP, 0, 0, WIDTH, HEIGHT);
		this.fontRenderer.draw(BLOCK_NAME, (float)(width / 2 - this.fontRenderer.getStringWidth(BLOCK_NAME) / 2), height/2.0F - 35, 4210752);
		super.method_18326(int_1, int_2, float_1);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int int_1, int int_2, int int_3)
	{
		if(int_1 == 262)
		{
			speed++;
			if(speed > MAX_SPEED){speed = MAX_SPEED;}
			speedSlider.setProgress(((double) speed) / ((double) MAX_SPEED));
			return true;
		}
		else if(int_1 == 263)
		{
			speed--;
			if(speed < 0){speed = 0;}
			speedSlider.setProgress(((double) speed) / ((double) MAX_SPEED));
			return true;
		}
		else
		{
			return super.keyPressed(int_1, int_2, int_3);
		}
	}

}
