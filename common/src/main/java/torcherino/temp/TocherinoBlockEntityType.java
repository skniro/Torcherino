package torcherino.temp;

import com.mojang.datafixers.types.Type;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import torcherino.api.TierSupplier;

// todo: nuke this
@Deprecated
@SuppressWarnings("SpellCheckingInspection")
public class TocherinoBlockEntityType<T extends BlockEntity> extends BlockEntityType<T> {
    public TocherinoBlockEntityType(BlockEntityType.BlockEntitySupplier<T> supplier, Type type) {
        super(supplier, null, type);
    }

    @Override
    public boolean isValid(BlockState state) {
        return state.getBlock() instanceof TierSupplier;
    }
}
