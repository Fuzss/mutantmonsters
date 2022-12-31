package fuzs.mutantmonsters.world.effect;

import com.google.common.collect.ImmutableMap;
import fuzs.mutantmonsters.entity.SkullSpirit;
import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.Map;
import java.util.function.Predicate;

public class ChemicalXMobEffect extends InstantenousMobEffect {
    private static final Map<EntityType<? extends Mob>, EntityType<? extends Mob>> MUTATIONS = ImmutableMap.<EntityType<? extends Mob>, EntityType<? extends Mob>>builder()
            .put(EntityType.CREEPER, ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.get())
            .put(EntityType.ENDERMAN, ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.get())
            .put(EntityType.PIG, ModRegistry.SPIDER_PIG_ENTITY_TYPE.get())
            .put(EntityType.SKELETON, ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get())
            .put(EntityType.SNOW_GOLEM, ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.get())
            .put(EntityType.ZOMBIE, ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.get()).build();
    public static final Predicate<LivingEntity> IS_APPLICABLE = (target) -> {
        EntityType<?> entityType = target.getType();
        return target.canChangeDimensions() && !MUTATIONS.containsValue(entityType) && entityType != ModRegistry.CREEPER_MINION_ENTITY_TYPE.get() && entityType != ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get();
    };
    public static final TargetingConditions PREDICATE = TargetingConditions.forNonCombat().selector(IS_APPLICABLE);

    public ChemicalXMobEffect(int i) {
        super(MobEffectCategory.HARMFUL, i);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level.isClientSide && livingEntity instanceof Mob target && PREDICATE.test(null, target)) {
            SkullSpirit spirit = new SkullSpirit(target.level, target);
            spirit.setPos(target.getX(), target.getY(), target.getZ());
            target.level.addFreshEntity(spirit);
            target.level.broadcastEntityEvent(target, (byte)3);
            target.discard();
        }
    }
}
