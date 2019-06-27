package torcherino.api.impl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import java.util.HashMap;
import java.util.Map;

public class TorcherinoImpl implements TorcherinoAPI
{
	private Map<ResourceLocation, Tier> tiers = new HashMap<>();

	public void registerTier(ResourceLocation name, int maxSpeed, int xzRange, int yRange)
	{
		Tier tier = new Tier(maxSpeed, xzRange, yRange);
		tiers.put(name, tier);
	}

	public ImmutableMap<ResourceLocation, Tier> getTiers()
	{
		return ImmutableMap.copyOf(tiers);
	}
}
