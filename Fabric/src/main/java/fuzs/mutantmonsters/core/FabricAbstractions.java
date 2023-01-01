package fuzs.mutantmonsters.core;

import fuzs.mutantmonsters.mixin.accessor.ExplosionFabricAccessor;
import fuzs.mutantmonsters.mixin.accessor.MobFabricAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FabricAbstractions implements CommonAbstractions {

    @Override
    public BlockPathTypes getAdjacentBlockPathType(BlockGetter blockReader, BlockPos.MutableBlockPos mutable, BlockPathTypes pathNodeType) {
        return null;
    }

    @Override
    public void onBlockCaughtFire(Block block, BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {

    }

    @Override
    public boolean getMobGriefingEvent(Level level, Entity entity) {
        return true;
    }

    @Override
    public boolean onAnimalTame(Animal animal, Player tamer) {
        return false;
    }

    @Override
    public BlockParticleOption setBlockParticlePos(BlockParticleOption particleOption, BlockPos pos) {
        return particleOption;
    }

    @Override
    public GoalSelector getGoalSelector(Mob mob) {
        return ((MobFabricAccessor) mob).mutantmonsters$getGoalSelector();
    }

    @Override
    public boolean onExplosionStart(Level level, Explosion explosion) {
        return false;
    }

    @Override
    public void onExplosionDetonate(Level level, Explosion explosion, List<Entity> list, double diameter) {

    }

    @Override
    public @Nullable Entity getExplosionExploder(Explosion explosion) {
        return ((ExplosionFabricAccessor) explosion).mutantmonsters$getExploder();
    }

    @Override
    public Vec3 getExplosionPosition(Explosion explosion) {
        ExplosionFabricAccessor accessor = (ExplosionFabricAccessor) explosion;
        return new Vec3(accessor.mutantmonsters$getX(), accessor.mutantmonsters$getY(), accessor.mutantmonsters$getZ());
    }

    @Override
    public boolean isArrowInfinite(ArrowItem arrow, ItemStack projectile, ItemStack bow, Player player) {
        int enchant = EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.INFINITY_ARROWS, bow);
        return enchant > 0 && arrow.getClass() == ArrowItem.class;
    }

    @Override
    public AbstractArrow getCustomArrowShotFromBow(BowItem bow, AbstractArrow arrow) {
        return arrow;
    }

    @Override
    public boolean shouldRiderSit(Entity vehicle) {
        return true;
    }

    @Override
    public boolean isBossMob(LivingEntity entity) {
        return !entity.canChangeDimensions();
    }
}
