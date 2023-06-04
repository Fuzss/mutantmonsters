package fuzs.mutantmonsters.core;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    BlockPathTypes getAdjacentBlockPathType(BlockGetter blockReader, BlockPos.MutableBlockPos mutable, BlockPathTypes pathNodeType);

    void onBlockCaughtFire(Block block, BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter);

    boolean getMobGriefingEvent(Level level, Entity entity);

    boolean onAnimalTame(Animal animal, Player tamer);

    BlockParticleOption setBlockParticlePos(BlockParticleOption particleOption, BlockPos pos);

    boolean onExplosionStart(Level level, Explosion explosion);

    void onExplosionDetonate(Level level, Explosion explosion, List<Entity> list, double diameter);

    @Nullable
    Entity getExplosionExploder(Explosion explosion);

    Vec3 getExplosionPosition(Explosion explosion);

    AbstractArrow getCustomArrowShotFromBow(BowItem bow, AbstractArrow arrow);

    boolean shouldRiderSit(Entity vehicle);
}
