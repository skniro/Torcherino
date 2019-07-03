package torcherino.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import torcherino.Torcherino;
import torcherino.api.blocks.TorcherinoBlockEntity;
import torcherino.client.screen.TorcherinoScreen;

public class Networker
{
    public static Networker INSTANCE = new Networker();

    public void initialize()
    {
        // Update Torcherino Values
        ServerSidePacketRegistry.INSTANCE.register(new Identifier(Torcherino.MOD_ID, "utv"), (PacketContext context, PacketByteBuf buffer) ->
        {
            World world = context.getPlayer().getEntityWorld();
            BlockPos pos = buffer.readBlockPos();
            buffer.retain();
            context.getTaskQueue().execute(() ->
            {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof TorcherinoBlockEntity)
                {
                    ((TorcherinoBlockEntity) blockEntity).readClientData(buffer);
                }
                buffer.release();
            });
        });

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
        {
            // Open Torcherino Screen
            ClientSidePacketRegistry.INSTANCE.register(new Identifier(Torcherino.MOD_ID, "ots"), (PacketContext context, PacketByteBuf buffer) ->
            {
                World world = MinecraftClient.getInstance().world;
                BlockPos pos = buffer.readBlockPos();
                Text title = buffer.readText();
                int xRange = buffer.readInt();
                int zRange = buffer.readInt();
                int yRange = buffer.readInt();
                int speed = buffer.readInt();
                int redstoneMode = buffer.readInt();
                context.getTaskQueue().execute(() -> {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (blockEntity instanceof TorcherinoBlockEntity)
                    {
                        Identifier tierID = ((TorcherinoBlockEntity) blockEntity).getTierID();
                        Screen screen = new TorcherinoScreen(title, xRange, zRange, yRange, speed, redstoneMode, pos, tierID);
                        MinecraftClient.getInstance().openScreen(screen);
                    }
                });
            });
        }
    }
}
