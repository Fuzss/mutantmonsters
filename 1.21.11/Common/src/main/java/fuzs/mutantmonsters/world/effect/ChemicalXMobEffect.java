package fuzs.mutantmonsters.world.effect;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.config.ServerConfig;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModTags;
import fuzs.mutantmonsters.world.entity.SkullSpirit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public class ChemicalXMobEffect extends InstantenousMobEffect {
    public static final TargetingConditions.Selector IS_APPLICABLE = (LivingEntity livingEntity, ServerLevel serverLevel) -> {
        EntityType<?> entityType = livingEntity.getType();
        return !entityType.is(ModTags.BOSSES_ENTITY_TYPE_TAG) &&
                !MutantMonsters.CONFIG.get(ServerConfig.class).mutantXConversions.containsValue(entityType) &&
                entityType != ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value() &&
                entityType != ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value();
    };
    public static final TargetingConditions TARGET_PREDICATE = TargetingConditions.forNonCombat()
            .selector(IS_APPLICABLE);

    public ChemicalXMobEffect(MobEffectCategory mobEffectCategory, int effectColor) {
        super(mobEffectCategory, effectColor);
    }

    @Override
    public void applyInstantenousEffect(ServerLevel serverLevel, @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {
        Player player = indirectSource instanceof Player ? (Player) indirectSource : null;
        if (livingEntity instanceof Mob mob && TARGET_PREDICATE.test(serverLevel, player, livingEntity)) {
            SkullSpirit skullSpirit = new SkullSpirit(serverLevel, mob, player != null ? player.getUUID() : null);
            skullSpirit.snapTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            serverLevel.addFreshEntity(skullSpirit);
        }
    }

    @Nullable
    public static EntityType<?> getMutantOf(Mob target) {
        EntityType<?> targetType = target.getType();
        if (target.isBaby() || targetType == EntityType.PIG && !target.hasEffect(MobEffects.UNLUCK)) {
            return null;
        } else {
            return MutantMonsters.CONFIG.get(ServerConfig.class).mutantXConversions.get(targetType);
        }
    }
}
