package torcherino.temp;

import com.mojang.datafixers.types.Type;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import torcherino.api.TierSupplier;
import torcherino.block.entity.TorcherinoBlockEntity;

// todo: nuke this
@Deprecated
@SuppressWarnings("SpellCheckingInspection")
public class TocherinoBlockEntityType extends BlockEntityType<TorcherinoBlockEntity> {
    public TocherinoBlockEntityType(BlockEntitySupplier<TorcherinoBlockEntity> supplier, Type type) {
        super(supplier, null, type);
    }

    @Override
    public boolean isValid(BlockState state) {
        return state.getBlock() instanceof TierSupplier;
    }
}
