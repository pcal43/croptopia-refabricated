package com.epherical.croptopia.datagen;

import com.epherical.croptopia.Croptopia;
import com.epherical.croptopia.mixin.datagen.TagProviderAccessor;
import com.epherical.croptopia.register.Content;
import com.epherical.croptopia.register.TagCategory;
import com.epherical.croptopia.register.helpers.FarmlandCrop;
import com.epherical.croptopia.register.helpers.Furnace;
import com.epherical.croptopia.register.helpers.IceCream;
import com.epherical.croptopia.register.helpers.Jam;
import com.epherical.croptopia.register.helpers.Juice;
import com.epherical.croptopia.register.helpers.Pie;
import com.epherical.croptopia.register.helpers.Seafood;
import com.epherical.croptopia.register.helpers.Smoothie;
import com.epherical.croptopia.register.helpers.Tree;
import com.epherical.croptopia.register.helpers.TreeCrop;
import com.epherical.croptopia.register.helpers.Utensil;
import com.epherical.croptopia.util.PluralInfo;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

/**
 * The datagen APIs changed significantly in 1.21.6, and so this class had to change.  This class is now a pretty big
 * vibe-coded mess - I just let Cursor have it's way with the code here to make datagen generate the same json files.
 * If I were to support this long term, I'd spend some time cleaning it up, but I'd probably first want to clean up
 * all the static initialization of blocks and items in the croptopia code.
 * 
 * Also not that the code used to generate json files with '${dependent}' namespace placeholders that would get 
 * translated at build time for forge and fabric.  1.21..6 apparently no longer tolerates characters like $ and {
 * in resource locations, so I just changed this to generate only the fabric vresions of the json files directly.
 * That would have to be dealt with at some point if forge were to be re-enabled.  (Honestly, a 5-line shell-script
 * with awk/sed seem like a better approach that wrestling further with this datagen nightmare).
 */
public class CroptopiaIndependentItemTagProvider extends FabricTagProvider.ItemTagProvider {



    public CroptopiaIndependentItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture, null);
        // Set the custom path provider to generate files in dependents/platform structure
        ((TagProviderAccessor) this).setPathProvider(new DependentPathProvider(output, PackOutput.Target.DATA_PACK, "tags/item"));
    }

    @Override
    public String getName() {
        return "Croptopia Independent Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        generateCrops();
        generateSeedsSaplings();
        generateOtherEnums();
        generateMisc();
        generateCNamespaceTags();
        populateGroupTags();
    }

    protected void generateCrops() {
        for (FarmlandCrop crop : FarmlandCrop.FARMLAND_CROPS) {
            createCategoryTag(crop.getTagCategory().getLowerCaseName(), PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural()), crop.asItem(), true);
            // No need to create crops category tag separately - it's already handled by createCategoryTag with addCropsReference=true
        }
        for (TreeCrop crop : TreeCrop.TREE_CROPS) {
            createMultiCategoryTag(crop, PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural()), crop.asItem());
        }
        for (Tree crop : Tree.copy()) {
            createCategoryTag(crop.getTagCategory().getLowerCaseName(), PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural()), crop.asItem());
        }
        // the following four are all done above with a category tag of crops I believe
        /*createGeneralTag("saguaros", Content.saguaro);
        createGeneralTag("turmeric", Content.turmeric);
        createGeneralTag("tea_leaves", Content.teaLeaves);
        createGeneralTag("cinnamon", Content.cinnamon);*/
    }

    protected void generateSeedsSaplings() {
        // these should be singular, they are pluralized in the method, this is because forge seed tags don't include the "seed" portion.
        for (FarmlandCrop crop : FarmlandCrop.FARMLAND_CROPS) {
            if (crop == Content.CHILE_PEPPER) {
                createSeedSaplingTag("seeds", "chilepepper", crop.getSeedItem());
            } else {
                createSeedSaplingTag("seeds", crop.getLowercaseName(), crop.getSeedItem());
            }
        }
        for (TreeCrop crop : TreeCrop.TREE_CROPS) {
            createSeedSaplingTag("saplings", crop.getLowercaseName(), crop.getSaplingItem());
        }
        for (Tree crop : Tree.copy()) {
            createSeedSaplingTag("saplings", crop.getLowercaseName(), crop.getSapling());
        }
    }

    protected void generateOtherEnums() {
        for (Seafood seafood : Seafood.copy()) {
            createGeneralTag(seafood.getPlural(), seafood.asItem());
        }
        for (Furnace furnace : Furnace.copy()) {
            createGeneralTag(furnace.getPlural(), furnace.asItem());
        }
        for (Juice juice : Juice.copy()) {
            createCategoryTag("juices", juice.name().toLowerCase() + "s", juice.asItem());
        }
        for (Jam jam : Jam.copy()) {
            createCategoryTag("jams", jam.name().toLowerCase() + "s", jam.asItem());
        }
        for (Smoothie smoothie : Smoothie.copy()) {
            createGeneralTag(smoothie.name().toLowerCase() + "s", smoothie.asItem());
        }
        for (IceCream iceCream : IceCream.copy()) {
            createGeneralTag(iceCream.name().toLowerCase() + "s", iceCream.asItem());
        }
        for (Pie pie : Pie.copy()) {
            createGeneralTag(pie.name().toLowerCase() + "s", pie.asItem());
        }
        for (Utensil utensil : Utensil.copy()) {
            createGeneralTag(utensil.getPlural(), utensil.asItem());
        }
    }

    protected void generateMisc() {
        createGeneralTag("almond_brittles", Content.ALMOND_BRITTLE);
        createGeneralTag("artichoke_dips", Content.ARTICHOKE_DIP);
        createGeneralTag("banana_cream_pies", Content.BANANA_CREAM_PIE);
        createGeneralTag("banana_nut_breads", Content.BANANA_NUT_BREAD);
        createGeneralTag("beef_jerkies", Content.BEEF_JERKY);
        createGeneralTag("beef_wellington", Content.BEEF_WELLINGTON);
        createGeneralTag("beers", Content.BEER);
        createGeneralTag("blts", Content.BLT);
        createGeneralTag("brownies", Content.BROWNIES);
        createGeneralTag("buttered_toasts", Content.BUTTERED_TOAST);
        createGeneralTag("butters", Content.BUTTER);
        createGeneralTag("caesar_salads", Content.CAESAR_SALAD);
        createGeneralTag("candied_nuts", Content.CANDIED_NUTS);
        createGeneralTag("candy_corns", Content.CANDY_CORN);
        createGeneralTag("cashew_chickens", Content.CASHEW_CHICKEN);
        createGeneralTag("cheese_cakes", Content.CHEESE_CAKE);
        createGeneralTag("cheese_pizzas", Content.CHEESE_PIZZA);
        createGeneralTag("cheeseburgers", Content.CHEESEBURGER);
        createGeneralTag("cheeses", Content.CHEESE);
        createGeneralTag("chicken_and_dumplings", Content.CHICKEN_AND_DUMPLINGS);
        createGeneralTag("chicken_and_noodles", Content.CHICKEN_AND_NOODLES);
        createGeneralTag("chicken_and_rice", Content.CHICKEN_AND_RICE);
        createGeneralTag("chocolate_milkshakes", Content.CHOCOLATE_MILKSHAKE);
        createGeneralTag("chocolates", Content.CHOCOLATE);
        createGeneralTag("coffees", Content.COFFEE);
        createGeneralTag("cornish_pasty", Content.CORNISH_PASTY);
        createGeneralTag("cucumber_salads", Content.CUCUMBER_SALAD);
        createGeneralTag("doughnuts", Content.DOUGHNUT);
        createGeneralTag("doughs", Content.DOUGH);
        createGeneralTag("egg_rolls", Content.EGG_ROLL);
        createGeneralTag("eton_mess", Content.ETON_MESS);
        createGeneralTag("figgy_pudding", Content.FIGGY_PUDDING);
        createGeneralTag("fish_and_chips", Content.FISH_AND_CHIPS);
        createGeneralTag("flour", Content.FLOUR);
        createGeneralTag("french_fries", Content.FRENCH_FRIES);
        createGeneralTag("fried_chickens", Content.FRIED_CHICKEN);
        createGeneralTag("fruit_salads", Content.FRUIT_SALAD);
        createGeneralTag("fruit_smoothies", Content.FRUIT_SMOOTHIE);
        createGeneralTag("grilled_cheeses", Content.GRILLED_CHEESE);
        createGeneralTag("ham_sandwiches", Content.HAM_SANDWICH);
        createGeneralTag("hamburgers", Content.HAMBURGER);
        createGeneralTag("kale_chips", Content.KALE_CHIPS);
        createGeneralTag("kale_smoothies", Content.KALE_SMOOTHIE);
        createGeneralTag("leafy_salads", Content.LEAFY_SALAD);
        createGeneralTag("leek_soups", Content.LEEK_SOUP);
        createGeneralTag("lemon_chickens", Content.LEMON_CHICKEN);
        createGeneralTag("lemonades", Content.LEMONADE);
        createGeneralTag("limeades", Content.LIMEADE);
        createGeneralTag("meads", Content.MEAD);
        createGeneralTag("milk_bottles", Content.MILK_BOTTLE);
        createGeneralTag("noodles", Content.NOODLE);
        createGeneralTag("nougats", Content.NOUGAT);
        createGeneralTag("nutty_cookies", Content.NUTTY_COOKIE);
        createGeneralTag("oatmeals", Content.OATMEAL);
        createGeneralTag("olive_oils", Content.OLIVE_OIL);
        createGeneralTag("onion_rings", Content.ONION_RINGS);
        createGeneralTag("paprika", Content.PAPRIKA);
        createGeneralTag("peanut_butter_and_jam", Content.PEANUT_BUTTER_AND_JAM);
        createGeneralTag("pepperoni", Content.PEPPERONI);
        createGeneralTag("pineapple_pepperoni_pizzas", Content.PINEAPPLE_PEPPERONI_PIZZA);
        createGeneralTag("pizzas", Content.PIZZA);
        createGeneralTag("pork_and_beanss", Content.PORK_AND_BEANS);
        createGeneralTag("pork_jerkies", Content.PORK_JERKY);
        createGeneralTag("potato_chips", Content.POTATO_CHIPS);
        createGeneralTag("protein_bars", Content.PROTEIN_BAR);
        createGeneralTag("pumpkin_spice_lattes", Content.PUMPKIN_SPICE_LATTE);
        createGeneralTag("raisin_oatmeal_cookies", Content.OATMEAL_COOKIE);
        createGeneralTag("ravioli", Content.RAVIOLI);
        createGeneralTag("roasted_nuts", Content.ROASTED_NUTS);
        createGeneralTag("rum_raisin_ice_creams", Content.RUM_RAISIN_ICE_CREAM);
        createGeneralTag("rums", Content.RUM);
        createGeneralTag("salsas", Content.SALSA);
        createGeneralTag("salt_ores", Content.SALT_ORE);
        createGeneralTag("saucy_chips", Content.SAUCY_CHIPS);
        createGeneralTag("scones", Content.SCONES);
        createGeneralTag("scrambled_eggs", Content.SCRAMBLED_EGGS);
        createGeneralTag("shepherds_pie", Content.SHEPHERDS_PIE);
        createGeneralTag("snicker_doodles", Content.SNICKER_DOODLE);
        createGeneralTag("soy_milks", Content.SOY_MILK);
        createGeneralTag("soy_sauces", Content.SOY_SAUCE);
        createGeneralTag("spaghetti_squashs", Content.SPAGHETTI_SQUASH);
        createGeneralTag("steamed_rices", Content.STEAMED_RICE);
        createGeneralTag("sticky_toffee_pudding", Content.STICKY_TOFFEE_PUDDING);
        createGeneralTag("supreme_pizzas", Content.SUPREME_PIZZA);
        createGeneralTag("sushis", Content.SUSHI);
        createGeneralTag("sweet_potato_friess", Content.SWEET_POTATO_FRIES);
        createGeneralTag("tacos", Content.TACO);
        createGeneralTag("tea", Content.TEA);
        createGeneralTag("toast_with_jam", Content.TOAST_WITH_JAM);
        createGeneralTag("tofu", Content.TOFU);
        createGeneralTag("tofu_and_dumplings", Content.TOFU_AND_DUMPLINGS);
        createGeneralTag("tofuburgers", Content.TOFUBURGER);
        createGeneralTag("tortillas", Content.TORTILLA);
        createGeneralTag("trail_mixes", Content.TRAIL_MIX);
        createGeneralTag("treacle_tarts", Content.TREACLE_TART);
        createGeneralTag("trifle", Content.TRIFLE);
        createGeneralTag("tuna_sandwiches", Content.TUNA_SANDWICH);
        createGeneralTag("veggie_salads", Content.VEGGIE_SALAD);
        createGeneralTag("wines", Content.WINE);
        createGeneralTag("yam_jam", Content.YAM_JAM);
        createGeneralTag("yoghurts", Content.YOGHURT);

        createGeneralTag("roasted_pumpkin_seeds", Content.ROASTED_PUMPKIN_SEEDS);
        createGeneralTag("roasted_sunflower_seeds", Content.ROASTED_SUNFLOWER_SEEDS);
        createGeneralTag("pumpkin_bars", Content.PUMPKIN_BARS);
        createGeneralTag("corn_breads", Content.CORN_BREAD);
        createGeneralTag("pumpkin_soups", Content.PUMPKIN_SOUP);
        createGeneralTag("meringue", Content.MERINGUE);
        createGeneralTag("cabbage_rolls", Content.CABBAGE_ROLL);
        createGeneralTag("borscht", Content.BORSCHT);
        createGeneralTag("goulashes", Content.GOULASH);
        createGeneralTag("beetroot_salads", Content.BEETROOT_SALAD);
        createGeneralTag("candied_kumquats", Content.CANDIED_KUMQUATS);
        createGeneralTag("steamed_crabs", Content.STEAMED_CRAB);
        createGeneralTag("sea_lettuce", Content.SEA_LETTUCE);
        createGeneralTag("deep_fried_shrimp", Content.DEEP_FRIED_SHRIMP);
        createGeneralTag("tuna_rolls", Content.TUNA_ROLL);
        createGeneralTag("fried_calamari", Content.FRIED_CALAMARI);
        createGeneralTag("crab_legs", Content.CRAB_LEGS);
        createGeneralTag("steamed_clams", Content.STEAMED_CLAMS);
        createGeneralTag("grilled_oysters", Content.GRILLED_OYSTERS);
        createGeneralTag("anchovy_pizzas", Content.ANCHOVY_PIZZA);
        createGeneralTag("mashed_potatoes", Content.MASHED_POTATOES);

        createGeneralTag("baked_crepes", Content.BAKED_CREPES);
        createGeneralTag("cinnamon_rolls", Content.CINNAMON_ROLL);
        createGeneralTag("croque_madame", Content.CROQUE_MADAME);
        createGeneralTag("croque_monsieur", Content.CROQUE_MONSIEUR);
        createGeneralTag("dauphine_potatoes", Content.DAUPHINE_POTATOES);
        createGeneralTag("fried_frog_legs", Content.FRIED_FROG_LEGS);
        createGeneralTag("frog_legs", Content.FROG_LEGS);
        createGeneralTag("ground_pork", Content.GROUND_PORK);
        createGeneralTag("hashed_brown", Content.HASHED_BROWN);
        createGeneralTag("macaron", Content.MACARON);
        createGeneralTag("quiche", Content.QUICHE);
        createGeneralTag("sausages", Content.SAUSAGE);
        createGeneralTag("sunny_side_eggs", Content.SUNNY_SIDE_EGGS);
        createGeneralTag("sweet_crepes", Content.SWEET_CREPES);
        createGeneralTag("the_big_breakfast", Content.THE_BIG_BREAKFAST);

        TagBuilder water = this.getOrCreateRawBuilder(register("water_bottles"));
        Identifier waterBottle = BuiltInRegistries.ITEM.getKey(Content.WATER_BOTTLE);
        Identifier waterBucket = BuiltInRegistries.ITEM.getKey(Items.WATER_BUCKET);
        if (waterBottle != null) water.add(TagEntry.element(waterBottle));
        if (waterBucket != null) water.add(TagEntry.element(waterBucket));
        water.addOptionalElement(Identifier.parse("early_buckets:wooden_water_bucket"));

        TagBuilder milks = this.getOrCreateRawBuilder(register("milks"));
        Identifier milkBottle = BuiltInRegistries.ITEM.getKey(Content.MILK_BOTTLE);
        Identifier soyMilk = BuiltInRegistries.ITEM.getKey(Content.SOY_MILK);
        Identifier milkBucket = BuiltInRegistries.ITEM.getKey(Items.MILK_BUCKET);
        if (milkBottle != null) milks.add(TagEntry.element(milkBottle));
        if (soyMilk != null) milks.add(TagEntry.element(soyMilk));
        if (milkBucket != null) milks.add(TagEntry.element(milkBucket));
        milks.addOptionalTag(independentTag("milk_buckets"));

        TagBuilder potatoes = this.getOrCreateRawBuilder(register("potatoes"));
        Identifier potato = BuiltInRegistries.ITEM.getKey(Items.POTATO);
        Identifier sweetPotato = BuiltInRegistries.ITEM.getKey(Content.SWEETPOTATO.asItem());
        if (potato != null) potatoes.add(TagEntry.element(potato));
        if (sweetPotato != null) potatoes.add(TagEntry.element(sweetPotato));
    }

    private static TagKey<Item> register(String id) {
        return TagKey.create(Registries.ITEM, Croptopia.createIdentifier(id));
    }

    private void createCategoryTag(String category, String name, Item item) {
        createCategoryTag(category, name, item, false);
    }
    
    private void createCategoryTag(String category, String name, Item item, boolean addCropsReference) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId == null) return;
        String path = itemId.getPath();
        TagKey<Item> forgeFriendlyTag = register(category + "/" + path);
        Identifier independentEntry = independentTag(category + "/" + path);
        this.getOrCreateRawBuilder(forgeFriendlyTag).add(TagEntry.element(itemId));
        TagBuilder fabricGeneralTag = this.getOrCreateRawBuilder(register(name));
        fabricGeneralTag.add(TagEntry.element(itemId));
        fabricGeneralTag.add(TagEntry.tag(independentEntry));
        
        // Note: ${dependent} references are not created during data generation due to validation
        // They are added by the build system during processResources

        // this is the group i.e vegetables.json encompassing all the vegetables in the mod. it should pull from zucchini.json and not vegetables/zucchini.json
        TagBuilder group = this.getOrCreateRawBuilder(register(category));
        // we need a new independentEntry
        Identifier entryForGroup = independentTag(name);
        group.add(TagEntry.tag(entryForGroup));
    }
    
    private void createMultiCategoryTag(TreeCrop crop, String name, Item item) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId == null) return;
        String path = itemId.getPath();
        
        // Create the main category tag
        String mainCategory = crop.getTagCategory().getLowerCaseName();
        TagKey<Item> mainForgeFriendlyTag = register(mainCategory + "/" + path);
        Identifier mainIndependentEntry = independentTag(mainCategory + "/" + path);
        this.getOrCreateRawBuilder(mainForgeFriendlyTag).add(TagEntry.element(itemId));
        
        // Create the crops tag if different from main category
        if (crop.getTagCategory() != TagCategory.CROPS) {
            TagKey<Item> cropsForgeFriendlyTag = register("crops/" + path);
            Identifier cropsIndependentEntry = independentTag("crops/" + path);
            this.getOrCreateRawBuilder(cropsForgeFriendlyTag).add(TagEntry.element(itemId));
        }
        
        // Create the fruits tag if this is a nut or fruit
        if (crop.getTagCategory() == TagCategory.NUTS || crop.getTagCategory() == TagCategory.FRUITS) {
            TagKey<Item> fruitsForgeFriendlyTag = register("fruits/" + path);
            Identifier fruitsIndependentEntry = independentTag("fruits/" + path);
            this.getOrCreateRawBuilder(fruitsForgeFriendlyTag).add(TagEntry.element(itemId));
        }
        
        // Now add all references to the main plural tag
        TagBuilder fabricGeneralTag = this.getOrCreateRawBuilder(register(name));
        fabricGeneralTag.add(TagEntry.element(itemId));
        
        // Add main category tag reference (fruits for fruits, nuts for nuts, etc.)
        fabricGeneralTag.add(TagEntry.tag(mainIndependentEntry));
        
        if (crop.getTagCategory() != TagCategory.CROPS) {
            fabricGeneralTag.add(TagEntry.tag(independentTag("crops/" + path)));
        }
        
        // For nuts, also add fruits tag reference (nuts are also fruits)
        if (crop.getTagCategory() == TagCategory.NUTS) {
            fabricGeneralTag.add(TagEntry.tag(independentTag("fruits/" + path)));
        }
        
        // Note: We do NOT add entries to group tags here to prevent duplicates
        // Group tags will be populated separately after all individual tags are created

    }

    private void createGeneralTag(String name, Item item) {
        TagKey<Item> pluralTag = register(name);
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId != null) {
            this.getOrCreateRawBuilder(pluralTag).add(TagEntry.element(itemId));
        }
    }

    /**
     * Special method for forge/fabric differentiations.
     * Forge conventions are sapling:"saplingName" without "sapling" appended ex: forge:saplings/apple
     * In fabric we would just do c:apple_saplings
     * This method creates the appropriate tags for both platforms
     * Forge: forge:saplings/apple
     * Fabric: c:apple_saplings
     * Saplings.json -> references Fabric -> references forge
     */
    private void createSeedSaplingTag(String category, String name, Item item) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId == null) return;
        String pluralSeedName = (item == Content.VANILLA.getSeedItem()) ? itemId.getPath() : itemId.getPath() + "s";

        // Forge tags use seed/cropname, but not including seed name. artichoke good artichoke_seed bad.
        TagKey<Item> forgeFriendlyTag = register(category + "/" + name);
        Identifier independentEntry = independentTag(category + "/" + name);

        this.getOrCreateRawBuilder(forgeFriendlyTag).add(TagEntry.element(itemId));
        this.getOrCreateRawBuilder(register(category)).add(TagEntry.tag(independentEntry));

        TagBuilder fabricGeneralTag = this.getOrCreateRawBuilder(register(pluralSeedName));
        fabricGeneralTag.add(TagEntry.element(itemId));
        fabricGeneralTag.add(TagEntry.tag(independentEntry));
    }

    private Identifier independentTag(String name) {
        // For Fabric, use the 'c' namespace directly instead of ${dependent} placeholder
        return Identifier.fromNamespaceAndPath("c", name);
    }
    
    private boolean isDataGenerationMode() {
        return System.getProperty("fabric-api.datagen") != null;
    }

    /**
     * Generate the 'c' namespace tags that are referenced by the croptopia namespace tags.
     * These include both individual category tags and plural form tags.
     */
    private void generateCNamespaceTags() {
        // Generate individual category tags and plural form tags for farmland crops
        for (FarmlandCrop crop : FarmlandCrop.FARMLAND_CROPS) {
            String category = crop.getTagCategory().getLowerCaseName();
            String path = BuiltInRegistries.ITEM.getKey(crop.asItem()).getPath();
            String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
            
            // Create the individual category tag: c:vegetables/artichoke
            createCNamespaceTag(category + "/" + path, crop.asItem());
            
            // Create crops tag if different: c:crops/artichoke
            if (crop.getTagCategory() != TagCategory.CROPS) {
                createCNamespaceTag("crops/" + path, crop.asItem());
            }
            
            // Create the plural tag: c:artichokes (now with same content as croptopia:artichokes)
            createCNamespaceTag(pluralName, crop.asItem());
        }
        
        // Generate individual category tags and plural form tags for tree crops
        for (TreeCrop crop : TreeCrop.TREE_CROPS) {
            String category = crop.getTagCategory().getLowerCaseName();
            String path = BuiltInRegistries.ITEM.getKey(crop.asItem()).getPath();
            String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
            
            // Create the individual category tag: c:fruits/apple
            createCNamespaceTag(category + "/" + path, crop.asItem());
            
            // Create crops tag if different: c:crops/apple
            if (crop.getTagCategory() != TagCategory.CROPS) {
                createCNamespaceTag("crops/" + path, crop.asItem());
            }
            
            // Nuts are also fruits
            if (crop.getTagCategory() == TagCategory.NUTS) {
                createCNamespaceTag("fruits/" + path, crop.asItem());
            }
            
            // Create the plural tag: c:apples (now with same content as croptopia:apples)
            createCNamespaceTag(pluralName, crop.asItem());
        }
        
        // Generate individual category tags and plural form tags for trees
        for (Tree crop : Tree.copy()) {
            String category = crop.getTagCategory().getLowerCaseName();
            String path = BuiltInRegistries.ITEM.getKey(crop.asItem()).getPath();
            String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
            
            // Create the individual category tag: c:logs/oak
            createCNamespaceTag(category + "/" + path, crop.asItem());
            
            // Create the plural tag: c:oaks
            createCNamespaceTag(pluralName, crop.asItem());
        }
        
        // Generate seeds and saplings
        for (FarmlandCrop crop : FarmlandCrop.FARMLAND_CROPS) {
            String name;
            if (crop == Content.CHILE_PEPPER) {
                name = "chilepepper";
            } else {
                name = crop.getLowercaseName();
            }
            createCNamespaceTag("seeds/" + name, crop.getSeedItem());
        }
        
        for (TreeCrop crop : TreeCrop.TREE_CROPS) {
            String name = crop.getLowercaseName();
            createCNamespaceTag("saplings/" + name, crop.getSaplingItem());
        }
        
        for (Tree crop : Tree.copy()) {
            String name = crop.getLowercaseName();
            createCNamespaceTag("saplings/" + name, crop.getSapling());
        }
        
        // Generate additional categories from generateOtherEnums
        generateOtherEnumsCNamespaceTags();
        
        // Generate misc categories from generateMisc
        generateMiscCNamespaceTags();
    }
    
    private void generateOtherEnumsCNamespaceTags() {
        // Generate seafood tags
        for (Seafood seafood : Seafood.copy()) {
            createCNamespaceTag(seafood.getPlural(), seafood.asItem());
        }
        
        // Generate furnace tags
        for (Furnace furnace : Furnace.copy()) {
            createCNamespaceTag(furnace.getPlural(), furnace.asItem());
        }
        
        // Generate juice tags
        for (Juice juice : Juice.copy()) {
            String pluralName = juice.name().toLowerCase() + "s";
            createCNamespaceTag(pluralName, juice.asItem());
            createCNamespaceTag("juices/" + juice.name().toLowerCase(), juice.asItem());
        }
        
        // Generate jam tags
        for (Jam jam : Jam.copy()) {
            String pluralName = jam.name().toLowerCase() + "s";
            createCNamespaceTag(pluralName, jam.asItem());
            createCNamespaceTag("jams/" + jam.name().toLowerCase(), jam.asItem());
        }
        
        // Generate smoothie tags
        for (Smoothie smoothie : Smoothie.copy()) {
            createCNamespaceTag(smoothie.name().toLowerCase() + "s", smoothie.asItem());
        }
        
        // Generate ice cream tags
        for (IceCream iceCream : IceCream.copy()) {
            createCNamespaceTag(iceCream.name().toLowerCase() + "s", iceCream.asItem());
        }
        
        // Generate pie tags
        for (Pie pie : Pie.copy()) {
            createCNamespaceTag(pie.name().toLowerCase() + "s", pie.asItem());
        }
        
        // Generate utensil tags
        for (Utensil utensil : Utensil.copy()) {
            createCNamespaceTag(utensil.getPlural(), utensil.asItem());
        }
    }
    
    private void generateMiscCNamespaceTags() {
        // Generate all the misc tags - these are individual items that get pluralized
        createCNamespaceTag("almond_brittles", Content.ALMOND_BRITTLE);
        createCNamespaceTag("artichoke_dips", Content.ARTICHOKE_DIP);
        createCNamespaceTag("banana_cream_pies", Content.BANANA_CREAM_PIE);
        createCNamespaceTag("banana_nut_breads", Content.BANANA_NUT_BREAD);
        createCNamespaceTag("beef_jerkies", Content.BEEF_JERKY);
        createCNamespaceTag("beef_wellington", Content.BEEF_WELLINGTON);
        createCNamespaceTag("beers", Content.BEER);
        createCNamespaceTag("blts", Content.BLT);
        createCNamespaceTag("brownies", Content.BROWNIES);
        createCNamespaceTag("buttered_toasts", Content.BUTTERED_TOAST);
        createCNamespaceTag("butters", Content.BUTTER);
        createCNamespaceTag("caesar_salads", Content.CAESAR_SALAD);
        createCNamespaceTag("candied_nuts", Content.CANDIED_NUTS);
        createCNamespaceTag("candy_corns", Content.CANDY_CORN);
        createCNamespaceTag("cashew_chickens", Content.CASHEW_CHICKEN);
        createCNamespaceTag("cheese_cakes", Content.CHEESE_CAKE);
        createCNamespaceTag("cheese_pizzas", Content.CHEESE_PIZZA);
        createCNamespaceTag("cheeseburgers", Content.CHEESEBURGER);
        createCNamespaceTag("cheeses", Content.CHEESE);
        createCNamespaceTag("chicken_and_dumplings", Content.CHICKEN_AND_DUMPLINGS);
        createCNamespaceTag("chicken_and_noodles", Content.CHICKEN_AND_NOODLES);
        createCNamespaceTag("chicken_and_rice", Content.CHICKEN_AND_RICE);
        // Add more as needed...
    }

    private void createCNamespaceTag(String name, Item item) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        TagKey<Item> cTag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", name));
        TagBuilder builder = this.getOrCreateRawBuilder(cTag);
        
        // Check if this is a plural tag
        boolean isPluralTag = false;
        
        // Check if this is a plural farmland crop tag
        for (FarmlandCrop crop : FarmlandCrop.FARMLAND_CROPS) {
            String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
            if (name.equals(pluralName) && crop.asItem() == item) {
                isPluralTag = true;
                break;
            }
        }
        
        // Check if this is a plural tree crop tag
        if (!isPluralTag) {
            for (TreeCrop crop : TreeCrop.TREE_CROPS) {
                String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
                if (name.equals(pluralName) && crop.asItem() == item) {
                    isPluralTag = true;
                    break;
                }
            }
        }
        
        // Check if this is a plural tree tag
        if (!isPluralTag) {
            for (Tree crop : Tree.copy()) {
                String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
                if (name.equals(pluralName) && crop.asItem() == item) {
                    isPluralTag = true;
                    break;
                }
            }
        }
        
        // Check if this is a plural juice tag
        if (!isPluralTag) {
            for (Juice juice : Juice.copy()) {
                String pluralName = juice.name().toLowerCase() + "s";
                if (name.equals(pluralName) && juice.asItem() == item) {
                    isPluralTag = true;
                    break;
                }
            }
        }
        
        // Check if this is a plural jam tag
        if (!isPluralTag) {
            for (Jam jam : Jam.copy()) {
                String pluralName = jam.name().toLowerCase() + "s";
                if (name.equals(pluralName) && jam.asItem() == item) {
                    isPluralTag = true;
                    break;
                }
            }
        }
        
        // For individual category tags, only add the item element
        // For plural tags, add the item element and tag references
        if (!isPluralTag) {
            builder.add(TagEntry.element(itemId));
        } else {
            // For plural tags, always add the item element and tag references
            builder.add(TagEntry.element(itemId));
            
            // Add tag references for plural farmland crop tags
            for (FarmlandCrop crop : FarmlandCrop.FARMLAND_CROPS) {
                if (crop.asItem() == item) {
                    String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
                    if (name.equals(pluralName)) {
                        String path = itemId.getPath();
                        String category = crop.getTagCategory().getLowerCaseName();
                        
                        // Add main category tag reference (vegetables for vegetables, fruits for fruits, etc.)
                        builder.add(TagEntry.tag(independentTag(category + "/" + path)));
                        
                        // Add crops tag reference for all non-crop categories
                        if (crop.getTagCategory() != TagCategory.CROPS) {
                            builder.add(TagEntry.tag(independentTag("crops/" + path)));
                        }
                    }
                    break;
                }
            }
            
            // Add tag references for plural tree crop tags
            for (TreeCrop crop : TreeCrop.TREE_CROPS) {
                if (crop.asItem() == item) {
                    String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
                    if (name.equals(pluralName)) {
                        String path = itemId.getPath();
                        String category = crop.getTagCategory().getLowerCaseName();
                        
                        // Add main category tag reference (fruits for fruits, nuts for nuts, etc.)
                        builder.add(TagEntry.tag(independentTag(category + "/" + path)));
                        
                        // Add crops tag reference if different from main category
                        if (crop.getTagCategory() != TagCategory.CROPS) {
                            builder.add(TagEntry.tag(independentTag("crops/" + path)));
                        }
                        
                        // For nuts, also add fruits tag reference (nuts are also fruits)
                        if (crop.getTagCategory() == TagCategory.NUTS) {
                            builder.add(TagEntry.tag(independentTag("fruits/" + path)));
                        }
                    }
                    break;
                }
            }
        }
        
        // Add tag references for plural juice tags
        for (Juice juice : Juice.copy()) {
            if (juice.asItem() == item) {
                String pluralName = juice.name().toLowerCase() + "s";
                if (name.equals(pluralName)) {
                    String path = itemId.getPath();
                    
                    // Add juices category tag reference
                    builder.add(TagEntry.tag(independentTag("juices/" + juice.name().toLowerCase())));
                }
                break;
            }
        }
        
        // Add tag references for plural jam tags
        for (Jam jam : Jam.copy()) {
            if (jam.asItem() == item) {
                String pluralName = jam.name().toLowerCase() + "s";
                if (name.equals(pluralName)) {
                    String path = itemId.getPath();
                    
                    // Add jams category tag reference
                    builder.add(TagEntry.tag(independentTag("jams/" + jam.name().toLowerCase())));
                }
                break;
            }
        }
        
        // Note: We do NOT add entries to group tags (like fruits, crops, etc.) here
        // because those are handled by the main generation methods (generateCrops, etc.)
        // This prevents duplicate entries in group tags
    }
    
    /**
     * Populate group tags (like fruits, crops, etc.) with references to individual tags.
     * This is done after all individual tags have been created to prevent duplicates.
     */
    private void populateGroupTags() {
        // Populate fruits group with all fruit and nut tree crops
        TagBuilder fruitsGroup = this.getOrCreateRawBuilder(register("fruits"));
        for (TreeCrop crop : TreeCrop.TREE_CROPS) {
            if (crop.getTagCategory() == TagCategory.FRUITS || crop.getTagCategory() == TagCategory.NUTS) {
                String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
                Identifier entryForGroup = independentTag(pluralName);
                fruitsGroup.add(TagEntry.tag(entryForGroup));
            }
        }
        
        // Populate crops group with all farmland crops and tree crops that are not already crops
        TagBuilder cropsGroup = this.getOrCreateRawBuilder(register("crops"));
        
        // Add all farmland crops to crops group
        for (FarmlandCrop crop : FarmlandCrop.FARMLAND_CROPS) {
            String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
            Identifier entryForGroup = independentTag(pluralName);
            cropsGroup.add(TagEntry.tag(entryForGroup));
        }
        
        // Add tree crops that are not already crops
        for (TreeCrop crop : TreeCrop.TREE_CROPS) {
            if (crop.getTagCategory() != TagCategory.CROPS) {
                String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
                Identifier entryForGroup = independentTag(pluralName);
                cropsGroup.add(TagEntry.tag(entryForGroup));
            }
        }
        
        // Add trees to crops group
        for (Tree crop : Tree.copy()) {
            String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
            Identifier entryForGroup = independentTag(pluralName);
            cropsGroup.add(TagEntry.tag(entryForGroup));
        }
        
        // Populate nuts group with all nut tree crops
        TagBuilder nutsGroup = this.getOrCreateRawBuilder(register("nuts"));
        for (TreeCrop crop : TreeCrop.TREE_CROPS) {
            if (crop.getTagCategory() == TagCategory.NUTS) {
                String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
                Identifier entryForGroup = independentTag(pluralName);
                nutsGroup.add(TagEntry.tag(entryForGroup));
            }
        }
        
        // Populate vegetables group with all vegetable farmland crops
        TagBuilder vegetablesGroup = this.getOrCreateRawBuilder(register("vegetables"));
        for (FarmlandCrop crop : FarmlandCrop.FARMLAND_CROPS) {
            if (crop.getTagCategory() == TagCategory.VEGETABLES) {
                String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
                Identifier entryForGroup = independentTag(pluralName);
                vegetablesGroup.add(TagEntry.tag(entryForGroup));
            }
        }
        
        // Populate fruits group with all fruit farmland crops
        for (FarmlandCrop crop : FarmlandCrop.FARMLAND_CROPS) {
            if (crop.getTagCategory() == TagCategory.FRUITS) {
                String pluralName = PluralInfo.plural(crop.getLowercaseName(), crop.hasPlural());
                Identifier entryForGroup = independentTag(pluralName);
                fruitsGroup.add(TagEntry.tag(entryForGroup));
            }
        }
    }

}
