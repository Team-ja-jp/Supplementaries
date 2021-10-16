package net.mehvahdjukaar.supplementaries.setup;


import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.mehvahdjukaar.supplementaries.block.util.CapturedMobsHelper;
import net.mehvahdjukaar.supplementaries.common.AdventurerMapsHandler;
import net.mehvahdjukaar.supplementaries.common.FlowerPotHandler;
import net.mehvahdjukaar.supplementaries.common.capabilities.CapabilitiesHandler;
import net.mehvahdjukaar.supplementaries.compat.CompatHandler;
import net.mehvahdjukaar.supplementaries.configs.RegistryConfigs;
import net.mehvahdjukaar.supplementaries.entities.VillagerTradesHandler;
import net.mehvahdjukaar.supplementaries.events.ItemsOverrideHandler;
import net.mehvahdjukaar.supplementaries.fluids.ModSoftFluids;
import net.mehvahdjukaar.supplementaries.mixins.accessors.ChickenEntityAccessor;
import net.mehvahdjukaar.supplementaries.mixins.accessors.HorseEntityAccessor;
import net.mehvahdjukaar.supplementaries.network.NetworkHandler;
import net.mehvahdjukaar.supplementaries.network.commands.ModCommands;
import net.mehvahdjukaar.supplementaries.world.data.map.CMDreg;
import net.mehvahdjukaar.supplementaries.world.structures.StructureLocator;
import net.mehvahdjukaar.supplementaries.world.structures.StructureRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = Supplementaries.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {

    public static void init(final FMLCommonSetupEvent event) {

        event.enqueueWork(() -> {
            try {

                StructureRegistry.setup();

                StructureLocator.init();

                CompatHandler.init();

                CMDreg.init(event);

                Spawns.registerSpawningStuff();

                CapabilitiesHandler.register();

                ComposterBlock.COMPOSTABLES.put(ModRegistry.FLAX_SEEDS_ITEM.get(), 0.3F);
                ComposterBlock.COMPOSTABLES.put(ModRegistry.FLAX_ITEM.get(), 0.65F);
                ComposterBlock.COMPOSTABLES.put(ModRegistry.FLAX_BLOCK_ITEM.get(), 1);

                ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModRegistry.FLAX_ITEM.get().getRegistryName(), ModRegistry.FLAX_POT);

                FlowerPotHandler.init();

                CapturedMobsHelper.refresh();

                ModSoftFluids.init();

                NetworkHandler.registerMessages();

                LootTableStuff.init();

                registerMobFoods();

                hasFinishedSetup = true;

            } catch (Exception e) {
                Supplementaries.LOGGER.throwing(new Exception("Exception during mod setup:" + e + ". This is a big bug"));
                terminateWhenSetupFails();
            }

        });
    }

    private static void terminateWhenSetupFails(){
        //if setup fails crash the game. idk why it doesn't do that on its own wtf
        Supplementaries.LOGGER.throwing(new Exception("Mod setup has failed to complete. This might be due to some mod incompatibility. Refusing to continue loading with a broken modstate. Next step: crashing this game, no survivors. Executing 69/0"));
        //proper way to crash the game lol
        int a = 69 / 0;
    }

    private static void registerMobFoods() {
        List<ItemStack> chickenFood = new ArrayList<>();
        Collections.addAll(chickenFood, ChickenEntityAccessor.getFoodItems().getItems());
        chickenFood.add(new ItemStack(ModRegistry.FLAX_SEEDS_ITEM.get()));
        ChickenEntityAccessor.setFoodItems(Ingredient.of(chickenFood.stream()));


        List<ItemStack> horseFood = new ArrayList<>();
        Collections.addAll(horseFood, HorseEntityAccessor.getFoodItems().getItems());
        horseFood.add(new ItemStack(ModRegistry.FLAX_ITEM.get()));
        horseFood.add(new ItemStack(ModRegistry.FLAX_BLOCK_ITEM.get()));
        HorseEntityAccessor.setFoodItems(Ingredient.of(horseFood.stream()));
    }

    //damn I hate this. If setup fails forge doesn't do anything and it keeps on going quietly
    private static boolean hasFinishedSetup = false;

    private static boolean firstTagLoad = false;

    //events on setup
    @SubscribeEvent
    public static void onTagLoad(TagsUpdatedEvent event) {

        //using this as a post setup event
        if (!hasFinishedSetup) {
            terminateWhenSetupFails();
        }

        //stuff that needs to be loaded after tags
        if (!firstTagLoad) {
            firstTagLoad = true;
            DispenserStuff.registerBehaviors();
            ItemsOverrideHandler.registerOverrides();

        }
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void villagerTradesEvent(VillagerTradesEvent ev) {
        if (RegistryConfigs.reg.FLAX_ENABLED.get()) {
            if (ev.getType().equals(VillagerProfession.FARMER)) {
                ev.getTrades().get(3).add(new BasicTrade(new ItemStack(ModRegistry.FLAX_SEEDS_ITEM.get(), 15), new ItemStack(net.minecraft.item.Items.EMERALD), 16, 2, 0.05f));
            }
        }
        AdventurerMapsHandler.loadCustomTrades();
        AdventurerMapsHandler.addTrades(ev);
    }

    @SubscribeEvent
    public static void registerWanderingTraderTrades(WandererTradesEvent event) {
        VillagerTradesHandler.registerWanderingTraderTrades(event);
    }


    //TODO: maybe move in /data json
    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent e) {
        LootTableStuff.injectLootTables(e);
    }


}
