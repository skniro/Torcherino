package io.github.ninjaphenix.torcherino;

import io.github.ninjaphenix.torcherino.block.BlockLanterino;
import io.github.ninjaphenix.torcherino.block.BlockTorcherino;
import io.github.ninjaphenix.torcherino.block.BlockTorcherinoWall;
import io.github.ninjaphenix.torcherino.tiles.TileTorcherino;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemWallOrFloor;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import org.dimdev.rift.listener.BlockAdder;
import org.dimdev.rift.listener.ItemAdder;

import java.util.ArrayList;

public class Torcherino implements BlockAdder, ItemAdder
{

    public static final TileEntityType<TileTorcherino> TORCHERINO = TileEntityType.registerTileEntityType("torcherino", TileEntityType.Builder.create(() -> new TileTorcherino()));
    ArrayList<ItemBlock> b = new ArrayList<ItemBlock>();
    @Override
    public void registerBlocks()
    {
        Utils.info("Registering blocks");

        registerTorcherino("torcherino", 4);
        registerTorcherino("compressed_torcherino", 36);
        registerTorcherino("double_compressed_torcherino", 324);
        registerLanterino("lanterino", 4);
        registerLanterino("compressed_lanterino", 36);
        registerLanterino("double_compressed_lanterino", 324);
        Utils.blacklistBlock(Blocks.AIR);
        Utils.blacklistBlock(Blocks.WATER);
        Utils.blacklistBlock(Blocks.LAVA);

        //Utils.blacklistBlock(ModBlocks.lanterino);
        //Utils.blacklistBlock(ModBlocks.compressedLanterino);
        //Utils.blacklistBlock(ModBlocks.doubleCompressedLanterino);
        Utils.blacklistTile(TileTorcherino.class);
    }

    public void registerTorcherino(String type, int speed)
    {
        Block blockTorcherino = new BlockTorcherino(speed);
        Block blockTorcherinoWall = new BlockTorcherinoWall(speed);

        Block.register(new ResourceLocation(Utils.MOD_ID, type), blockTorcherino);
        Block.register(new ResourceLocation(Utils.MOD_ID, "wall_"+type), blockTorcherinoWall);
        b.add(new ItemWallOrFloor(blockTorcherino, blockTorcherinoWall, (new Item.Builder().group(ItemGroup.DECORATIONS))));
        Utils.blacklistBlock(blockTorcherino);
        Utils.blacklistBlock(blockTorcherinoWall);
    }
    public void registerLanterino(String type, int speed)
    {
        Block blockLanterino = new BlockLanterino(speed);
        Block.register(new ResourceLocation(Utils.MOD_ID, type), blockLanterino);
        b.add(new ItemBlock(blockLanterino, new Item.Builder().group(ItemGroup.DECORATIONS)));
        Utils.blacklistBlock(blockLanterino);
    }
    // done to fix the ordering in creative tab
    @Override
    public void registerItems()
    {
        b.forEach((ItemBlock bl)->Item.register(bl));
    }

}
