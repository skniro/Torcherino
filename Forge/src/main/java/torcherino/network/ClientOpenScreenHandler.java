package torcherino.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.network.CustomPayloadEvent;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.client.screen.TorcherinoScreen;

public final class ClientOpenScreenHandler {
    public static void open(OpenScreenMessage message, CustomPayloadEvent.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player.level().getBlockEntity(message.pos()) instanceof TorcherinoBlockEntity blockEntity) {
            TorcherinoScreen screen = new TorcherinoScreen(Component.translatable(message.title()), message.xRange(), message.zRange(), message.yRange(),
                    message.speed(), message.redstoneMode(), blockEntity.getBlockPos(), blockEntity.getTier());
            minecraft.setScreen(screen);
        }
    }
}

