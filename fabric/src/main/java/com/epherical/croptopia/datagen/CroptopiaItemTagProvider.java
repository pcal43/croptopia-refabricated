package com.epherical.croptopia.datagen;

import com.epherical.croptopia.CroptopiaMod;
import com.epherical.croptopia.register.Content;
import com.epherical.croptopia.register.helpers.Tree;
import com.epherical.croptopia.register.helpers.TreeCrop;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class CroptopiaItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public CroptopiaItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture, null);
    }


    @Override
    protected void addTags(HolderLookup.Provider arg) {
        generateSaplings();
        generateBarkLogs();
        // currently, only generates air, but leaves item tag isn't used by vanilla anyway
        // generateLeaves();
        generateMisc();
        generateSeedsEatenByTag(ItemTags.CHICKEN_FOOD);
        generateSeedsEatenByTag(ItemTags.PARROT_FOOD);
    }

    protected void generateSeedsEatenByTag(TagKey<Item> key) {
        TagBuilder builder = this.getOrCreateRawBuilder(key);
        for (Item seed : CroptopiaMod.seeds) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(seed);
            if (id != null) builder.add(TagEntry.element(id));
        }
    }


    protected void generateSaplings() {
        TagBuilder saplings = this.getOrCreateRawBuilder(ItemTags.SAPLINGS);
        for (TreeCrop crop : TreeCrop.copy()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(crop.getSaplingItem());
            if (id != null) saplings.add(TagEntry.element(id));
        }
        for (Tree crop : Tree.copy()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(crop.getSapling());
            if (id != null) saplings.add(TagEntry.element(id));
        }
    }

    protected void generateBarkLogs() {
        for (Tree crop : Tree.copy()) {
            TagBuilder treeLogs = this.getOrCreateRawBuilder(crop.getLogItemTag());
            ResourceLocation log = BuiltInRegistries.ITEM.getKey(crop.getLog().asItem());
            ResourceLocation sLog = BuiltInRegistries.ITEM.getKey(crop.getStrippedLog().asItem());
            ResourceLocation wood = BuiltInRegistries.ITEM.getKey(crop.getWood().asItem());
            ResourceLocation sWood = BuiltInRegistries.ITEM.getKey(crop.getStrippedWood().asItem());
            if (log != null) treeLogs.add(TagEntry.element(log));
            if (sLog != null) treeLogs.add(TagEntry.element(sLog));
            if (wood != null) treeLogs.add(TagEntry.element(wood));
            if (sWood != null) treeLogs.add(TagEntry.element(sWood));

            TagBuilder burnable = this.getOrCreateRawBuilder(ItemTags.LOGS_THAT_BURN);
            burnable.add(TagEntry.tag(crop.getLogItemTag().location()));
        }
    }

    protected void generateLeaves() {
        TagBuilder leaves = this.getOrCreateRawBuilder(ItemTags.LEAVES);
        for (TreeCrop crop : TreeCrop.copy()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(crop.getLeaves().asItem());
            if (id != null) leaves.add(TagEntry.element(id));
        }
        for (Tree crop : Tree.copy()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(crop.getLeaves().asItem());
            if (id != null) leaves.add(TagEntry.element(id));
        }
    }

    protected void generateMisc() {
        TagBuilder villagerSeeds = this.getOrCreateRawBuilder(ItemTags.VILLAGER_PLANTABLE_SEEDS);
        for (Item seed : CroptopiaMod.seeds) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(seed);
            if (id != null) villagerSeeds.add(TagEntry.element(id));
        }
        // explicitly used as dolphin food in vanilla
        TagBuilder fishes = this.getOrCreateRawBuilder(ItemTags.FISHES);
        ResourceLocation anchovy = BuiltInRegistries.ITEM.getKey(Content.ANCHOVY.asItem());
        ResourceLocation calamari = BuiltInRegistries.ITEM.getKey(Content.CALAMARI.asItem());
        ResourceLocation gcalamari = BuiltInRegistries.ITEM.getKey(Content.GLOWING_CALAMARI.asItem());
        ResourceLocation clam = BuiltInRegistries.ITEM.getKey(Content.CLAM.asItem());
        ResourceLocation crab = BuiltInRegistries.ITEM.getKey(Content.CRAB.asItem());
        ResourceLocation oyster = BuiltInRegistries.ITEM.getKey(Content.OYSTER.asItem());
        ResourceLocation roe = BuiltInRegistries.ITEM.getKey(Content.ROE.asItem());
        ResourceLocation shrimp = BuiltInRegistries.ITEM.getKey(Content.SHRIMP.asItem());
        ResourceLocation tuna = BuiltInRegistries.ITEM.getKey(Content.TUNA.asItem());
        if (anchovy != null) fishes.add(TagEntry.element(anchovy));
        if (calamari != null) fishes.add(TagEntry.element(calamari));
        if (gcalamari != null) fishes.add(TagEntry.element(gcalamari));
        if (clam != null) fishes.add(TagEntry.element(clam));
        if (crab != null) fishes.add(TagEntry.element(crab));
        if (oyster != null) fishes.add(TagEntry.element(oyster));
        if (roe != null) fishes.add(TagEntry.element(roe));
        if (shrimp != null) fishes.add(TagEntry.element(shrimp));
        if (tuna != null) fishes.add(TagEntry.element(tuna));
        // fox food: all berries added by croptopia
        TagBuilder foxFood = this.getOrCreateRawBuilder(ItemTags.FOX_FOOD);
        ResourceLocation blackberry = BuiltInRegistries.ITEM.getKey(Content.BLACKBERRY.asItem());
        ResourceLocation blueberry = BuiltInRegistries.ITEM.getKey(Content.BLUEBERRY.asItem());
        ResourceLocation cranberry = BuiltInRegistries.ITEM.getKey(Content.CRANBERRY.asItem());
        ResourceLocation elderberry = BuiltInRegistries.ITEM.getKey(Content.ELDERBERRY.asItem());
        ResourceLocation raspberry = BuiltInRegistries.ITEM.getKey(Content.RASPBERRY.asItem());
        ResourceLocation strawberry = BuiltInRegistries.ITEM.getKey(Content.STRAWBERRY.asItem());
        if (blackberry != null) foxFood.add(TagEntry.element(blackberry));
        if (blueberry != null) foxFood.add(TagEntry.element(blueberry));
        if (cranberry != null) foxFood.add(TagEntry.element(cranberry));
        if (elderberry != null) foxFood.add(TagEntry.element(elderberry));
        if (raspberry != null) foxFood.add(TagEntry.element(raspberry));
        if (strawberry != null) foxFood.add(TagEntry.element(strawberry));
        // piglin food: more cannibalism (which already happens in vanilla)
        TagBuilder piglinFood = this.getOrCreateRawBuilder(ItemTags.PIGLIN_FOOD);
        ResourceLocation hamSandwich = BuiltInRegistries.ITEM.getKey(Content.HAM_SANDWICH);
        ResourceLocation pepperoni = BuiltInRegistries.ITEM.getKey(Content.PEPPERONI);
        ResourceLocation porkAndBeans = BuiltInRegistries.ITEM.getKey(Content.PORK_AND_BEANS);
        ResourceLocation porkJerky = BuiltInRegistries.ITEM.getKey(Content.PORK_JERKY);
        ResourceLocation rawBacon = BuiltInRegistries.ITEM.getKey(Content.RAW_BACON);
        ResourceLocation cookedBacon = BuiltInRegistries.ITEM.getKey(Content.COOKED_BACON.asItem());
        if (hamSandwich != null) piglinFood.add(TagEntry.element(hamSandwich));
        if (pepperoni != null) piglinFood.add(TagEntry.element(pepperoni));
        if (porkAndBeans != null) piglinFood.add(TagEntry.element(porkAndBeans));
        if (porkJerky != null) piglinFood.add(TagEntry.element(porkJerky));
        if (rawBacon != null) piglinFood.add(TagEntry.element(rawBacon));
        if (cookedBacon != null) piglinFood.add(TagEntry.element(cookedBacon));
    }

}
