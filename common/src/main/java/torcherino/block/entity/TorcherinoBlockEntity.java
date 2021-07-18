package torcherino.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import torcherino.api.Tier;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoAPI;
import torcherino.config.Config;
import torcherino.platform.NetworkUtils;

public class TorcherinoBlockEntity extends BlockEntity implements Nameable, TickableBlockEntity, TierSupplier {
    public static int randomTicks;
    private Component customName;
    private int xRange, yRange, zRange, speed, redstoneMode;
    private Iterable<BlockPos> area;
    private boolean active;
    private ResourceLocation tierID;
    private String uuid = "";

    public TorcherinoBlockEntity() {
        super(Registry.BLOCK_ENTITY_TYPE.get(new ResourceLocation("torcherino", "torcherino")));
    }

    @Override
    public boolean hasCustomName() {
        return customName != null;
    }

    @Override
    public Component getCustomName() {
        return customName;
    }

    public void setCustomName(Component name) {
        customName = name;
    }

    private String getOwner() {
        return uuid;
    }

    public void setOwner(String s) {
        uuid = s;
    }

    @Override
    public Component getName() {
        return hasCustomName() ? customName : new TranslatableComponent(getBlockState().getBlock().getDescriptionId());
    }

    // Invoked by mixin or overrides forge's IForgeTileEntity#onLoad method
    public void onLoad() {
        if (level.isClientSide) {
            return;
        }
        area = BlockPos.betweenClosed(worldPosition.getX() - xRange, worldPosition.getY() - yRange, worldPosition.getZ() - zRange,
                worldPosition.getX() + xRange, worldPosition.getY() + yRange, worldPosition.getZ() + zRange);
        level.getServer().tell(new TickTask(level.getServer().getTickCount(), () -> getBlockState().neighborChanged(level, worldPosition, null, null, false)));
    }

    @Override
    public void tick() {
        if (!active || speed == 0 || (xRange == 0 && yRange == 0 && zRange == 0)) {
            return;
        }
        if (!Config.INSTANCE.online_mode.equals("") && !NetworkUtils.getInstance().s_isPlayerOnline(this.getOwner())) {
            return;
        }
        // todo: get on load and then when updated
        randomTicks = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        area.forEach(this::tickBlock);
    }

    private void tickBlock(BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();
        if (TorcherinoAPI.INSTANCE.isBlockBlacklisted(block)) {
            return;
        }
        if (level instanceof ServerLevel && block.isRandomlyTicking(blockState) &&
                level.getRandom().nextInt(Mth.clamp(4096 / (speed * Config.INSTANCE.random_tick_rate), 1, 4096)) < randomTicks) {
            blockState.randomTick((ServerLevel) level, pos, level.getRandom());
        }
        if (!block.isEntityBlock()) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.isRemoved() || TorcherinoAPI.INSTANCE.isBlockEntityBlacklisted(blockEntity.getType()) ||
                !(blockEntity instanceof TickableBlockEntity tickableBlockEntity)) {
            return;
        }
        for (int i = 0; i < speed; i++) {
            if (blockEntity.isRemoved()) {
                break;
            }
            tickableBlockEntity.tick();
        }
    }

    public boolean readClientData(int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        Tier tier = TorcherinoAPI.INSTANCE.getTiers().get(getTier());
        if (this.valueInRange(xRange, 0, tier.getXZRange()) &&
                this.valueInRange(zRange, 0, tier.getXZRange()) &&
                this.valueInRange(yRange, 0, tier.getYRange()) &&
                this.valueInRange(speed, 0, tier.getMaxSpeed()) &&
                this.valueInRange(redstoneMode, 0, 3)) {
            this.xRange = xRange;
            this.zRange = zRange;
            this.yRange = yRange;
            this.speed = speed;
            this.redstoneMode = redstoneMode;
            area = BlockPos.betweenClosed(worldPosition.getX() - xRange, worldPosition.getY() - yRange, worldPosition.getZ() - zRange,
                    worldPosition.getX() + xRange, worldPosition.getY() + yRange, worldPosition.getZ() + zRange);
            this.getBlockState().neighborChanged(level, worldPosition, null, null, false);
            return true;
        }
        return false;
    }

    private boolean valueInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    @Override
    public ResourceLocation getTier() {
        if (tierID == null) {
            Block block = this.getBlockState().getBlock();
            if (block instanceof TierSupplier supplier) {
                tierID = supplier.getTier();
            }
        }
        return tierID;
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
        if (this.hasCustomName()) {
            tag.putString("CustomName", Component.Serializer.toJson(getCustomName()));
        }
        tag.putInt("XRange", xRange);
        tag.putInt("ZRange", zRange);
        tag.putInt("YRange", yRange);
        tag.putInt("Speed", speed);
        tag.putInt("RedstoneMode", redstoneMode);
        tag.putBoolean("Active", active);
        tag.putString("Owner", getOwner() == null ? "" : getOwner());
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        if (tag.contains("CustomName", 8)) {
            this.setCustomName(Component.Serializer.fromJson(tag.getString("CustomName")));
        }
        xRange = tag.getInt("XRange");
        zRange = tag.getInt("ZRange");
        yRange = tag.getInt("YRange");
        speed = tag.getInt("Speed");
        redstoneMode = tag.getInt("RedstoneMode");
        active = tag.getBoolean("Active");
        uuid = tag.getString("Owner");
    }

    public void openTorcherinoScreen(ServerPlayer player) {
        NetworkUtils.getInstance().s2c_openTorcherinoScreen(player, worldPosition, this.getName(), xRange, zRange, yRange, speed, redstoneMode);
    }
}
