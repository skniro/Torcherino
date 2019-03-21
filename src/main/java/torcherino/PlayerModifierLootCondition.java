package torcherino;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;
import java.util.Set;

public class PlayerModifierLootCondition implements LootCondition
{
	private static final PlayerModifierLootCondition INSTANCE = new PlayerModifierLootCondition();

	@Override public Set<LootContextParameter<?>> getRequiredParameters() { return ImmutableSet.of(LootContextParameters.THIS_ENTITY); }
	@Override public boolean test(LootContext context)
	{
		return Utils.keyStates.getOrDefault(context.get(LootContextParameters.THIS_ENTITY), false);
	}

	public static class Factory extends LootCondition.Factory<PlayerModifierLootCondition>
	{
		Factory() { super(Utils.getId("player_modifier"), PlayerModifierLootCondition.class); }

		@Override public void toJson(JsonObject jsonObject, PlayerModifierLootCondition lootCondition, JsonSerializationContext context) { }
		@Override public PlayerModifierLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext context)
		{
			return PlayerModifierLootCondition.INSTANCE;
		}
	}
}