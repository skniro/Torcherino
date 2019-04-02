package torcherino.network;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import torcherino.Torcherino;

@OnlyIn(Dist.CLIENT)
public class Client
{
	private static KeyBinding modifierBind;
	private static boolean pressed = false;

	@SubscribeEvent
	public static void clientSetup(final FMLClientSetupEvent event)
	{
		modifierBind = new KeyBinding("key.torcherino.modifier", KeyConflictContext.IN_GAME, InputMappings.getInputByName("key.keyboard.left.shift"), "key.categories.misc");
		ClientRegistry.registerKeyBinding(modifierBind);
	}

	@SubscribeEvent public static void clientTick(final TickEvent.ClientTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START && Minecraft.getInstance().currentScreen == null)
		{
			boolean bindPressed = InputMappings.isKeyDown(modifierBind.getKey().getKeyCode());
			if (bindPressed ^ pressed) Torcherino.torcherinoNetworkChannel.sendToServer(new Messages.KeystateUpdate(pressed = !pressed, new PacketBuffer(Unpooled.buffer())));
		}
	}
}
