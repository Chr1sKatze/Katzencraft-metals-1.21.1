package net.chriskatze.katzencraftmetals;

import net.chriskatze.katzencraftmetals.block.ModBlocks;
import net.chriskatze.katzencraftmetals.item.ModItemGroups;
import net.chriskatze.katzencraftmetals.item.ModItems;
import net.chriskatze.katzencraftmetals.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KatzencraftMetals implements ModInitializer {
	public static final String MOD_ID = "katzencraftmetals";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// THIS CODE RUNS AS SOON AS MINECRAFT IS IN A MOD-LOAD-READY STATE --------------------------------------------
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModWorldGeneration.generateModWorldGeneration();
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
	}

	private void onServerStarted(MinecraftServer server) {
		// Force disable phantom spawn
		server.getGameRules().get(GameRules.DO_INSOMNIA).set(false, server);
		System.out.println("[KatzencraftMetals] Phantom spawning disabled via gamerule doInsomnia.");
		// Force disable fire spread
		server.getGameRules().get(GameRules.DO_FIRE_TICK).set(false, server);
		System.out.println("[KatzencraftMetals] Fire ticks disabled via gamerule doFireTick.");
		// Force enable keep inventory
		server.getGameRules().get(GameRules.KEEP_INVENTORY).set(false, server);
		System.out.println("[KatzencraftMetals] Keep inventory enabled via gamerule keepInventory.");
	}
}