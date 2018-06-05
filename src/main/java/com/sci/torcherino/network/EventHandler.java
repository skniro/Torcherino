package com.sci.torcherino.network;

import com.sci.torcherino.Torcherino;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandler 
{
	private boolean state = false;
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void clientTick(TickEvent.ClientTickEvent clientTickEvent)
	{
	    if (clientTickEvent.phase == TickEvent.Phase.END)
	    {
	    	if(Minecraft.getMinecraft().isGamePaused()) return;
	    	if(Minecraft.getMinecraft().player == null) return;
	        boolean keyDown = Minecraft.getMinecraft().gameSettings.isKeyDown(KeyHandler.usageKey);
	        if(keyDown != state)
	        {
	        	PacketHandler.sendUpdateToSever(keyDown);
	        	state = keyDown;
	        }
	    }
	}
}
