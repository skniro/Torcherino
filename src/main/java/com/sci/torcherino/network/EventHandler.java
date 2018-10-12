package com.sci.torcherino.network;

import com.sci.torcherino.Torcherino;
import com.sci.torcherino.blocks.blocks.BlockLanterino;
import com.sci.torcherino.blocks.blocks.BlockTorcherino;
import com.sci.torcherino.blocks.tiles.TileTorcherino;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
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
	@SubscribeEvent
	public void onPlayerRightClick(RightClickBlock event)
	{
		if(event.getHand() == EnumHand.OFF_HAND) return;
		EntityPlayer player = event.getEntityPlayer();
		Block blockClicked = event.getWorld().getBlockState(event.getPos()).getBlock();
		TileEntity tile = event.getWorld().getTileEntity(event.getPos());
		if(tile == null || !(tile instanceof TileTorcherino)) return;
		if(!event.getWorld().isRemote)
		{
			TileTorcherino torch = (TileTorcherino) tile;
	        if(Torcherino.keyStates.get(player) == null) torch.changeMode(false);
	        else torch.changeMode(Torcherino.keyStates.get(player).booleanValue()==true);
	        player.sendStatusMessage(torch.getDescription(), true);
		}
		event.setUseBlock(Event.Result.DENY);
		event.setUseItem(Event.Result.DENY);
		event.setCanceled(true);
	}
}
