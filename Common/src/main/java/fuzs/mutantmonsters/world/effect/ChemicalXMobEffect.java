package fuzs.mutantmonsters.world.effect;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import fuzs.mutantmonsters.core.CommonAbstractions;
import fuzs.mutantmonsters.entity.SkullSpiritEntity;
import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ChemicalXMobEffect extends InstantenousMobEffect {
    private static final Supplier<Map<EntityType<? extends Mob>, EntityType<? extends Mob>>> MUTATIONS = Suppliers.memoize(() -> ImmutableMap.<EntityType<? extends Mob>, EntityType<? extends Mob>>builder()
            .put(EntityType.CREEPER, ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.get())
            .put(EntityType.ENDERMAN, ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.get())
            .put(EntityType.PIG, ModRegistry.SPIDER_PIG_ENTITY_TYPE.get())
            .put(EntityType.SKELETON, ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get())
            .put(EntityType.SNOW_GOLEM, ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.get())
            .put(EntityType.ZOMBIE, ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.get()).build());
    public static final Predicate<LivingEntity> IS_APPLICABLE = (target) -> {
        EntityType<?> entityType = target.getType();
        return !CommonAbstractions.INSTANCE.isBossMob(target) && !MUTATIONS.get().containsValue(entityType) && entityType != ModRegistry.CREEPER_MINION_ENTITY_TYPE.get() && entityType != ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get();
    };
    public static final TargetingConditions PREDICATE = TargetingConditions.forNonCombat().selector(IS_APPLICABLE);

    public ChemicalXMobEffect(MobEffectCategory mobEffectCategory, int effectColor) {
        super(mobEffectCategory, effectColor);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        Level level = livingEntity.level;
        if (!level.isClientSide && livingEntity instanceof Mob target && PREDICATE.test(null, target)) {
            SkullSpiritEntity spirit = new SkullSpiritEntity(level, target);
            spirit.moveTo(target.getX(), target.getY(), target.getZ());
            level.addFreshEntity(spirit);
        }
    }

    @Nullable
    public static EntityType<? extends Mob> getMutantOf(LivingEntity target) {
        EntityType<?> targetType = target.getType();
        if (!MUTATIONS.get().containsKey(targetType)) {
            return null;
        } else if (targetType == EntityType.PIG && (!target.hasEffect(MobEffects.UNLUCK) || target.getEffect(MobEffects.UNLUCK).getAmplifier() != 13)) {
            return null;
        } else {
            return targetType == EntityType.ZOMBIE && target.isBaby() ? null : MUTATIONS.get().get(targetType);
        }
    }
}
