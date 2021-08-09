package torcherino.block.entity;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import torcherino.api.Tier;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoAPI;
import torcherino.config.Config;
import torcherino.platform.NetworkUtils;

public abstract class TorcherinoBlockEntity extends BlockEntity implements Nameable, TierSupplier {
    protected int randomTicks;
    protected Component customName;
    protected int xRange, yRange, zRange, speed;
    protected Iterable<BlockPos> area;
    protected boolean active;
    protected ResourceLocation tierID;
    protected String uuid = "";

    public TorcherinoBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
        return this.hasCustomName() ? customName : new TranslatableComponent(this.getBlockState().getBlock().getDescriptionId());
    }

    protected boolean readClientData(int xRange, int zRange, int yRange, int speed) {
        Tier tier = TorcherinoAPI.INSTANCE.getTiers().get(getTier());
        if (this.valueInRange(xRange, 0, tier.xzRange()) &&
                this.valueInRange(zRange, 0, tier.xzRange()) &&
                this.valueInRange(yRange, 0, tier.yRange()) &&
                this.valueInRange(speed, 0, tier.maxSpeed()) ) {
            this.xRange = xRange;
            this.zRange = zRange;
            this.yRange = yRange;
            this.speed = speed;
            area = BlockPos.betweenClosed(worldPosition.getX() - xRange, worldPosition.getY() - yRange, worldPosition.getZ() - zRange,
                    worldPosition.getX() + xRange, worldPosition.getY() + yRange, worldPosition.getZ() + zRange);
            return true;
        }
        return false;
    }

    protected boolean valueInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (!level.isClientSide()) {
            level.getServer().tell(new TickTask(level.getServer().getTickCount(), () -> this.getBlockState().neighborChanged(level, worldPosition, null, null, false)));
        }
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

    public static void tick(Level level, BlockPos pos, BlockState state, TorcherinoBlockEntity entity) {
        if (!entity.active || entity.speed == 0 || (entity.xRange == 0 && entity.yRange == 0 && entity.zRange == 0)) {
            return;
        }
        if (!Config.INSTANCE.online_mode.equals("") && !NetworkUtils.getInstance().s_isPlayerOnline(entity.getOwner())) {
            return;
        }
        // todo: get on load and then when updated
        entity.randomTicks = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        entity.area.forEach(entity::tickBlock);
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

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        if (this.hasCustomName()) {
            tag.putString("CustomName", Component.Serializer.toJson(this.getCustomName()));
        }
        tag.putInt("XRange", xRange);
        tag.putInt("ZRange", zRange);
        tag.putInt("YRange", yRange);
        tag.putInt("Speed", speed);
        tag.putBoolean("Active", active);
        tag.putString("Owner", this.getOwner() == null ? "" : this.getOwner());
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("CustomName", 8)) {
            this.setCustomName(Component.Serializer.fromJson(tag.getString("CustomName")));
        }
        xRange = tag.getInt("XRange");
        zRange = tag.getInt("ZRange");
        yRange = tag.getInt("YRange");
        speed = tag.getInt("Speed");
        active = tag.getBoolean("Active");
        uuid = tag.getString("Owner");
        area = BlockPos.betweenClosed(worldPosition.getX() - xRange, worldPosition.getY() - yRange, worldPosition.getZ() - zRange,
                worldPosition.getX() + xRange, worldPosition.getY() + yRange, worldPosition.getZ() + zRange);
    }

    public void openTorcherinoScreen(ServerPlayer player) {
        NetworkUtils.getInstance().s2c_openTorcherinoScreen(player, worldPosition, this.getName(), xRange, zRange, yRange, speed);
    }
}
