package torcherino.mixin;

import com.google.common.collect.BiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import torcherino.block.CopperLanterinoBlock;
import torcherino.block.WeatheringLanterinoBlock;

import static torcherino.block.WeatheringLanterinoBlock.swapPreserveTorcherinoBE;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = useOnContext.getLevel().getBlockState(pos);
        Player player = useOnContext.getPlayer();
        ItemStack stack = useOnContext.getItemInHand();
        if (state.getBlock() instanceof CopperLanterinoBlock) {
            if (!level.isClientSide()) {
                BiMap<Block, Block> waxOffMap = (BiMap<Block, Block>) HoneycombItem.WAX_OFF_BY_BLOCK.get();
                Block unwaxed = waxOffMap.get(state.getBlock());
                if (unwaxed != null) {
                    swapPreserveTorcherinoBE((ServerLevel) level, pos, state, unwaxed.defaultBlockState());
                    stack.hurtAndBreak(1, (ServerLevel) level, null, (p) -> {
                    });
                    level.levelEvent(player, 3005, pos, 0);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
            if (state.getBlock() instanceof WeatheringLanterinoBlock) {
                if (!level.isClientSide()) {
                    BiMap<Block, Block> prevMap = (BiMap<Block, Block>) WeatheringCopper.PREVIOUS_BY_BLOCK.get();
                    Block prev = prevMap.get(state.getBlock());
                    if (prev != null) {
                        swapPreserveTorcherinoBE((ServerLevel) level, pos, state, prev.defaultBlockState());
                        stack.hurtAndBreak(1, (ServerLevel) level, null, (p) -> {
                        });
                        level.levelEvent(player, 3004, pos, 0);
                        cir.setReturnValue(InteractionResult.SUCCESS);
                    }
                }
            }
        }
    }
}
