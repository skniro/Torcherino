package torcherino.api.blocks;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.Tickable;
import net.minecraft.util.registry.Registry;

public class TorcherinoBlockEntity extends BlockEntity implements Nameable, Tickable
{
    private Text customName;

    public TorcherinoBlockEntity() { super(Registry.BLOCK_ENTITY.get(new Identifier("torcherino", "torcherino"))); }

    @Override
    public Text getName()
    {
        return hasCustomName() ? customName : new TranslatableText(world.getBlockState(pos).getBlock().getTranslationKey());
    }

    @Override
    public boolean hasCustomName()
    {
        return customName != null;
    }

    @Override
    public Text getCustomName()
    {
        return customName;
    }

    public void setCustomName(Text name)
    {
        this.customName = name;
    }

    @Override
    public void tick()
    {

    }

    public void setPoweredByRedstone(boolean receivingRedstonePower)
    {

    }
}
