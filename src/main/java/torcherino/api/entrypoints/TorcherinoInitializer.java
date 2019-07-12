package torcherino.api.entrypoints;

@SuppressWarnings("SpellCheckingInspection")
@FunctionalInterface
public interface TorcherinoInitializer
{
    /**
     * Here you should only blacklist blocks or block entities, this can be done in your ModInitializer, however using this in a separate class will not need
     * Torcherino API to be bundled at all. Please don't access the TorcherinoImpl class directly, use the TorcherinoAPI.INSTANCE field.
     *
     * @since 1.9.51
     */
    void onTorcherinoInitialize();
}
