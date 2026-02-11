package com.epherical.croptopia.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Add an additional LootTable as the modifier.
 */
public class AdditionalTableModifier extends LootModifier {
    public static final Supplier<MapCodec<AdditionalTableModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(instance -> {
        return codecStart(instance).and(
                instance.group(
                        Codec.STRING.fieldOf("tableRef").forGetter(o -> o.tableID),
                        Codec.FLOAT.fieldOf("referChance").forGetter(o -> o.referChance)
                )
        ).apply(instance, AdditionalTableModifier::new);
    }));

    private String tableID;
    private final NestedLootTable reference;


    //NestedLootTable.lootTableReference
    private final float referChance;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected AdditionalTableModifier(LootItemCondition[] conditionsIn, String tableID, float chanceToRefer) {
        super(conditionsIn);
        this.referChance = chanceToRefer;
        this.tableID = tableID;
        ResourceKey<LootTable> croptopia = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("croptopia", "gameplay/fishing/fish"));
        this.reference = (NestedLootTable) NestedLootTable.lootTableReference(croptopia).build();
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() <= referChance) {
            ObjectArrayList<ItemStack> items = new ObjectArrayList<>();
            reference.createItemStack(items::add, context);
            return items;
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
