package torcherino.mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import torcherino.block.entity.TorcherinoBlockEntity;

@Mixin(Level.class)
public abstract class BlockEntityAddedToLevelMixin {
    // todo: need to inject after a ticker has been added but also get access to the corresponding BlockEntity
    //@Inject(method = "addBlockEntityTicker(Lnet/minecraft/world/level/block/entity/TickingBlockEntity;)V", at = @At("TAIL"))
    //private void torcherino_addBlockEntity(TickingBlockEntity be, CallbackInfoReturnable<Boolean> cir) {
    //    if (be instanceof TorcherinoBlockEntity blockEntity) {
    //        blockEntity.onLoad();
    //    }
    //}
}
