package torcherino.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import torcherino.platform.NetworkUtils;

public class RedstoneControlledTorcherinoBlockEntity extends TorcherinoBlockEntity {
    private int redstoneMode;

    public RedstoneControlledTorcherinoBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.BLOCK_ENTITY_TYPE.get(new ResourceLocation("torcherino", "torcherino")), pos, state);
    }

    public boolean readClientData(int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        if (this.valueInRange(redstoneMode, 0, 3)) {
            if (super.readClientData(xRange, zRange, yRange, speed)) {
                this.redstoneMode = redstoneMode;
                this.getBlockState().neighborChanged(level, worldPosition, null, null, false);
                return true;
            }
        }
        return false;
    }

    public void setPoweredByRedstone(boolean powered) {
        switch (redstoneMode) {
            case 0 -> active = !powered;
            case 1 -> active = powered;
            case 2 -> active = true;
            case 3 -> active = false;
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putInt("RedstoneMode", redstoneMode);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        redstoneMode = tag.getInt("RedstoneMode");
    }

    @Override
    public void openTorcherinoScreen(ServerPlayer player) {
        NetworkUtils.getInstance().s2c_openTorcherinoScreen(player, worldPosition, this.getName(), xRange, zRange, yRange, speed, redstoneMode);
    }
}
