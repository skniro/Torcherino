package torcherino;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntityType;
import org.dimdev.rift.listener.BlockAdder;
import org.dimdev.rift.listener.ItemAdder;
import org.dimdev.rift.listener.TileEntityTypeAdder;
import torcherino.block.ModBlocks;
import torcherino.block.tile.TorcherinoTileEntity;
import java.util.ArrayList;

public class Torcherino implements BlockAdder, ItemAdder, TileEntityTypeAdder
{
    public static TileEntityType<TorcherinoTileEntity> TORCHERINO;
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
        TORCHERINO = TileEntityType.register("torcherino", TileEntityType.Builder.create(TorcherinoTileEntity::new));
        Utils.blacklistTileEntity(TORCHERINO);
    }
}
