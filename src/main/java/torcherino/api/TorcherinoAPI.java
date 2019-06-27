package torcherino.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import torcherino.api.impl.TorcherinoImpl;

public interface TorcherinoAPI
{
	TorcherinoAPI INSTANCE = new TorcherinoImpl();

	ImmutableMap<ResourceLocation, Tier> getTiers();

	boolean registerTier(ResourceLocation name, int maxSpeed, int xzRange, int yRange);

	boolean blacklistBlock(ResourceLocation block);

	boolean blacklistTileEntity(ResourceLocation tileEntity);

}
