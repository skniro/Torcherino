package com.sci.torcherino.init;

import com.sci.torcherino.Torcherino;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModRecipes {
    public static void init() {
        if (Torcherino.overPoweredRecipe) {
            GameRegistry.addShapedRecipe(ModBlocks.torcherino.getRegistryName(), null, new ItemStack(ModBlocks.torcherino), new Object[] {" C ", "CTC", " C ", 'C', Items.CLOCK, 'T', Blocks.TORCH});
        }else {
        	GameRegistry.addShapedRecipe(ModBlocks.torcherino.getRegistryName(), null, new ItemStack(ModBlocks.torcherino), new Object[] {"SCS", "CTC", "SCS", 'C', Items.CLOCK, 'S', Items.NETHER_STAR, 'T', Blocks.TORCH});
        }
        if (Torcherino.compressedTorcherino) {
        	GameRegistry.addShapedRecipe(ModBlocks.compressedTorcherino.getRegistryName(), null, new ItemStack(ModBlocks.compressedTorcherino), new Object[] {"ttt", "ttt", "ttt", 't', ModBlocks.torcherino});
            if (Torcherino.doubleCompressedTorcherino) {
            	GameRegistry.addShapedRecipe(ModBlocks.doubleCompressedTorcherino.getRegistryName(), null, new ItemStack(ModBlocks.doubleCompressedTorcherino), new Object[] {"ttt", "ttt", "ttt", 't', ModBlocks.compressedTorcherino});
            }
        }
    }
}