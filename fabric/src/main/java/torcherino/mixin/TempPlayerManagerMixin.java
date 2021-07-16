package torcherino.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import torcherino.temp.PlayerConnectCallback;
import torcherino.temp.PlayerDisconnectCallback;

@Mixin(PlayerList.class)
public abstract class TempPlayerManagerMixin
{
    @Inject(method = "placeNewPlayer(Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;addNewPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void torcherino_onPlayerConnected(final Connection connection, final ServerPlayer player, final CallbackInfo ci)
    {
        PlayerConnectCallback.EVENT.invoker().onPlayerConnected(player);
    }

    @Inject(method = "remove(Lnet/minecraft/server/level/ServerPlayer;)V", at = @At("HEAD"))
    private void torcherino_onPlayerDisconnected(final ServerPlayer player, final CallbackInfo ci)
    {
        PlayerDisconnectCallback.EVENT.invoker().onPlayerDisconnected(player);
    }
}
