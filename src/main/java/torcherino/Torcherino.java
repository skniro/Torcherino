package torcherino;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.registry.IRegistry;
import org.dimdev.rift.listener.BlockAdder;
import org.dimdev.rift.listener.ItemAdder;
import org.dimdev.rift.listener.MessageAdder;
import org.dimdev.rift.listener.TileEntityTypeAdder;
import org.dimdev.rift.network.Message;
import torcherino.block.ModBlocks;
import torcherino.block.tile.TorcherinoTileEntity;
import java.util.ArrayList;

public class Torcherino implements BlockAdder, ItemAdder, TileEntityTypeAdder, MessageAdder
{
    public static TileEntityType<TorcherinoTileEntity> TORCHERINO_TILEENTITY;
    static ArrayList<ItemBlock> itemBlocks = new ArrayList<>();

    public void registerBlocks()
    {
        ModBlocks.register();
    }

    public void registerItems()
    {
        itemBlocks.forEach(Item::register);
    }

    public void registerTileEntityTypes()
    {
        TORCHERINO_TILEENTITY = TileEntityType.register("torcherino", TileEntityType.Builder.create(TorcherinoTileEntity::new));
        Utils.blacklistTileEntity(TORCHERINO_TILEENTITY);
    }

    public void registerMessages(IRegistry<Class<? extends Message>> registry)
    {
        registry.put(Utils.getId("modifier"), TorcherinoMessage.class);
    }
}
