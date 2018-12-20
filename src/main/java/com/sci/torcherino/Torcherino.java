package com.sci.torcherino;


import java.io.File;
import java.util.HashMap;
import org.apache.logging.log4j.Logger;
import com.sci.torcherino.blocks.ModBlocks;
import com.sci.torcherino.blocks.tiles.TileCompressedTorcherino;
import com.sci.torcherino.blocks.tiles.TileDoubleCompressedTorcherino;
import com.sci.torcherino.blocks.tiles.TileTorcherino;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid=Torcherino.MOD_ID, name=Torcherino.MOD_NAME, version="7.5", acceptedMinecraftVersions="[1.12,1.12.2]", useMetadata=true)
public class Torcherino 
{
	public static boolean logPlacement;
	public static boolean overPoweredRecipe;
	public static boolean compressedTorcherino;
	public static boolean doubleCompressedTorcherino;
	public static Configuration config;
	public static HashMap<EntityPlayer, Boolean> keyStates = new HashMap<EntityPlayer, Boolean>();
	public static Logger logger;
	public static SimpleNetworkWrapper network;
	public static final String MOD_ID = "torcherino";
	public static final String MOD_NAME = "Torcherino";
	private static String[] blacklistedBlocks;
	private static String[] blacklistedTiles;

	@Mod.Instance(Torcherino.MOD_ID)
	public static Torcherino instance;
	
	@SidedProxy(clientSide="com.sci.torcherino.ClientProxy", serverSide="com.sci.torcherino.CommonProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		network = NetworkRegistry.INSTANCE.newSimpleChannel(Torcherino.MOD_NAME);
		File folder = new File(event.getModConfigurationDirectory(), "sci4me");
        if(!folder.exists()) folder.mkdir();
        Configuration config = new Configuration(new File(folder, Torcherino.MOD_NAME+".cfg"));
		try
		{
	        config.load();
	        logPlacement = config.getBoolean("logPlacement", "general", false, "(For Server Owners) Is it logged when someone places a Torcherino?");
	        overPoweredRecipe = config.getBoolean("overPoweredRecipe", "general", true, "Is the recipe for Torcherino extremely OP?");
	        compressedTorcherino = config.getBoolean("compressedTorcherino", "general", false, "Is the recipe for the Compressed Torcherino enabled?");
	        doubleCompressedTorcherino = config.getBoolean("doubleCompressedTorcherino", "general", false, "Is the recipe for the Double Compressed Torcherino enabled? Only takes effect if Compressed Torcherinos are enabled.");
	        blacklistedBlocks = config.getStringList("blacklistedBlocks", "blacklist", new String[]{}, "modid:unlocalized");
	        blacklistedTiles = config.getStringList("blacklistedTiles", "blacklist", new String[]{}, "Fully qualified class name");
	    }
		finally
		{
	        if(config.hasChanged()) config.save();
	    }
		proxy.preInit();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		logger.info(TileTorcherino.class);
		TorcherinoRegistry.blacklistBlock(Blocks.AIR);
		TorcherinoRegistry.blacklistBlock(Blocks.WATER);
		TorcherinoRegistry.blacklistBlock(Blocks.FLOWING_WATER);
		TorcherinoRegistry.blacklistBlock(Blocks.LAVA);
		TorcherinoRegistry.blacklistBlock(Blocks.FLOWING_LAVA);
		TorcherinoRegistry.blacklistBlock(ModBlocks.torcherino);
		TorcherinoRegistry.blacklistBlock(ModBlocks.compressedTorcherino);
		TorcherinoRegistry.blacklistBlock(ModBlocks.doubleCompressedTorcherino);
		TorcherinoRegistry.blacklistBlock(ModBlocks.lanterino);
		TorcherinoRegistry.blacklistBlock(ModBlocks.compressedLanterino);
		TorcherinoRegistry.blacklistBlock(ModBlocks.doubleCompressedLanterino);
		TorcherinoRegistry.blacklistTile(TileTorcherino.class);
		TorcherinoRegistry.blacklistTile(TileCompressedTorcherino.class);
		TorcherinoRegistry.blacklistTile(TileDoubleCompressedTorcherino.class);
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		if(Loader.isModLoaded("projecte"))
		{
			TorcherinoRegistry.blacklistString("projecte:dm_pedestal");
		}
		for(String block : blacklistedBlocks)
		{
			TorcherinoRegistry.blacklistString(block);
		}
        for(String tile : blacklistedTiles)
        {
        	TorcherinoRegistry.blacklistString(tile);
        }
	}
	
	@Mod.EventHandler
    public void imcMessage(FMLInterModComms.IMCEvent event)
	{
        for(FMLInterModComms.IMCMessage message : event.getMessages())
        {
            if(!message.isStringMessage())
            {
                logger.info("Received non-string message! Ignoring");
                continue;
            }
            String s = message.getStringValue();
            TorcherinoRegistry.blacklistString(s);
        }
    }
}
