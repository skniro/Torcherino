package torcherino.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.api.blocks.TorcherinoTileEntity;

import java.util.function.Supplier;

public class ValueUpdateMessage
{
    private final BlockPos pos;
    private final int xRange;
    private final int zRange;
    private final int yRange;
    private final int speed;
    private final int redstoneMode;

    public ValueUpdateMessage(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode)
    {
        this.pos = pos;
        this.xRange = xRange;
        this.zRange = zRange;
        this.yRange = yRange;
        this.speed = speed;
        this.redstoneMode = redstoneMode;
    }

    static void encode(ValueUpdateMessage msg, PacketBuffer buf)
    {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.xRange);
        buf.writeInt(msg.zRange);
        buf.writeInt(msg.yRange);
        buf.writeInt(msg.speed);
        buf.writeInt(msg.redstoneMode);
    }

    static ValueUpdateMessage decode(PacketBuffer buf)
    {
        return new ValueUpdateMessage(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    static void handle(ValueUpdateMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() ->
        {
            @SuppressWarnings("ConstantConditions") World world = context.getSender().world;
            TileEntity tileEntity = world.getTileEntity(msg.pos);
            if (tileEntity instanceof TorcherinoTileEntity)
            {
                TorcherinoTileEntity torcherinoTileEntity = (TorcherinoTileEntity) tileEntity;
                Tier tier = TorcherinoAPI.INSTANCE.getTier(torcherinoTileEntity.getTierName());
                if (msg.xRange > tier.getXZRange() || msg.zRange > tier.getXZRange() || msg.yRange > tier.getYRange() || msg.speed > tier.getMaxSpeed() ||
                        msg.redstoneMode > 3 || msg.xRange < 0 || msg.zRange < 0 || msg.yRange < 0 || msg.speed < 0 || msg.redstoneMode < 0)
                { return; }
                torcherinoTileEntity.readClientData(msg.xRange, msg.zRange, msg.yRange, msg.speed, msg.redstoneMode);
            }
        });
        context.setPacketHandled(true);
    }
}