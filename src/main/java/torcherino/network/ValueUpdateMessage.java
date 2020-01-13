package torcherino.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.blocks.tile.TorcherinoTileEntity;

import java.util.function.Supplier;

public class ValueUpdateMessage
{
    private final BlockPos pos;
    private final int xRange, zRange, yRange, speed, redstoneMode;

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
        buf.writeBlockPos(msg.pos).writeInt(msg.xRange).writeInt(msg.zRange).writeInt(msg.yRange).writeInt(msg.speed).writeInt(msg.redstoneMode);
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
                if (msg.withinBounds(TorcherinoAPI.INSTANCE.getTiers().get(torcherinoTileEntity.getTierName())))
                {
                    torcherinoTileEntity.readClientData(msg.xRange, msg.zRange, msg.yRange, msg.speed, msg.redstoneMode);
                }
            }
        });
        context.setPacketHandled(true);
    }

    private boolean withinBounds(Tier tier)
    {
        if (tier == null)
        {
            Torcherino.LOGGER.error("Torcherino tile entity does not have a valid tier.");
            return false;
        }
        if (xRange > tier.getXZRange() || zRange > tier.getXZRange() || yRange > tier.getYRange() || speed > tier.getMaxSpeed() || redstoneMode > 3 ||
                xRange < 0 || zRange < 0 || yRange < 0 || speed < 1 || redstoneMode < 0)
        {
            Torcherino.LOGGER.error("Data received from client is invalid.");
            return false;
        }
        return true;
    }
}