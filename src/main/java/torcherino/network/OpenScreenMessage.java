package torcherino.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import torcherino.client.gui.TorcherinoScreen;

import java.util.function.Supplier;

public class OpenScreenMessage
{
    public final BlockPos pos;
    public final ITextComponent title;
    public final int xRange, zRange, yRange, speed, redstoneMode;

    public OpenScreenMessage(BlockPos pos, ITextComponent title, int xRange, int zRange, int yRange, int speed, int redstoneMode)
    {
        this.pos = pos;
        this.title = title;
        this.xRange = xRange;
        this.zRange = zRange;
        this.yRange = yRange;
        this.speed = speed;
        this.redstoneMode = redstoneMode;
    }

    static void encode(OpenScreenMessage msg, PacketBuffer buf)
    {
        buf.writeBlockPos(msg.pos).writeTextComponent(msg.title).writeInt(msg.xRange)
           .writeInt(msg.zRange).writeInt(msg.yRange).writeInt(msg.speed).writeInt(msg.redstoneMode);
    }

    static OpenScreenMessage decode(PacketBuffer buf)
    {
        return new OpenScreenMessage(buf.readBlockPos(), buf.readTextComponent(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    static void handle(OpenScreenMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getOriginationSide() == LogicalSide.SERVER) TorcherinoScreen.open(msg);
        context.setPacketHandled(true);
    }
}