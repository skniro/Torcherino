package torcherino.platform;

import com.google.common.base.Suppliers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.function.Supplier;

public class PlatformUtilsImpl implements PlatformUtils {
    private static final Supplier<PlatformUtils> instance = Suppliers.memoize(PlatformUtilsImpl::new);

    public static PlatformUtils getInstance() {
        return instance.get();
    }

    @Override
    public boolean isDedicatedServer() {
        return FMLLoader.getDist() == Dist.DEDICATED_SERVER;
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get().resolve("sci4me/Torcherino.cfg");
    }
}
