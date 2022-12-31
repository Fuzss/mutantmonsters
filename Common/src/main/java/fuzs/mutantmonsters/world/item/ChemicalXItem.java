package fuzs.mutantmonsters.world.item;

import fuzs.mutantmonsters.entity.projectile.ChemicalXEntity;
import net.minecraft.Util;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class ChemicalXItem extends Item {
    public ChemicalXItem(Item.Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                return Util.make(new ChemicalXEntity(position.x(), position.y(), position.z(), worldIn), (p_218408_1_) -> {
                    p_218408_1_.setItem(stackIn);
                });
            }
        });
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (worldIn.getRandom().nextFloat() * 0.4F + 0.8F));
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide) {
            ChemicalXEntity chemicalXEntity = new ChemicalXEntity(playerIn, worldIn);
            chemicalXEntity.setItem(itemstack);
            chemicalXEntity.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), -20.0F, 0.5F, 1.0F);
            worldIn.addFreshEntity(chemicalXEntity);
        }

        playerIn.awardStat(Stats.ITEM_USED.get(this));
        if (!playerIn.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
    }
}