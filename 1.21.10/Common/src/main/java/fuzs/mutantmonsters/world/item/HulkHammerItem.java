package fuzs.mutantmonsters.world.item;

import fuzs.mutantmonsters.world.level.SeismicWave;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class HulkHammerItem extends Item {

    public HulkHammerItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 8.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.9F, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 2, false);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (context.getClickedFace() != Direction.UP) {
            return InteractionResult.PASS;
        } else {
            if (!level.isClientSide()) {
                List<SeismicWave> seismicWaves = new ArrayList<>();
                Vec3 vec = Vec3.directionFromRotation(0.0F, player.getYRot());
                int x = Mth.floor(player.getX() + vec.x * 1.5);
                int y = Mth.floor(player.getBoundingBox().minY);
                int z = Mth.floor(player.getZ() + vec.z * 1.5);
                int x1 = Mth.floor(player.getX() + vec.x * 8.0);
                int z1 = Mth.floor(player.getZ() + vec.z * 8.0);
                SeismicWave.createWaves(level, seismicWaves, x, z, x1, z1, y);
                SeismicWave.addAll(player, seismicWaves);
            }

            level.playSound(player,
                    context.getClickedPos(),
                    SoundEvents.GENERIC_EXPLODE.value(),
                    SoundSource.BLOCKS,
                    0.8F,
                    0.8F + player.getRandom().nextFloat() * 0.4F);
            player.getCooldowns().addCooldown(context.getItemInHand(), 25);
            player.awardStat(Stats.ITEM_USED.get(this));
            ItemHelper.hurtAndBreak(context.getItemInHand(), 1, player, context.getHand());
            return InteractionResultHelper.sidedSuccess(level.isClientSide());
        }
    }
}
