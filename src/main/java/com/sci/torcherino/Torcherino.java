package com.sci.torcherino;

import com.sci.torcherino.init.ModBlocks;
import com.sci.torcherino.proxy.CommonProxy;
import com.sci.torcherino.tile.TileCompressedTorcherino;
import com.sci.torcherino.tile.TileDoubleCompressedTorcherino;
import com.sci.torcherino.tile.TileTorcherino;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = "torcherino", name = "Torcherino", version="7.1", acceptedMinecraftVersions="[1.12,1.12.2]", useMetadata=true)
public final class Torcherino {
    private static Torcherino instance;

    @Mod.InstanceFactory
    public static Torcherino instance() {
        if (Torcherino.instance == null) {
            Torcherino.instance = new Torcherino();
        }
        return Torcherino.instance;
    }

    @SidedProxy(clientSide = "com.sci.torcherino.proxy.ClientProxy", serverSide = "com.sci.torcherino.proxy.ServerProxy")
    public static CommonProxy proxy;

    public static boolean logPlacement;
    public static boolean overPoweredRecipe;
    public static boolean compressedTorcherino;
    public static boolean doubleCompressedTorcherino;

    private String[] blacklistedBlocks;
    private String[] blacklistedTiles;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        Torcherino.logger = evt.getModLog();

        final File folder = new File(evt.getModConfigurationDirectory(), "sci4me");

        if (!folder.exists())
            folder.mkdir();

        final Configuration cfg = new Configuration(new File(folder, "Torcherino.cfg"));
        try {
            cfg.load();

            Torcherino.logPlacement = cfg.getBoolean("logPlacement", "general", false, "(For Server Owners) Is it logged when someone places a Torcherino?");
            Torcherino.overPoweredRecipe = cfg.getBoolean("overPoweredRecipe", "general", true, "Is the recipe for Torcherino extremely OP?");
            Torcherino.compressedTorcherino = cfg.getBoolean("compressedTorcherino", "general", false, "Is the recipe for the Compressed Torcherino enabled?");
            Torcherino.doubleCompressedTorcherino = cfg.getBoolean("doubleCompressedTorcherino", "general", false, "Is the recipe for the Double Compressed Torcherino enabled? Only takes effect if Compressed Torcherinos are enabled.");

            this.blacklistedBlocks = cfg.getStringList("blacklistedBlocks", "blacklist", new String[]{}, "modid:unlocalized");
            this.blacklistedTiles = cfg.getStringList("blacklistedTiles", "blacklist", new String[]{}, "Fully qualified class name");
        } finally {
            if (cfg.hasChanged())
                cfg.save();
        }

        ModBlocks.init();

        Torcherino.proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
        TorcherinoRegistry.blacklistBlock(Blocks.AIR);

        TorcherinoRegistry.blacklistBlock(ModBlocks.torcherino);
        TorcherinoRegistry.blacklistBlock(ModBlocks.compressedTorcherino);
        TorcherinoRegistry.blacklistBlock(ModBlocks.doubleCompressedTorcherino);

        TorcherinoRegistry.blacklistTile(TileTorcherino.class);
        TorcherinoRegistry.blacklistTile(TileCompressedTorcherino.class);
        TorcherinoRegistry.blacklistTile(TileDoubleCompressedTorcherino.class);

        TorcherinoRegistry.blacklistBlock(Blocks.WATER);
        TorcherinoRegistry.blacklistBlock(Blocks.FLOWING_WATER);

        TorcherinoRegistry.blacklistBlock(Blocks.LAVA);
        TorcherinoRegistry.blacklistBlock(Blocks.FLOWING_LAVA);

        Torcherino.proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        for (final String block : this.blacklistedBlocks)
            this.blacklistBlock(block);

        for (final String tile : this.blacklistedTiles)
            this.blacklistTile(tile);

        Torcherino.proxy.postInit();
    }

    @SubscribeEvent
    public void missingMapping(RegistryEvent.MissingMappings<Block> evt) {
        ModBlocks.handleMissingMappings(evt);
    }
    @Mod.EventHandler
    public void imcMessage(FMLInterModComms.IMCEvent evt) {
        for (final FMLInterModComms.IMCMessage message : evt.getMessages()) {
            if (!message.isStringMessage()) {
                System.out.println("Received non-string message! Ignoring");
                continue;
            }

            final String s = message.getStringValue();

            if (message.key.equals("blacklist-block"))
                this.blacklistBlock(s);
            else if (message.key.equals("blacklist-tile"))
                this.blacklistTile(s);
        }
    }

    private void blacklistBlock(String s) {
        final String[] parts = s.split(":");

        if (parts.length != 2) {
            System.out.println("Received malformed message: " + s);
            return;
        }

        final Block block = Block.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));

        if (block == null) {
            System.out.println("Could not find block: " + s + ", ignoring");
            return;
        }

        System.out.println("Blacklisting block: " + block.getUnlocalizedName());

        TorcherinoRegistry.blacklistBlock(block);
    }

    @SuppressWarnings("unchecked")
    private void blacklistTile(String s) {
        try {
            final Class<?> clazz = this.getClass().getClassLoader().loadClass(s);

            if (clazz == null) {
                System.out.println("Class null: " + s);
                return;
            }

            if (!TileEntity.class.isAssignableFrom(clazz)) {
                System.out.println("Class not a TileEntity: " + s);
                return;
            }

            TorcherinoRegistry.blacklistTile((Class<? extends TileEntity>) clazz);
        } catch (final ClassNotFoundException e) {
            System.out.println("Class not found: " + s + ", ignoring");
        }
    }
}