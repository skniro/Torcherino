package com.sci.torcherino.init;

import com.sci.torcherino.block.BlockCompressedTorcherino;
import com.sci.torcherino.block.BlockDoubleCompresedTorcherino;
import com.sci.torcherino.block.BlockTorcherino;
import com.sci.torcherino.tile.TileCompressedTorcherino;
import com.sci.torcherino.tile.TileDoubleCompressedTorcherino;
import com.sci.torcherino.tile.TileTorcherino;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GameRegistry.ObjectHolder("torcherino")
public final class ModBlocks {
    public static BlockTorcherino torcherino;
    public static BlockTorcherino compressedTorcherino;
    public static BlockTorcherino doubleCompressedTorcherino;

    private static Map<String, ItemBlock> remap = new HashMap<String, ItemBlock>();

    public static void init() {
        torcherino = new BlockTorcherino();
        compressedTorcherino = new BlockCompressedTorcherino();
        doubleCompressedTorcherino = new BlockDoubleCompresedTorcherino();
        ForgeRegistries.BLOCKS.register(torcherino.setRegistryName("blocktorcherino"));
        ForgeRegistries.BLOCKS.register(compressedTorcherino.setRegistryName("blockcompressedtorcherino"));
        ForgeRegistries.BLOCKS.register(doubleCompressedTorcherino.setRegistryName("blockdoublecompressedtorcherino"));
        ForgeRegistries.ITEMS.register(new ItemBlock(torcherino).setRegistryName(torcherino.getRegistryName()));
        ForgeRegistries.ITEMS.register(new ItemBlock(compressedTorcherino).setRegistryName(compressedTorcherino.getRegistryName()));
        ForgeRegistries.ITEMS.register(new ItemBlock(doubleCompressedTorcherino).setRegistryName(doubleCompressedTorcherino.getRegistryName()));
        GameRegistry.registerTileEntity(TileTorcherino.class, "torcherino_tile");
        GameRegistry.registerTileEntity(TileCompressedTorcherino.class, "compressed_torcherino_tile");
        GameRegistry.registerTileEntity(TileDoubleCompressedTorcherino.class, "double_compressed_torcherino_tile");
        remap.put("torcherino:tile.torcherino", (ItemBlock) Item.getItemFromBlock(torcherino));
        remap.put("torcherino:tile.compressed_torcherino", (ItemBlock) Item.getItemFromBlock(compressedTorcherino));
        remap.put("torcherino:tile.double_compressed_torcherino", (ItemBlock) Item.getItemFromBlock(doubleCompressedTorcherino));
    }

    public static void initRenders() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(torcherino), 0, new ModelResourceLocation(new ResourceLocation("torcherino", "blocktorcherino"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(compressedTorcherino), 0, new ModelResourceLocation(new ResourceLocation("torcherino", "blockcompressedtorcherino"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(doubleCompressedTorcherino), 0, new ModelResourceLocation(new ResourceLocation("torcherino", "blockdoublecompressedtorcherino"), "inventory"));
    }

    public static void handleMissingMappings(RegistryEvent.MissingMappings<Block> event) {
    	List<Mapping<Block>> list = event.getMappings();
    	for(Mapping<Block> mapping : list)
		{
    		if (remap.containsKey(mapping.key.toString())) {
    			mapping.remap(remap.get(mapping.key.toString()).getBlock());
           }
		}
    }
}