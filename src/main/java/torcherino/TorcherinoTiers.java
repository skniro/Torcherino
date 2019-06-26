package torcherino;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import torcherino.config.TorcherinoConfig;
import java.util.HashMap;
import java.util.Map;

public class TorcherinoTiers
{
	public static final TorcherinoTiers INSTANCE = new TorcherinoTiers();
	private Map<ResourceLocation, Tier> tiers;

	public void initialise()
	{
		tiers = new HashMap<>();

		for(TorcherinoConfig.Tier tier : TorcherinoConfig.INSTANCE.tiers)
		{
			registerTier(Utilities.resloc(tier.name), tier.max_speed, tier.xz_range, tier.y_range);
		}
	}

	private void registerTier(ResourceLocation name, int maxSpeed, int xzRange, int yRange)
	{
		Tier tier = new Tier(maxSpeed, xzRange, yRange);
		tiers.put(name, tier);
	}

	public ImmutableMap<ResourceLocation, Tier> getTiers()
	{
		return ImmutableMap.copyOf(tiers);
	}

	public class Tier
	{
		public final int MAX_SPEED;
		public final int XZ_RANGE;
		public final int Y_RANGE;

		private Tier(int maxSpeed, int xzRange, int yRange)
		{
			this.MAX_SPEED = maxSpeed;
			this.XZ_RANGE = xzRange;
			this.Y_RANGE = yRange;
		}
	}
}
