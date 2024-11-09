package torcherino.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import torcherino.Torcherino;

import java.util.function.Supplier;

public class TorcherinoParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Torcherino.MOD_ID);
    public static final Supplier<SimpleParticleType> Normal_Torcherino_Flame = register("flame",() -> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> Compressed_Torcherino_Flame = register("compressed_flame",() -> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> Double_Compressed_Torcherino_Flame = register("double_compressed_flame",() -> new SimpleParticleType(false));

    public static <T extends ParticleType<?>> Supplier<T> register(String name, Supplier<T> particleType){
        return PARTICLE_TYPES.register(name, particleType);
    }
}
