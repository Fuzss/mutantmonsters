package fuzs.mutantmonsters.world.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;

public class ArmorBlockItem extends StandingAndWallBlockItem {

    public ArmorBlockItem(Block floorBlock, Block wallBlock, ArmorMaterial armorMaterial, Item.Properties properties) {
        super(floorBlock, wallBlock, Direction.DOWN, properties.humanoidArmor(armorMaterial, ArmorType.HELMET));
    }

    public Component getDescriptionComponent() {
        return Component.translatable(this.getDescriptionId() + ".description").withStyle(ChatFormatting.GOLD);
    }
}
