package torcherino;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import torcherino.blocks.ModBlocks;
import torcherino.config.Manager;
import torcherino.items.ModItems;
import torcherino.network.Networker;

@Mod(Utilities.MOD_ID)
public class Torcherino
{
	public Torcherino()
	{
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		TorcherinoTiers.INSTANCE.initialise();
		// todo load this from config:

		String serializedData;
		serializedData = Manager.serialize("Hello World");
		Utilities.LOGGER.info("Serialized: {}", serializedData);
		Utilities.LOGGER.info("Deserialized: " + Manager.deserialize(serializedData, String.class));
		serializedData = Manager.serialize(132);
		Utilities.LOGGER.info("Serialized: {}", serializedData);
		Utilities.LOGGER.info("Deserialized: " + Manager.deserialize(serializedData, Integer.class));
		serializedData = Manager.serialize(Utilities.resloc("normal_torcherino"));
		Utilities.LOGGER.info("Serialized: {}", serializedData);
		Utilities.LOGGER.info("Deserialized: " + Manager.deserialize(serializedData, ResourceLocation.class));
		serializedData = Manager.serialize(true);
		Utilities.LOGGER.info("Serialized: {}", serializedData);
		Utilities.LOGGER.info("Deserialized: " + Manager.deserialize(serializedData, Boolean.class));
		serializedData = Manager.serialize(Boolean.FALSE);
		Utilities.LOGGER.info("Serialized: {}", serializedData);
		Utilities.LOGGER.info("Deserialized: " + Manager.deserialize(serializedData, Boolean.class));
		serializedData = Manager.serialize(new Integer[]{1, 2, 6, 4});
		Utilities.LOGGER.info("Serialized: {}", serializedData);
		Utilities.LOGGER.info("Deserialized: " + Manager.deserialize(serializedData, Integer[].class));
		serializedData = Manager.serialize(new ResourceLocation[]{Utilities.resloc("ok"), new ResourceLocation("dirt")});
		Utilities.LOGGER.info("Serialized: {}", serializedData);
		Utilities.LOGGER.info("Deserialized: " + Manager.deserialize(serializedData, ResourceLocation[].class));
		System.exit(0);

		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("normal"), 4, 4, 1);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("compressed"), 36, 4, 1);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("double_compressed"), 324, 4, 1);
		ModBlocks.INSTANCE.initialise();
		eventBus.register(ModBlocks.INSTANCE);
		eventBus.register(ModItems.INSTANCE);
		Networker.INSTANCE.initialise();
	}
}