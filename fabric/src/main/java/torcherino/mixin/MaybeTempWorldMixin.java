package torcherino.mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import torcherino.api.blocks.entity.ExpandedBlockEntity;

@Mixin(Level.class)
public abstract class MaybeTempWorldMixin {
    @Inject(method = "addBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)Z", at = @At("TAIL"))
    private void torcherino_addBlockEntity(final BlockEntity be, final CallbackInfoReturnable<Boolean> cir) {
        if (be instanceof ExpandedBlockEntity blockEntity) {
            blockEntity.onLoad();
        }
    }
}
