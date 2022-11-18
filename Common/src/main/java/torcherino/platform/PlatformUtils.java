package torcherino.platform;

import java.nio.file.Path;

public interface PlatformUtils {
    static PlatformUtils getInstance() {
        return Services.PLATFORM;
    }

    boolean isDedicatedServer();

    Path getConfigPath();
}
