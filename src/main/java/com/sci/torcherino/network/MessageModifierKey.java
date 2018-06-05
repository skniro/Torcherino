package com.sci.torcherino.network;

import com.sci.torcherino.Torcherino;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageModifierKey implements IMessage, IMessageHandler<MessageModifierKey, MessageModifierKey>
{

	public boolean pressed;
	public MessageModifierKey(){}
	public MessageModifierKey(boolean pressed)
	{
        this.pressed = pressed;
    }
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.pressed = buf.readBoolean();
	}
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(this.pressed);
	}
	public MessageModifierKey onMessage(MessageModifierKey msg, MessageContext ctx)
	{
		EntityPlayer player = ctx.getServerHandler().player;
		if(Torcherino.keyStates.get(player) == null || Torcherino.keyStates.get(player).booleanValue() != msg.pressed) Torcherino.keyStates.put(player, new Boolean(msg.pressed));
		return null;
	}
}

