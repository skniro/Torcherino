package torcherino.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import torcherino.api.impl.TorcherinoImpl;

public interface TorcherinoAPI
{
	public TorcherinoAPI INSTANCE = new TorcherinoImpl();

	ImmutableMap<ResourceLocation, Tier> getTiers();

	void registerTier(ResourceLocation name, int maxSpeed, int xzRange, int yRange);

}
