package torcherino.mixins;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin
{
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void onPlayerConnect(ClientConnection clientConnection, ServerPlayerEntity player, CallbackInfo info)
    {
        Torcherino.playerConnected(player.getUuidAsString());
        ImmutableMap<Identifier, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
        PacketByteBuf packetBuffer = new PacketByteBuf(Unpooled.buffer());
        packetBuffer.writeInt(tiers.size());
        tiers.forEach((id, tier) ->
        {
            packetBuffer.writeIdentifier(id);
            packetBuffer.writeInt(tier.getMaxSpeed());
            packetBuffer.writeInt(tier.getXZRange());
            packetBuffer.writeInt(tier.getYRange());
        });
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new Identifier(Torcherino.MOD_ID, "tts"), packetBuffer);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void onPlayerDisconnect(ServerPlayerEntity player, CallbackInfo ci) { Torcherino.playerDisconnect(player.getUuidAsString()); }
}
