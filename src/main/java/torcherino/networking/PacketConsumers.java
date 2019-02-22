package torcherino.networking;

import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import torcherino.Utils;
import torcherino.block.entity.TorcherinoBlockEntity;

public class PacketConsumers
{
	public static class ModifierBind implements PacketConsumer
	{
		@Override
		public void accept(PacketContext context, PacketByteBuf buffer)
		{
			PlayerEntity player = context.getPlayer();
			boolean pressed = buffer.readBoolean();
			context.getTaskQueue().execute(() -> Utils.keyStates.put(player, pressed));
		}
	}

	public static class TorcherinoScreen implements PacketConsumer
	{
		@Override
		public void accept(PacketContext context, PacketByteBuf buffer)
		{
			CompoundTag tag = buffer.readCompoundTag();
			BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
			int Speed = tag.getInt("Speed");
			int MaxSpeed = tag.getInt("MaxSpeed");
			byte Mode = tag.getByte("Mode");
			byte state = tag.getByte("RedstonePowerMode");
			context.getTaskQueue().execute(() -> MinecraftClient.getInstance().openScreen(new torcherino.block.screen.TorcherinoScreen(pos, Speed, MaxSpeed, Mode, state)));
		}
	}

	public static class UpdateTorcherino implements PacketConsumer
	{
		@Override
		public void accept(PacketContext context, PacketByteBuf buffer)
		{
			World world = context.getPlayer().world;
			BlockPos pos = buffer.readBlockPos();
			int speed = buffer.readInt();
			byte mode = buffer.readByte();
			byte state = buffer.readByte();
			context.getTaskQueue().execute(() ->
			{
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if(blockEntity instanceof TorcherinoBlockEntity)
				{
					TorcherinoBlockEntity torch = (TorcherinoBlockEntity) blockEntity;
					torch.setSpeed(speed);
					torch.setMode(mode);
					torch.setRedstonePowerMode(TorcherinoBlockEntity.PowerState.fromByte(state));
				}
			});
		}
	}
}
