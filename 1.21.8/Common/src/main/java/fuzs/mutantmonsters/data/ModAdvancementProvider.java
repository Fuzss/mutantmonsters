package fuzs.mutantmonsters.data;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModTags;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.data.v2.AbstractAdvancementProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AbstractAdvancementProvider {
    public static final AdvancementToken ROOT_ADVANCEMENT = new AdvancementToken(MutantMonsters.id("root"));
    public static final AdvancementToken BURN_ZOMBIE_BURN_ADVANCEMENT = new AdvancementToken(MutantMonsters.id(
            "burn_zombie_burn"));
    public static final AdvancementToken FROSTY_THE_SNOW_GOLEM_ADVANCEMENT = new AdvancementToken(MutantMonsters.id(
            "frosty_the_snow_golem"));
    public static final AdvancementToken GUNPOWDER_SPICE_ADVANCEMENT = new AdvancementToken(MutantMonsters.id(
            "gunpowder_spice"));
    public static final AdvancementToken HULK_SMASH_ADVANCEMENT = new AdvancementToken(MutantMonsters.id("hulk_smash"));
    public static final AdvancementToken NO_BONES_ABOUT_IT_ADVANCEMENT = new AdvancementToken(MutantMonsters.id(
            "no_bones_about_it"));
    public static final AdvancementToken SPIDER_PIG_SPIDER_PIG_ADVANCEMENT = new AdvancementToken(MutantMonsters.id(
            "spider_pig_spider_pig"));
    public static final AdvancementToken YOU_DA_BOMBY_ADVANCEMENT = new AdvancementToken(MutantMonsters.id(
            "you_da_bomby"));

    public ModAdvancementProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addAdvancements(HolderLookup.Provider registries, Consumer<AdvancementHolder> writer) {
        HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);
        HolderLookup.RegistryLookup<EntityType<?>> entityTypeLookup = registries.lookupOrThrow(Registries.ENTITY_TYPE);
        Advancement.Builder.advancement()
                .display(display(ModItems.ENDERSOUL_HAND_ITEM.value().getDefaultInstance(),
                        ROOT_ADVANCEMENT.id(),
                        ResourceLocationHelper.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
                        AdvancementType.TASK,
                        false))
                .addCriterion("killed_something",
                        KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity()
                                .of(entityTypeLookup, ModTags.MUTANTS_ENTITY_TYPE_TAG)))
                .addCriterion("killed_by_something",
                        KilledTrigger.TriggerInstance.entityKilledPlayer(EntityPredicate.Builder.entity()
                                .of(entityTypeLookup, ModTags.MUTANTS_ENTITY_TYPE_TAG)))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(writer, ROOT_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(Items.FLINT_AND_STEEL.getDefaultInstance(), BURN_ZOMBIE_BURN_ADVANCEMENT.id()))
                .parent(ROOT_ADVANCEMENT.asParent())
                .addCriterion("used_flint_and_steel",
                        PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item()
                                        .of(itemLookup, Items.FLINT_AND_STEEL, Items.FIRE_CHARGE),
                                Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity()
                                        .of(entityTypeLookup, ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE.value())))))
                .save(writer, BURN_ZOMBIE_BURN_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(Items.GUNPOWDER.getDefaultInstance(), GUNPOWDER_SPICE_ADVANCEMENT.id()))
                .parent(ROOT_ADVANCEMENT.asParent())
                .addCriterion("obtained_chemical_x",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
                                .of(itemLookup, ModItems.CHEMICAL_X_ITEM.value())))
                .save(writer, GUNPOWDER_SPICE_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(Items.JACK_O_LANTERN.getDefaultInstance(), FROSTY_THE_SNOW_GOLEM_ADVANCEMENT.id()))
                .parent(GUNPOWDER_SPICE_ADVANCEMENT.asParent())
                .addCriterion("created_mutant_snow_golem",
                        SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity()
                                .of(entityTypeLookup, ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value())))
                .save(writer, FROSTY_THE_SNOW_GOLEM_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(ModItems.HULK_HAMMER_ITEM.value().getDefaultInstance(),
                        HULK_SMASH_ADVANCEMENT.id(),
                        AdvancementType.GOAL))
                .parent(BURN_ZOMBIE_BURN_ADVANCEMENT.asParent())
                .addCriterion("killed_mutant_zombie",
                        KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity()
                                        .of(entityTypeLookup, ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE.value()),
                                DamageSourcePredicate.Builder.damageType()
                                        .direct(EntityPredicate.Builder.entity()
                                                .equipment(EntityEquipmentPredicate.Builder.equipment()
                                                        .mainhand(ItemPredicate.Builder.item()
                                                                .of(itemLookup, ModItems.HULK_HAMMER_ITEM.value()))))))
                .save(writer, HULK_SMASH_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(ModItems.MUTANT_SKELETON_SKULL_ITEM.value().getDefaultInstance(),
                        NO_BONES_ABOUT_IT_ADVANCEMENT.id(),
                        AdvancementType.GOAL))
                .parent(ROOT_ADVANCEMENT.asParent())
                .addCriterion("killed_mutant_skeleton",
                        KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity()
                                        .of(entityTypeLookup, ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value()),
                                DamageSourcePredicate.Builder.damageType()
                                        .tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
                                        .direct(EntityPredicate.Builder.entity()
                                                .of(entityTypeLookup, EntityTypeTags.ARROWS)
                                                .nbt(new NbtPredicate(Util.make(new CompoundTag(),
                                                        tag -> tag.putBoolean("ShotFromCrossbow", true)))))
                                        .source(EntityPredicate.Builder.entity()
                                                .equipment(EntityEquipmentPredicate.Builder.equipment()
                                                        .head(ItemPredicate.Builder.item()
                                                                .of(itemLookup,
                                                                        ModItems.MUTANT_SKELETON_SKULL_ITEM.value()))
                                                        .chest(ItemPredicate.Builder.item()
                                                                .of(itemLookup,
                                                                        ModItems.MUTANT_SKELETON_CHESTPLATE_ITEM.value()))
                                                        .legs(ItemPredicate.Builder.item()
                                                                .of(itemLookup,
                                                                        ModItems.MUTANT_SKELETON_LEGGINGS_ITEM.value()))
                                                        .feet(ItemPredicate.Builder.item()
                                                                .of(itemLookup,
                                                                        ModItems.MUTANT_SKELETON_BOOTS_ITEM.value()))))))
                .save(writer, NO_BONES_ABOUT_IT_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(Items.COBWEB.getDefaultInstance(), SPIDER_PIG_SPIDER_PIG_ADVANCEMENT.id()))
                .parent(GUNPOWDER_SPICE_ADVANCEMENT.asParent())
                .addCriterion("created_spider_pig",
                        SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity()
                                .of(entityTypeLookup, ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value())))
                .save(writer, SPIDER_PIG_SPIDER_PIG_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(Items.CREEPER_HEAD.getDefaultInstance(), YOU_DA_BOMBY_ADVANCEMENT.id()))
                .parent(ROOT_ADVANCEMENT.asParent())
                .addCriterion("tamed_creeper_minion",
                        TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity()
                                .of(entityTypeLookup, ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value())))
                .save(writer, YOU_DA_BOMBY_ADVANCEMENT.name());
    }
}
