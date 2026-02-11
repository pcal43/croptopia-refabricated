package com.epherical.croptopia.datagen;

import com.epherical.croptopia.register.Content;
import com.epherical.croptopia.register.helpers.FarmlandCrop;
import com.epherical.croptopia.register.helpers.Tree;
import com.epherical.croptopia.register.helpers.TreeCrop;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;

import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;

import java.util.concurrent.CompletableFuture;

public class CroptopiaBlockTagProvider extends FabricTagProvider.BlockTagProvider {


	public CroptopiaBlockTagProvider(FabricDataOutput output,
	                                CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(output, completableFuture);
	}

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        generateSaplings();
        generateBarkLogs();
        generateLeaves();
        // in vanilla for bees only
        generateCrops();
        generateMisc();
    }

    protected void generateSaplings() {
        TagBuilder saplings = this.getOrCreateRawBuilder(BlockTags.SAPLINGS);
        for (TreeCrop crop : TreeCrop.copy()) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(crop.getSaplingBlock());
            if (id != null) saplings.add(TagEntry.element(id));
        }
        for (Tree crop : Tree.copy()) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(crop.getSaplingBlock());
            if (id != null) saplings.add(TagEntry.element(id));
        }
    }

    protected void generateBarkLogs() {
        for (Tree crop : Tree.copy()) {
            // add different log types to log tag of this crop
            TagBuilder treeLogs = this.getOrCreateRawBuilder(crop.getLogBlockTag());
            Identifier log = BuiltInRegistries.BLOCK.getKey(crop.getLog());
            Identifier sLog = BuiltInRegistries.BLOCK.getKey(crop.getStrippedLog());
            Identifier wood = BuiltInRegistries.BLOCK.getKey(crop.getWood());
            Identifier sWood = BuiltInRegistries.BLOCK.getKey(crop.getStrippedWood());
            if (log != null) treeLogs.add(TagEntry.element(log));
            if (sLog != null) treeLogs.add(TagEntry.element(sLog));
            if (wood != null) treeLogs.add(TagEntry.element(wood));
            if (sWood != null) treeLogs.add(TagEntry.element(sWood));

            // make this crop log burnable
            TagBuilder burnable = this.getOrCreateRawBuilder(BlockTags.LOGS_THAT_BURN);
            burnable.add(TagEntry.tag(crop.getLogBlockTag().location()));
        }
    }

    protected void generateLeaves() {
        TagBuilder leaves = this.getOrCreateRawBuilder(BlockTags.LEAVES);
        TagBuilder hoe = this.getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_HOE);
        for (TreeCrop crop : TreeCrop.TREE_CROPS) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(crop.getLeaves());
            if (id != null) {
                leaves.add(TagEntry.element(id));
                hoe.add(TagEntry.element(id));
            }
        }
        for (Tree crop : Tree.copy()) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(crop.getLeaves());
            if (id != null) {
                leaves.add(TagEntry.element(id));
                hoe.add(TagEntry.element(id));
            }
        }
    }

    protected void generateCrops() {
        TagBuilder crops = this.getOrCreateRawBuilder(BlockTags.CROPS);
        for (FarmlandCrop crop : FarmlandCrop.copy()) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(crop.asBlock());
            if (id != null) crops.add(TagEntry.element(id));
        }
        for (TreeCrop crop : TreeCrop.copy()) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(crop.asBlock());
            if (id != null) crops.add(TagEntry.element(id));
        }
    }

    protected void generateMisc() {
        TagBuilder shovel = this.getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_SHOVEL);
        TagBuilder azalea = this.getOrCreateRawBuilder(BlockTags.AZALEA_ROOT_REPLACEABLE);
        TagBuilder drip = this.getOrCreateRawBuilder(BlockTags.DRIPSTONE_REPLACEABLE);
        TagBuilder enderman = this.getOrCreateRawBuilder(BlockTags.ENDERMAN_HOLDABLE);
        Identifier saltOre = BuiltInRegistries.BLOCK.getKey(Content.SALT_ORE_BLOCK);
        if (saltOre != null) {
            shovel.add(TagEntry.element(saltOre));
            azalea.add(TagEntry.element(saltOre));
            drip.add(TagEntry.element(saltOre));
            enderman.add(TagEntry.element(saltOre));
        }
    }
}
