package fuzs.mutantmonsters.world.effect;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.config.ServerConfig;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.world.entity.SkullSpirit;
import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ChemicalXMobEffect extends InstantenousMobEffect {
    public static final Predicate<LivingEntity> IS_APPLICABLE = (target) -> {
        EntityType<?> entityType = target.getType();
        return !CommonAbstractions.INSTANCE.isBossMob(entityType) &&
                !MutantMonsters.CONFIG.get(ServerConfig.class).mutantXConversions.containsValue(entityType) &&
                entityType != ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value() &&
                entityType != ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value();
    };
    public static final TargetingConditions TARGET_PREDICATE = TargetingConditions.forNonCombat().selector(IS_APPLICABLE);

    public ChemicalXMobEffect(MobEffectCategory mobEffectCategory, int effectColor) {
        super(mobEffectCategory, effectColor);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {
        Level level = livingEntity.level();
        Player player = indirectSource instanceof Player ? (Player) indirectSource : null;
        if (!level.isClientSide && livingEntity instanceof Mob mob && TARGET_PREDICATE.test(player, livingEntity)) {
            SkullSpirit skullSpirit = new SkullSpirit(level, mob, player != null ? player.getUUID() : null);
            skullSpirit.moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            level.addFreshEntity(skullSpirit);
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
