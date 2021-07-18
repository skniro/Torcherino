package torcherino.platform;

import com.google.common.base.Suppliers;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.function.Supplier;

public final class PlatformUtilsImpl implements PlatformUtils {
    private static final Supplier<PlatformUtils> instance = Suppliers.memoize(PlatformUtilsImpl::new);

    public static PlatformUtils getInstance() {
        return instance.get();
    }

    @Override
    public boolean isDedicatedServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    @Override
    public Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("sci4me/Torcherino.cfg");
    }
}
