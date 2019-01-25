package torcherino;
import net.minecraft.network.PacketBuffer;
import org.dimdev.rift.network.Message;
import org.dimdev.rift.network.ServerMessageContext;

public class TorcherinoMessage extends Message
{

    private boolean pressed;

    // Do not remove, used by rift when creating a new message instance
    public TorcherinoMessage(){}

    TorcherinoMessage(boolean b)
    {
        pressed = b;
    }

    @Override
    public void write(PacketBuffer buffer)
    {
        buffer.writeBoolean(pressed);
    }

    @Override
    public void read(PacketBuffer buffer)
    {
        pressed = buffer.readBoolean();
    }

    @Override
    public void process(ServerMessageContext context)
    {
        Utils.logger.info("New keystate update received from " + context.getSender().getDisplayName().getString() + ": " + pressed);
        Utils.keyStates.put(context.getSender(), pressed);
    }
}
