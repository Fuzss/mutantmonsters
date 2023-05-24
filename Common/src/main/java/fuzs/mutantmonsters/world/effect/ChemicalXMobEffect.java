package fuzs.mutantmonsters.world.effect;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.config.CommonConfig;
import fuzs.mutantmonsters.core.CommonAbstractions;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.world.entity.SkullSpirit;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ChemicalXMobEffect extends InstantenousMobEffect {
    public static final Predicate<LivingEntity> IS_APPLICABLE = (target) -> {
        EntityType<?> entityType = target.getType();
        return !CommonAbstractions.INSTANCE.isBossMob(target) && !MutantMonsters.CONFIG.get(CommonConfig.class).mutantXConversions.containsValue(entityType) && entityType != ModRegistry.CREEPER_MINION_ENTITY_TYPE.get() && entityType != ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get();
    };
    public static final TargetingConditions PREDICATE = TargetingConditions.forNonCombat().selector(IS_APPLICABLE);

    public ChemicalXMobEffect(MobEffectCategory mobEffectCategory, int effectColor) {
        super(mobEffectCategory, effectColor);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        Level level = livingEntity.level;
        if (!level.isClientSide && livingEntity instanceof Mob target && PREDICATE.test(null, target)) {
            SkullSpirit spirit = new SkullSpirit(level, target);
            spirit.moveTo(target.getX(), target.getY(), target.getZ());
            level.addFreshEntity(spirit);
        }
    }

    @Nullable
    public static EntityType<?> getMutantOf(LivingEntity target) {
        EntityType<?> targetType = target.getType();
        if (!MutantMonsters.CONFIG.get(CommonConfig.class).mutantXConversions.containsKey(targetType)) {
            return null;
        } else if (targetType == EntityType.PIG && (!target.hasEffect(MobEffects.UNLUCK) || target.getEffect(MobEffects.UNLUCK).getAmplifier() != 13)) {
            return null;
        } else {
            return targetType == EntityType.ZOMBIE && target.isBaby() ? null : MutantMonsters.CONFIG.get(CommonConfig.class).mutantXConversions.get(targetType);
        }
    }
}
