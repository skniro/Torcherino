package torcherino_unofficial;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TorcherinoUnofficial.MOD_ID)
public final class TorcherinoUnofficial {
    public static final String MOD_ID = "torcherino_unofficial";

    public TorcherinoUnofficial(FMLJavaModLoadingContext context) {
        UnofficialContent.initialise(context.getModBusGroup());
    }
}
