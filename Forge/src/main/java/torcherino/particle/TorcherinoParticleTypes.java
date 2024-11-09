package torcherino.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import torcherino.Torcherino;

import java.util.function.Supplier;

public class TorcherinoParticleTypes {
    public static final DeferredRegister <ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Torcherino.MOD_ID);
    public static final RegistryObject<SimpleParticleType> Normal_Torcherino_Flame = register("flame",() -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> Compressed_Torcherino_Flame = register("compressed_flame",() -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> Double_Compressed_Torcherino_Flame = register("double_compressed_flame",() -> new SimpleParticleType(false));

    public static <T extends ParticleType<?>> RegistryObject<T> register(String name, Supplier<T> particleType){
        return PARTICLE_TYPES.register(name, particleType);
    }
}
