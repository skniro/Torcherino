package torcherino.block.entity;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import torcherino.api.Tier;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoAPI;
import torcherino.config.Config;
import torcherino.platform.NetworkUtils;

public class TorcherinoBlockEntity extends BlockEntity implements Nameable, TierSupplier {
    public static int randomTicks;
    private Component customName;
    private int xRange, yRange, zRange, speed, redstoneMode;
    private Iterable<BlockPos> area;
    private boolean active;
    private Identifier tierID;
    private String uuid = "";

    public TorcherinoBlockEntity(BlockPos pos, BlockState state) {
        super(BuiltInRegistries.BLOCK_ENTITY_TYPE.get(Identifier.fromNamespaceAndPath("torcherino", "torcherino")).get().value(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TorcherinoBlockEntity entity) {
        if (!entity.active || entity.speed == 0 || (entity.xRange == 0 && entity.yRange == 0 && entity.zRange == 0)) {
            return;
        }
        if (!Config.INSTANCE.online_mode.equals("") && !NetworkUtils.getInstance().s_isPlayerOnline(entity.getOwner())) {
            return;
        }
        // todo: get on load and then when updated
        if (level instanceof ServerLevel serverlevel) {
            randomTicks = serverlevel.getGameRules().get(GameRules.RANDOM_TICK_SPEED);
        }
        entity.area.forEach(entity::tickBlock);
    }

    @Override
    public boolean hasCustomName() {
        return customName != null;
    }

    @Override
    public Component getCustomName() {
        return customName;
    }

    public Component getDisplayName() {
        return this.getName();
    }

    public void setCustomName(Component name) {
        customName = name;
    }

    public String getOwner() {
        return uuid;
    }

    public void setOwner(String s) {
        uuid = s;
    }

    @Override
    public @NotNull Component getName() {
        return hasCustomName() ? customName : Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public void setLevel(@NotNull Level level) {
        super.setLevel(level);
        if (!level.isClientSide()) {
            level.getServer().schedule(new TickTask(level.getServer().getTickCount(), () -> getBlockState().handleNeighborChanged(level, worldPosition, null, null, false)));
        }
    }

    private void tickBlock(BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();
        if (TorcherinoAPI.INSTANCE.isBlockBlacklisted(block)) {
            return;
        }
        if (level instanceof ServerLevel && blockState.isRandomlyTicking() && level.tickRateManager().runsNormally() &&
                level.getRandom().nextInt(Mth.clamp(4096 / (speed * Config.INSTANCE.random_tick_rate), 1, 4096)) < randomTicks) {
            blockState.randomTick((ServerLevel) level, pos, level.getRandom());
        }
        if (!(block instanceof EntityBlock entityBlock)) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            //noinspection unchecked
            BlockEntityTicker<BlockEntity> ticker = (BlockEntityTicker<BlockEntity>) entityBlock.getTicker(level, blockState, blockEntity.getType());
            if (blockEntity.isRemoved() || TorcherinoAPI.INSTANCE.isBlockEntityBlacklisted(blockEntity.getType()) || ticker == null) {
                return;
            }
            for (int i = 0; i < speed; i++) {
                if (blockEntity.isRemoved()) {
                    break;
                }
                ticker.tick(level, pos, blockState, blockEntity);
            }
        }
    }

    public boolean readClientData(int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        Tier tier = TorcherinoAPI.INSTANCE.getTiers().get(getTier());
        if (this.valueInRange(xRange, 0, tier.xzRange()) &&
                this.valueInRange(zRange, 0, tier.xzRange()) &&
                this.valueInRange(yRange, 0, tier.yRange()) &&
                this.valueInRange(speed, 0, tier.maxSpeed()) &&
                this.valueInRange(redstoneMode, 0, 3)) {
            this.xRange = xRange;
            this.zRange = zRange;
            this.yRange = yRange;
            this.speed = speed;
            this.redstoneMode = redstoneMode;
            area = BlockPos.betweenClosed(worldPosition.getX() - xRange, worldPosition.getY() - yRange, worldPosition.getZ() - zRange,
                    worldPosition.getX() + xRange, worldPosition.getY() + yRange, worldPosition.getZ() + zRange);
            this.getBlockState().handleNeighborChanged(level, worldPosition, null, null, false);
            return true;
        }
        return false;
    }

    private boolean valueInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    @Override
    public Identifier getTier() {
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
    public void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        if (this.hasCustomName()) {
            Component customName = this.getCustomName();
            JsonElement json = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, customName)
                                                           .getOrThrow(error -> {
                                                               throw new IllegalStateException("Failed to serialize component: " + error);
                                                           });
            tag.putString("CustomName", json.toString());
        }
        tag.putInt("XRange", xRange);
        tag.putInt("ZRange", zRange);
        tag.putInt("YRange", yRange);
        tag.putInt("Speed", speed);
        tag.putInt("RedstoneMode", redstoneMode);
        tag.putBoolean("Active", active);
        tag.putString("Owner", getOwner() == null ? "" : getOwner());
    }

    @Override
    public void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        if (tag.equals("CustomName")) {
            String jsonString = String.valueOf(tag.getString("CustomName"));
            Component name = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, com.google.gson.JsonParser.parseString(jsonString))
                                                         .getOrThrow( error -> {
                                                             throw new IllegalStateException("Failed to deserialize component: " + error);
                                                         });
            this.setCustomName(name);
        }
        xRange = tag.getInt("XRange").orElse(0);
        zRange = tag.getInt("ZRange").orElse(0);
        yRange = tag.getInt("YRange").orElse(0);
        speed = tag.getInt("Speed").orElse(1);
        redstoneMode = tag.getInt("RedstoneMode").orElse(0);
        active = tag.getBooleanOr("Active", false);
        uuid = String.valueOf(tag.getString("Owner"));

        area = BlockPos.betweenClosed(worldPosition.getX() - xRange, worldPosition.getY() - yRange, worldPosition.getZ() - zRange,
                worldPosition.getX() + xRange, worldPosition.getY() + yRange, worldPosition.getZ() + zRange);
    }

    public void openTorcherinoScreen(ServerPlayer player) {
        NetworkUtils.getInstance().s2c_openTorcherinoScreen(player, worldPosition, this.getName(), xRange, zRange, yRange, speed, redstoneMode);
    }

    public static class Data {
        public final Component customName;
        public final int xRange, yRange, zRange, speed, redstoneMode;
        public final boolean active;
        public final String uuid;

        private Data(Component customName, int xRange, int yRange, int zRange,
                     int speed, int redstoneMode, boolean active, String uuid) {
            this.customName = customName;
            this.xRange = xRange;
            this.yRange = yRange;
            this.zRange = zRange;
            this.speed = speed;
            this.redstoneMode = redstoneMode;
            this.active = active;
            this.uuid = uuid;
        }

        public static Data from(TorcherinoBlockEntity be) {
            return new Data(be.getCustomName(), be.xRange, be.yRange, be.zRange,
                    be.speed, be.redstoneMode, be.active, be.getOwner());
        }
    }

    public void restore(Data data) {
        if (data.customName != null) {
            this.setCustomName(data.customName);
        }
        this.readClientData(data.xRange, data.zRange, data.yRange, data.speed, data.redstoneMode);
        this.active = data.active;
        this.setOwner(data.uuid);
    }


    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
