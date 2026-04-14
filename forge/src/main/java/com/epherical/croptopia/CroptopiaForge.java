package com.epherical.croptopia;

import com.epherical.croptopia.biome.CropModifier;
import com.epherical.croptopia.biome.SaltModifier;
import com.epherical.croptopia.biome.TreeModifier;
import com.epherical.croptopia.blocks.CroptopiaCropBlock;
import com.epherical.croptopia.common.ItemNamesV2;
import com.epherical.croptopia.common.MiscNames;
import com.epherical.croptopia.config.CroptopiaConfig;
import com.epherical.croptopia.items.GuideBookItem;
import com.epherical.croptopia.items.SeedItem;
import com.epherical.croptopia.listeners.BlockBreakEvent;
import com.epherical.croptopia.listeners.EntitySpawn;
import com.epherical.croptopia.listeners.Harvest;
import com.epherical.croptopia.listeners.LootTableModification;
import com.epherical.croptopia.loot.AdditionalTableModifier;
import com.epherical.croptopia.loot.EntityModifier;
import com.epherical.croptopia.loot.SpawnChestModifier;
import com.epherical.croptopia.register.Content;
import com.epherical.croptopia.register.helpers.FarmlandCrop;
import com.epherical.croptopia.register.helpers.TreeCrop;
import com.epherical.croptopia.register.helpers.Utensil;
import com.epherical.epherolib.libs.org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.epherical.croptopia.CroptopiaMod.createGroup;
import static com.epherical.croptopia.biome.TreeModifier.SERIALIZER;
import static com.epherical.croptopia.common.MiscNames.MOD_ID;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod("croptopia")
public class CroptopiaForge {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_SERIALIZER =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MiscNames.MOD_ID);
    public static final DeferredRegister<BiomeModifier> BIOME_MODIFIER =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, MiscNames.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MiscNames.MOD_ID);

    public static CreativeModeTab CROPTOPIA_ITEM_GROUP = null;




    public static CroptopiaMod mod;

    public static MinecraftServer server;

    public CroptopiaForge(IEventBus bus, ModContainer modContainer) {

        bus.addListener(this::setup);
        bus.addListener(this::enqueueIMC);
        bus.addListener(this::processIMC);
        bus.addListener(this::doClientStuff);
        BIOME_MODIFIER.register(bus);
        BIOME_SERIALIZER.register(bus);
        BIOME_SERIALIZER.register("salt", SaltModifier::makeCodec);
        GLM.register(bus);
        GLM.register("spawn_loot", SpawnChestModifier.CODEC);
        GLM.register("entity_modifier", EntityModifier.CODEC);
        GLM.register("table_adder", AdditionalTableModifier.CODEC);


        NeoForge.EVENT_BUS.addListener(CroptopiaForge::onWorldLoad);
        NeoForge.EVENT_BUS.register(new LootTableModification());
        NeoForge.EVENT_BUS.register(new Harvest());
        NeoForge.EVENT_BUS.register(new BlockBreakEvent());
        //MinecraftForge.EVENT_BUS.addListener(this::onDatapackRegister);
        //MinecraftForge.EVENT_BUS.register(new CroptopiaVillagerTrades());
        NeoForge.EVENT_BUS.register(new EntitySpawn());
        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config.config);

        // Register ourselves for server and other game events we are interested in
        mod = new CroptopiaMod(new ForgeAdapter(), new CroptopiaConfig(HoconConfigurationLoader.builder(), "croptopia_v3.conf"));
    }

    private void setup(final FMLCommonSetupEvent event) {
        mod.registerCompost();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        ClientFunctions functions = new ClientFunctions();
        functions.registerBlockLayers(block -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutoutMipped()));
        BlockColors colors = Minecraft.getInstance().getBlockColors();
        colors.register(functions.registerLeafColors(), functions.leaves());
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("cookingforblockheads", "RegisterTool", () -> new ItemStack(Content.COOKING_POT));
        InterModComms.sendTo("cookingforblockheads", "RegisterTool", () -> new ItemStack(Content.FOOD_PRESS));
        InterModComms.sendTo("cookingforblockheads", "RegisterTool", () -> new ItemStack(Content.FRYING_PAN));
        InterModComms.sendTo("cookingforblockheads", "RegisterTool", () -> new ItemStack(Content.MORTAR_AND_PESTLE));

        InterModComms.sendTo("cookingforblockheads", "RegisterWaterItem", () -> new ItemStack(Content.WATER_BOTTLE));
        InterModComms.sendTo("cookingforblockheads", "RegisterMilkItem", () -> new ItemStack(Content.MILK_BOTTLE));
    }

    /*public void onDatapackRegister(ServerAboutToStartEvent event) {
        server = event.getServer();
    }*/

    private void processIMC(final InterModProcessEvent event) {
    }


    @SubscribeEvent // You can use SubscribeEvent and let the Event Bus discover methods to call
    public void onServerStarting(FMLDedicatedServerSetupEvent event) {

    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void registerTag(BuildCreativeModeTabContentsEvent event) {
            // not a fan of forges event compared to fabrics.
            if (event.getTab().equals(CreativeModeTabs.NATURAL_BLOCKS)) {
                event.getEntries().putAfter(new ItemStack(Items.MANGROVE_PROPAGULE), new ItemStack(Content.CINNAMON.getSapling()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                TreeCrop.TREE_CROPS.stream().map(TreeCrop::getSaplingItem).map(ItemStack::new).forEachOrdered(stack -> {
                    event.getEntries().putAfter(new ItemStack(Items.FLOWERING_AZALEA), stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
                FarmlandCrop.FARMLAND_CROPS.stream().map(FarmlandCrop::getSeedItem).map(ItemStack::new).forEachOrdered(stack -> {
                    event.getEntries().putAfter(new ItemStack(Items.NETHER_WART), stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
                event.getEntries().putBefore(new ItemStack(Items.COAL_ORE), new ItemStack(Content.SALT_ORE), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            } else if (event.getTab().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
                Utensil.copy().stream().map(ItemStack::new).forEachOrdered(stack -> {
                    event.getEntries().putAfter(new ItemStack(Items.FLINT_AND_STEEL), stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
            }
        }

        @SubscribeEvent
        public static void onRegister(RegisterEvent event) {
            if (event.getRegistryKey().equals(Registries.ITEM)) {
                CROPTOPIA_ITEM_GROUP = CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.croptopia"))
                        .displayItems((featureFlagSet, output) ->
                                BuiltInRegistries.ITEM.entrySet().stream()
                                        .filter(entry -> entry.getKey().location().getNamespace().equals(MOD_ID))
                                        .sorted(Comparator.comparing(entry -> BuiltInRegistries.ITEM.getId(entry.getValue())))
                                        .forEach(entry -> output.accept(entry.getValue())))
                        .icon(() -> new ItemStack(Content.COFFEE)).build();
                Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(MOD_ID, "croptopia"), CROPTOPIA_ITEM_GROUP);
                Content.GUIDE = new GuideBookItem(createGroup());
                event.register(Registries.ITEM, createIdentifier(ItemNamesV2.GUIDE), () -> Content.GUIDE);


                Content.registerItems((id, itemSupplier) -> {
                    if (Content.ITEM_REGISTER.getManipulations().containsKey(id)) {
                        itemSupplier = Content.ITEM_REGISTER.getManipulations().get(id);
                    }
                    Item item = itemSupplier.get();
                    event.register(Registries.ITEM, id, () -> item);
                    if (item instanceof ItemNameBlockItem) {
                        ((ItemNameBlockItem) item).registerBlocks(Item.BY_BLOCK, item);
                    }
                    if (item instanceof SeedItem it) {
                        // maybe not needed anymore
                        CroptopiaCropBlock block = (CroptopiaCropBlock) (it).getBlock();
                        block.setSeed(it);
                    }
                    return item;
                });
            }
            if (event.getRegistryKey().equals(Registries.BLOCK)) {
                Content.registerBlocks((id, supplier) -> {
                    if (Content.BLOCK_REGISTER.getManipulations().containsKey(id)) {
                        supplier = Content.BLOCK_REGISTER.getManipulations().get(id);
                    }
                    Block block = supplier.get();
                    event.register(Registries.BLOCK, id, () -> block);
                    return block;
                });
                mod.platform().registerFlammableBlocks();
            }
        }
    }

    public static Identifier createIdentifier(String name) {
        return Identifier.fromNamespaceAndPath(MiscNames.MOD_ID, name);
    }

    private static void modifyVillagerFoodItems() {
        // todo: implement again
        /*ImmutableMap.Builder<Item, Integer> villagerFoodItems = new ImmutableMap.Builder<Item, Integer>()
                .putAll(VillagerAccess.getItemFoodValues());
        cropItems.forEach(item -> villagerFoodItems.put(item, item.getFood().getHealing()));
        VillagerAccess.setItemFoodValues(villagerFoodItems.build());*/
    }

    private static void modifyVillagerGatherables() {
        // todo: implement again
        /*ImmutableSet.Builder<Item> villagerGatherables = new ImmutableSet.Builder<Item>().addAll(VillagerAccess.getGatherableItems());
        seeds.forEach(villagerGatherables::add);
        cropItems.forEach(villagerGatherables::add);
        VillagerAccess.setGatherableItems(villagerGatherables.build());*/
    }

    private static boolean hasRun;

    public static void onWorldLoad(LevelEvent.Load event) {
        if (!hasRun) {
            modifyVillagerFoodItems();
            modifyVillagerGatherables();
            hasRun = true;
        }
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }
}
