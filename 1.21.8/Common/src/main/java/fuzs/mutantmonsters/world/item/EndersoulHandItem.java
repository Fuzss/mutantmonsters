package fuzs.mutantmonsters.world.item;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.config.ServerConfig;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.init.ModTags;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import fuzs.mutantmonsters.world.entity.projectile.ThrowableBlock;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class EndersoulHandItem extends Item {

    public EndersoulHandItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4F, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 2, false);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState blockState = level.getBlockState(pos);
        ItemStack itemStack = context.getItemInHand();
        Player player = context.getPlayer();
        if (context.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else if (!MutantEnderman.canBlockBeHeld(level,
                pos,
                blockState,
                ModTags.ENDERSOUL_HAND_HOLDABLE_IMMUNE_BLOCK_TAG)) {
            return InteractionResult.PASS;
        } else if (!level.mayInteract(player, pos)) {
            return InteractionResult.PASS;
        } else if (!player.mayUseItemAt(pos, context.getClickedFace(), itemStack)) {
            return InteractionResult.PASS;
        } else {
            if (!level.isClientSide) {
                level.addFreshEntity(new ThrowableBlock(player, blockState, pos));
                level.removeBlock(pos, false);
            }

            return InteractionResultHelper.sidedSuccess(level.isClientSide);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        if (!player.isSecondaryUseActive()) {
            return InteractionResultHelper.pass(itemInHand);
        } else {
            HitResult result = player.pick(MutantMonsters.CONFIG.get(ServerConfig.class).endersoulHandTeleportDistance,
                    1.0F,
                    false);
            if (result.getType() != HitResult.Type.BLOCK) {
                player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".teleport_failed"), true);
                return InteractionResultHelper.fail(itemInHand);
            } else {
                if (level instanceof ServerLevel serverLevel) {
                    BlockPos startPos = ((BlockHitResult) result).getBlockPos();
                    BlockPos endPos = startPos.relative(((BlockHitResult) result).getDirection());
                    BlockPos posDown = startPos.below();
                    if (!level.isEmptyBlock(posDown) || !level.getBlockState(posDown).blocksMotion()) {
                        for (int i = 0; i < 3; ++i) {
                            BlockPos checkPos = startPos.above(i + 1);
                            if (level.isEmptyBlock(checkPos)) {
                                endPos = checkPos;
                                break;
                            }
                        }
                    }

                    level.playSound(null,
                            player.xo,
                            player.yo,
                            player.zo,
                            SoundEvents.CHORUS_FRUIT_TELEPORT,
                            player.getSoundSource(),
                            1.0F,
                            1.0F);
                    player.teleportTo((double) endPos.getX() + 0.5, endPos.getY(), (double) endPos.getZ() + 0.5);
                    level.playSound(null,
                            endPos,
                            SoundEvents.CHORUS_FRUIT_TELEPORT,
                            player.getSoundSource(),
                            1.0F,
                            1.0F);
                    MutantEnderman.teleportAttack(serverLevel, player);
                    EntityUtil.sendParticlePacket(player, ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(), 256);
                    player.getCooldowns().addCooldown(itemInHand, 200);
                    ItemHelper.hurtAndBreak(itemInHand, 4, player, interactionHand);
                }

                player.fallDistance = 0.0F;
                player.swing(interactionHand);
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHelper.sidedSuccess(itemInHand, level.isClientSide);
            }
        }
    }
}