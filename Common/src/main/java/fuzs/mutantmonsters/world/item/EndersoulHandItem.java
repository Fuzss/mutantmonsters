package fuzs.mutantmonsters.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import fuzs.mutantmonsters.entity.mutant.MutantEndermanEntity;
import fuzs.mutantmonsters.entity.projectile.ThrowableBlockEntity;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EndersoulHandItem extends Item implements Vanishable {
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public EndersoulHandItem(Item.Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 5.0, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4000000953674316, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, (livingEntity) -> {
            livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState blockState = world.getBlockState(pos);
        ItemStack itemStack = context.getItemInHand();
        Player player = context.getPlayer();
        if (context.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else if (!blockState.is(ModRegistry.ENDERSOUL_HAND_HOLDABLE)) {
            return InteractionResult.PASS;
        } else if (!world.mayInteract(player, pos)) {
            return InteractionResult.PASS;
        } else if (!player.mayUseItemAt(pos, context.getClickedFace(), itemStack)) {
            return InteractionResult.PASS;
        } else {
            if (!world.isClientSide) {
                world.addFreshEntity(new ThrowableBlockEntity(player, blockState, pos));
                world.removeBlock(pos, false);
            }

            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!playerIn.isSecondaryUseActive()) {
            return InteractionResultHolder.pass(stack);
        } else {
            HitResult result = playerIn.pick(128.0, 1.0F, false);
            if (result.getType() != HitResult.Type.BLOCK) {
                playerIn.displayClientMessage(Component.translatable(this.getDescriptionId() + ".teleport_failed"), true);
                return InteractionResultHolder.fail(stack);
            } else {
                if (!worldIn.isClientSide) {
                    BlockPos startPos = ((BlockHitResult)result).getBlockPos();
                    BlockPos endPos = startPos.relative(((BlockHitResult)result).getDirection());
                    BlockPos posDown = startPos.below();
                    if (!worldIn.isEmptyBlock(posDown) || !worldIn.getBlockState(posDown).getMaterial().blocksMotion()) {
                        for(int tries = 0; tries < 3; ++tries) {
                            BlockPos checkPos = startPos.above(tries + 1);
                            if (worldIn.isEmptyBlock(checkPos)) {
                                endPos = checkPos;
                                break;
                            }
                        }
                    }

                    worldIn.playSound(null, playerIn.xo, playerIn.yo, playerIn.zo, SoundEvents.CHORUS_FRUIT_TELEPORT, playerIn.getSoundSource(), 1.0F, 1.0F);
                    playerIn.teleportTo((double)endPos.getX() + 0.5, (double)endPos.getY(), (double)endPos.getZ() + 0.5);
                    worldIn.playSound(null, endPos, SoundEvents.CHORUS_FRUIT_TELEPORT, playerIn.getSoundSource(), 1.0F, 1.0F);
                    MutantEndermanEntity.teleportAttack(playerIn);
                    EntityUtil.sendParticlePacket(playerIn, ModRegistry.ENDERSOUL_PARTICLE_TYPE.get(), 256);
                    playerIn.getCooldowns().addCooldown(this, 40);
                    stack.hurtAndBreak(4, playerIn, (e) -> {
                        e.broadcastBreakEvent(handIn);
                    });
                }

                playerIn.fallDistance = 0.0F;
                playerIn.swing(handIn);
                playerIn.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.sidedSuccess(stack, worldIn.isClientSide);
            }
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }
}