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

public class PlayerSneakingLootCondition implements LootCondition
{
    private static final PlayerSneakingLootCondition INSTANCE = new PlayerSneakingLootCondition();

    public Set<Parameter<?>> getRequiredParameters()
    {
        return ImmutableSet.of(Parameters.THIS_ENTITY);
    }

    public boolean test(LootContext lootContext_1)
    {
        Entity player = lootContext_1.get(Parameters.THIS_ENTITY);
        if(player == null) return false;
        return player.isSneaking();
    }

    public static class Factory extends net.minecraft.world.loot.condition.LootCondition.Factory<PlayerSneakingLootCondition>
    {
        Factory()
        {
            super(new Identifier("torcherino", "player_sneaking"), PlayerSneakingLootCondition.class);
        }

        public void toJson(JsonObject jsonObject, PlayerSneakingLootCondition lootCondition, JsonSerializationContext context)
        {

        }
        public PlayerSneakingLootCondition fromJson(JsonObject jsonObject_1, JsonDeserializationContext jsonDeserializationContext_1)
        {
            return PlayerSneakingLootCondition.INSTANCE;
        }
    }
}