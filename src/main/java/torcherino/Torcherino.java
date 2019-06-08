package torcherino;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import torcherino.blocks.ModBlocks;
import torcherino.client.gui.TorcherinoScreen;
import torcherino.blocks.miscellaneous.TorcherinoTileEntity;
import torcherino.items.ModItems;

@Mod(Utilities.MOD_ID)
public class Torcherino
{

	public Torcherino()
	{
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		TorcherinoTiers.INSTANCE.initialise();
		// todo load this from config:
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("normal"), 4, 9, 3);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("compressed"), 36, 9, 3);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("double_compressed"), 324, 9, 3);
		ModBlocks.INSTANCE.initialise();
		eventBus.register(ModBlocks.INSTANCE);
		eventBus.register(ModItems.INSTANCE);
		//FMLJavaModLoadingContext.get().getModEventBus().register(this);
		//MinecraftForge.EVENT_BUS.register(this);
		//MinecraftForge.EVENT_BUS.register(ModBlocks.INSTANCE);

		if (FMLEnvironment.dist.isClient())
		{
			ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () ->
			{
				return openContainer ->
				{
					BlockPos pos = openContainer.getAdditionalData().readBlockPos();
					EntityPlayerSP player = Minecraft.getInstance().player;
					TileEntity tile = player.world.getTileEntity(pos);
					if (tile instanceof TorcherinoTileEntity)
					{
						return new TorcherinoScreen((TorcherinoTileEntity) tile);
					}
					return null;
				};
			});
		}
	}
}