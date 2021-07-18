package torcherino.mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import torcherino.block.entity.TorcherinoBlockEntity;

@Mixin(Level.class)
public abstract class BlockEntityAddedToLevelMixin {
    @Inject(method = "addBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)Z", at = @At("TAIL"))
    private void torcherino_addBlockEntity(BlockEntity be, CallbackInfoReturnable<Boolean> cir) {
        if (be instanceof TorcherinoBlockEntity blockEntity) {
            blockEntity.onLoad();
        }
    }
}
