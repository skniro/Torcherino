package torcherino.mixin;

import com.google.common.collect.BiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import torcherino.ModContent;
import torcherino.block.CopperLanterinoBlock;
import torcherino.block.WeatheringLanterinoBlock;
import torcherino.block.api.LanterinoOxidizableRegistry;

import static torcherino.block.WeatheringLanterinoBlock.swapPreserveTorcherinoBE;

@Mixin(HoneycombItem.class)
public abstract class HoneycombItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = useOnContext.getLevel().getBlockState(pos);
        Player player = useOnContext.getPlayer();
        ItemStack stack = useOnContext.getItemInHand();
        if (state.getBlock() instanceof WeatheringLanterinoBlock weatheringLanterinoBlock) {
            if (!level.isClientSide()) {
                Identifier tierID = weatheringLanterinoBlock.getTier();
                BiMap<Block, Block> waxables = (BiMap<Block, Block>) LanterinoOxidizableRegistry.getWaxMap(tierID);
                Block waxed = waxables.get(state.getBlock());
                if (waxed != null) {
                    swapPreserveTorcherinoBE((ServerLevel) level, pos, state, waxed.defaultBlockState());
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    level.levelEvent(player, 3003, pos, 0);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }
    }
}
