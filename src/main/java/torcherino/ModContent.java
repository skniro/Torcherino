package torcherino;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import torcherino.api.Tier;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoAPI;
import torcherino.blocks.JackoLanterinoBlock;
import torcherino.blocks.LanterinoBlock;
import torcherino.blocks.TorcherinoBlock;
import torcherino.blocks.TorcherinoWallBlock;
import torcherino.blocks.tile.CustomTileEntityType;
import torcherino.blocks.tile.TorcherinoTileEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ModContent
{
    public static final ModContent INSTANCE = new ModContent();
    private HashSet<Block> blocks;
    private HashSet<Item> items;
    private HashMap<ResourceLocation, BasicParticleType> particles;
    public static final TileEntityType<TorcherinoTileEntity> TORCHERINO_TILE_ENTITY;

    static
    {
        TORCHERINO_TILE_ENTITY = new CustomTileEntityType<>(TorcherinoTileEntity::new, (b) -> b instanceof TierSupplier, null);
        TORCHERINO_TILE_ENTITY.setRegistryName(Torcherino.resloc("torcherino"));
    }

    public void initialise()
    {
        blocks = new HashSet<>();
        items = new HashSet<>();
        particles = new HashMap<>();
        Map<ResourceLocation, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
        tiers.keySet().forEach(this::register);
    }

    private ResourceLocation resloc(ResourceLocation tierID, String type)
    {
        if (tierID.getPath().equals("normal")) return new ResourceLocation(Torcherino.MOD_ID, type);
        return new ResourceLocation(Torcherino.MOD_ID, tierID.getPath() + "_" + type);
    }

    private void register(ResourceLocation tierID)
    {
        if (tierID.getNamespace().equals(Torcherino.MOD_ID))
        {
            ResourceLocation torcherinoID = resloc(tierID, "torcherino");
            ResourceLocation jackoLanterinoID = resloc(tierID, "lanterino");
            ResourceLocation lanterinoID = resloc(tierID, "lantern");
            ResourceLocation flameID = resloc(tierID, "flame");
            Block standingBlock = new TorcherinoBlock(tierID, flameID).setRegistryName(torcherinoID);
            Block wallBlock = new TorcherinoWallBlock((TorcherinoBlock) standingBlock, flameID)
                    .setRegistryName(Torcherino.resloc("wall_" + torcherinoID.getPath()));
            Item torcherinoItem = new WallOrFloorItem(standingBlock, wallBlock,
                    new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(torcherinoID);
            Block jackoLanterinoBlock = new JackoLanterinoBlock(tierID).setRegistryName(jackoLanterinoID);
            Item jackoLanterinoItem = new BlockItem(jackoLanterinoBlock, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                    .setRegistryName(jackoLanterinoID);
            Block lanterinoBlock = new LanterinoBlock(tierID).setRegistryName(lanterinoID);
            Item lanterinoItem = new BlockItem(lanterinoBlock, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(lanterinoID);
            blocks.add(standingBlock);
            blocks.add(wallBlock);
            blocks.add(jackoLanterinoBlock);
            blocks.add(lanterinoBlock);
            TorcherinoAPI.INSTANCE.registerTorcherinoBlock(standingBlock);
            TorcherinoAPI.INSTANCE.registerTorcherinoBlock(wallBlock);
            TorcherinoAPI.INSTANCE.registerTorcherinoBlock(jackoLanterinoBlock);
            TorcherinoAPI.INSTANCE.registerTorcherinoBlock(lanterinoBlock);
            items.add(torcherinoItem);
            items.add(jackoLanterinoItem);
            items.add(lanterinoItem);

            BasicParticleType particleType = new BasicParticleType(false);
            particleType.setRegistryName(flameID);
            particles.put(flameID, particleType);
        }
    }

    @SubscribeEvent
    public void registerBlocks(final RegistryEvent.Register<Block> registryEvent)
    {
        IForgeRegistry<Block> registry = registryEvent.getRegistry();
        blocks.forEach(registry::register);
    }

    @SubscribeEvent
    public void registerItems(final RegistryEvent.Register<Item> registryEvent)
    {
        IForgeRegistry<Item> registry = registryEvent.getRegistry();
        items.forEach(registry::register);
    }

    @SubscribeEvent
    public void registerTileEntityTypes(final RegistryEvent.Register<TileEntityType<?>> registryEvent)
    {
        TorcherinoAPI.INSTANCE.blacklistTileEntity(TORCHERINO_TILE_ENTITY);
        registryEvent.getRegistry().register(TORCHERINO_TILE_ENTITY);
    }

    @SubscribeEvent
    public void registerParticleTypes(final RegistryEvent.Register<ParticleType<?>> registryEvent)
    {
        IForgeRegistry<ParticleType<?>> registry = registryEvent.getRegistry();
        particles.forEach((resloc, pt) -> registry.register(pt));
    }

    @SubscribeEvent
    public void registerParticleFactories(ParticleFactoryRegisterEvent event)
    {
        particles.forEach((resloc, pt) -> Minecraft.getInstance().particles.registerFactory(pt, FlameParticle.Factory::new));
    }

    public BasicParticleType getParticleType(ResourceLocation resloc)
    {
        return particles.getOrDefault(resloc, null);
    }
}
