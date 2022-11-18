package torcherino.platform;

import java.util.ServiceLoader;

public class Services {

    public static final NetworkUtils NETWORK = load(NetworkUtils.class);
    public static final PlatformUtils PLATFORM = load(PlatformUtils.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                                             .findFirst()
                                             .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        return loadedService;
    }
}
