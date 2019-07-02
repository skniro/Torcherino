package torcherino.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import torcherino.api.blocks.TorcherinoBlockEntity;
import torcherino.client.screen.TorcherinoScreen;

public class PacketConsumers
{
    @Environment(EnvType.CLIENT)
    public static class TorcherinoScreenConsumer implements PacketConsumer
    {
        @Override
        public void accept(PacketContext context, PacketByteBuf buffer)
        {
            CompoundTag tag = buffer.readCompoundTag();
            BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            int Speed = tag.getInt("Speed");
            int MaxSpeed = tag.getInt("MaxSpeed");
            int Mode = tag.getInt("Mode");
            int state = tag.getInt("RedstoneInteractionMode");
            context.getTaskQueue().execute(() -> MinecraftClient.getInstance().openScreen(new TorcherinoScreen(pos, Speed, MaxSpeed, Mode, state)));
        }
    }

    public static class UpdateTorcherinoConsumer implements PacketConsumer
    {
        @Override
        public void accept(PacketContext context, PacketByteBuf buffer)
        {
            World world = context.getPlayer().world;
            BlockPos pos = buffer.readBlockPos();
            int speed = buffer.readInt();
            int mode = buffer.readInt();
            int redstoneInteractionMode = buffer.readInt();
            context.getTaskQueue().execute(() ->
            {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof TorcherinoBlockEntity)
                {
                    TorcherinoBlockEntity torch = (TorcherinoBlockEntity) blockEntity;
                    torch.setSpeed(speed);
                    torch.setMode(mode);
                    torch.setRedstoneInteractionMode(redstoneInteractionMode);
                }
            });
        }
    }
}
