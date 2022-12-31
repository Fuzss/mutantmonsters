package fuzs.mutantmonsters.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import fuzs.mutantmonsters.util.SeismicWave;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class HulkHammerItem extends Item implements Vanishable {
    public static final Map<UUID, List<SeismicWave>> WAVES = new HashMap<>();

    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public HulkHammerItem(Item.Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 8.0, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3.0, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, (e) -> {
            e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (state.getDestroySpeed(worldIn, pos) != 0.0F) {
            stack.hurtAndBreak(2, entityLiving, (e) -> {
                e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Player playerEntity = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        if (context.getClickedFace() != Direction.UP) {
            return InteractionResult.PASS;
        } else {
            if (!world.isClientSide) {
                List<SeismicWave> list = new ArrayList<>();
                Vec3 vec = Vec3.directionFromRotation(0.0F, playerEntity.getYRot());
                int x = Mth.floor(playerEntity.getX() + vec.x * 1.5);
                int y = Mth.floor(playerEntity.getBoundingBox().minY);
                int z = Mth.floor(playerEntity.getZ() + vec.z * 1.5);
                int x1 = Mth.floor(playerEntity.getX() + vec.x * 8.0);
                int z1 = Mth.floor(playerEntity.getZ() + vec.z * 8.0);
                SeismicWave.createWaves(world, list, x, z, x1, z1, y);
                addWave(playerEntity.getUUID(), list);
            }

            world.playSound(playerEntity, context.getClickedPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.8F, 0.8F + playerEntity.getRandom().nextFloat() * 0.4F);
            playerEntity.getCooldowns().addCooldown(this, 25);
            playerEntity.awardStat(Stats.ITEM_USED.get(this));
            itemStack.hurtAndBreak(1, playerEntity, (e) -> {
                e.broadcastBreakEvent(context.getHand());
            });
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }

    public static void addWave(UUID uuid, List<SeismicWave> list) {
        List<SeismicWave> waves = Iterables.getLast(WAVES.values(), null);
        if (waves == null) {
            WAVES.put(uuid, list);
        } else {
            waves.addAll(list);
            WAVES.put(uuid, waves);
        }
    }
}
