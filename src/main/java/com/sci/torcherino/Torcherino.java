package com.sci.torcherino;

import com.sci.torcherino.config.ConfigR;
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
    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        Torcherino.logger = evt.getModLog();

        final File folder = new File(evt.getModConfigurationDirectory(), "sci4me");

        if (!folder.exists())
            folder.mkdir();

        final Configuration cfg = new Configuration(new File(folder, "Torcherino.cfg"));
        ConfigR.init(cfg);
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
        ConfigR.postInit();
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
            TorcherinoRegistry.blacklistString(s);
        }
    }
}