package torcherino;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.Parameter;
import net.minecraft.world.loot.context.Parameters;

public class PlayerModifierLootCondition implements LootCondition
{
    private static final PlayerModifierLootCondition INSTANCE = new PlayerModifierLootCondition();

    public Set<Parameter<?>> getRequiredParameters()
    {
        return ImmutableSet.of(Parameters.THIS_ENTITY);
    }

    public boolean test(LootContext lootContext_1)
    {
        Entity player = lootContext_1.get(Parameters.THIS_ENTITY);
        if(player == null) return false;
        return Utils.keyStates.getOrDefault(player, false);
    }

    public static class Factory extends net.minecraft.world.loot.condition.LootCondition.Factory<PlayerModifierLootCondition>
    {
        Factory()
        {
            super(new Identifier("torcherino", "player_sneaking"), PlayerModifierLootCondition.class);
        }

        public void toJson(JsonObject jsonObject, PlayerModifierLootCondition lootCondition, JsonSerializationContext context)
        {

        }
        public PlayerModifierLootCondition fromJson(JsonObject jsonObject_1, JsonDeserializationContext jsonDeserializationContext_1)
        {
            return PlayerModifierLootCondition.INSTANCE;
        }
    }
}