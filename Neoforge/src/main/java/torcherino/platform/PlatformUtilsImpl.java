package torcherino.platform;

import com.google.common.base.Suppliers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.function.Supplier;

public final class PlatformUtilsImpl implements PlatformUtils {
    private static final Supplier<PlatformUtils> instance = Suppliers.memoize(PlatformUtilsImpl::new);

    public static PlatformUtils getInstance() {
        return instance.get();
    }

    @Override
    public boolean isDedicatedServer() {
        return FMLLoader.getDist() == Dist.DEDICATED_SERVER;
    }

    @Override
    public Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get().resolve("sci4me/Torcherino.cfg");
    }
}
