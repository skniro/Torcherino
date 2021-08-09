package torcherino.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CandleBlockEntity extends TorcherinoBlockEntity {
    public CandleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected boolean readClientData(int xRange, int zRange, int yRange, int speed) {
        boolean rv = super.readClientData(xRange, zRange ,yRange, speed);
        if (rv) {
            this.getBlockState().neighborChanged(level, worldPosition, null, null, false);
        }
        return rv;
    }

    public void setCandleLit(boolean lit) {
        active = lit;
    }
}
