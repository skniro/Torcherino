package torcherino_unofficial;

import net.fabricmc.api.ModInitializer;
import torcherino.api.entrypoints.TorcherinoInitializer;

public final class TorcherinoUnofficial implements ModInitializer {
    public static final String MOD_ID = "torcherino_unofficial";

    @Override
    public void onInitialize() {
        UnofficialContent.INSTANCE.initialise();
    }
}
