package torcherino_unofficial;

import torcherino.api.entrypoints.TorcherinoInitializer;

public final class TorcherinoUnofficial implements TorcherinoInitializer {
    public static final String MOD_ID = "torcherino_unofficial";

    @Override
    public void onTorcherinoInitialize() {
        UnofficialContent.initialise();
    }
}
