package torcherino_unofficial;

import net.fabricmc.api.ModInitializer;
import torcherino.api.entrypoints.TorcherinoInitializer;

public final class TorcherinoUnofficial implements ModInitializer, TorcherinoInitializer {
    public static final String MOD_ID = "torcherino_unofficial";

    @Override
    public void onInitialize() {

    }

    @Override
    public void onTorcherinoInitialize() {
        UnofficialContent.INSTANCE.initialise();
    }
}
