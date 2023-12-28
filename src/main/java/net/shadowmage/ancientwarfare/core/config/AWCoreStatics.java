package net.shadowmage.ancientwarfare.core.config;

import codechicken.lib.util.ArrayUtils;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import scala.Int;

import java.util.HashMap;
import java.util.Hashtable;

public class AWCoreStatics extends ModConfiguration {

	public static final String KEY_ALT_ITEM_USE_1 = "keybind.awCore.alt.item.use.1";
	public static final String KEY_ALT_ITEM_USE_2 = "keybind.awCore.alt.item.use.2";
	public static final String KEY_ALT_ITEM_USE_3 = "keybind.awCore.alt.item.use.3";
	public static final String KEY_ALT_ITEM_USE_4 = "keybind.awCore.alt.item.use.4";
	public static final String KEY_ALT_ITEM_USE_5 = "keybind.awCore.alt.item.use.5";

	public static boolean DEBUG = true;
	public static final String resourcePath = "/assets/ancientwarfare/resources/";
	public static final String utilsExportPath = AWCoreStatics.configPathForFiles + "/export/";

	/*
	 * category names
	 */
	private static final String worldGenSettings = "04_world_gen_settings";
	private static final String researchSettings = "06_research";
	private static final String recipeDetailSettings = "08_recipe_details";
	private static final String recipeResearchDetails = "09_recipe_research_details";

	/*
	 * research options
	 */
	public static boolean useResearchSystem = true;
	public static boolean enableResearchResourceUse = true;
	public static double energyPerResearchUnit = 1D;
	public static double researchPerTick = 1;//TODO add to config

	/*
	 * server options
	 */
	public static boolean fireBlockBreakEvents = true;
	public static boolean includeResearchInChests = true;
	public static double energyPerWorkUnit = 50D;

	/*
	 * Tweaks
	 */
	public static int glowDuration = 6000;
	public static int conquerThreshold = 5;
	public static int normalConquerResistance = 1;
	public static int spawnerConquerResistance = 1;
	public static int eliteConquerResistance = 2;
	public static int bossConquerResistance = 5;
	public static float blockProtectionMulti = 100.0f;
	public static boolean npcDialogue = true;
	public static boolean allowStealing = true;
	public static boolean chestProtection = true;
	public static boolean blockProtection = true;
	public static HashMap modDistanceFromSpawnMap = new HashMap<String, Integer>();
	public static HashMap mobReplacementMap = new HashMap<String, String>();
	private static String[] modDistanceFromSpawnArray;
	private static String[] mobReplacementArray;
	private static String[] defaultMobReplacementArray = {
			"primitivemobs:bewitched_tome > twilightforest:death_tome",
			"grimoireofgaia:yeti > twilightforest:yeti",
			"grimoireofgaia:mimic > primitivemobs:mimic",
			"primitivemobs:mimic > grimoireofgaia:mimic",
			"grimoireofgaia:minotaur > twilightforest:minotaur",
			"grimoireofgaia:witch > minecraft:witch",
			"grimoireofgaia:mummy > minecraft:husk",
			"grimoireofgaia:siren > iceandfire:siren",
			"iceandfire:siren > grimoireofgaia:siren",
			"grimoireofgaia:banshee > mocreatures:wraith",
			"grimoireofgaia:goblin > twilightforest:kobold",
			"grimoireofgaia:goblin_feral > twilightforest:redcap",
			"grimoireofgaia:vampire > dungeonmobs:dmvampire",
			"dungeonmobs:dmvampire > grimoireofgaia:vampire",
			"grimoireofgaia:sharko > oe:drowned",
			"grimoireofgaia:gelatinous_slime > miencraft:slime",
			"mod_lavacow:banshee > mocreatures:wraith",
			"dungeonmobs:dmshrieker > iceandfire:dread_ghoul",
			"dungeonmobs:dmrustmonster > mocreatures:dirtscorpion",
			"dungeonmobs:dmillithid > minecraft:illusion_illager",
			"dungeonmobs:dmeldermob > minecraft:ghast",
			"dungeonmobs:dmhookhorror > iceandfire:dread_beast",
			"dungeonmobs:dmbeholder > twilightforest:mini_ghast",
			"dungeonmobs:dmghost > mocreatures:wraith",
			"dungeonmobs:dmtroll > iceandfire:if_troll",
			"dungeonmobs:dmcavefisher > mocreatures:cavescorpion",
			"dungeonmobs:dmmanticore > mocreatures:plainmanticore",
			"mocreatures:scorpion > mocreatures:dirtscorpion",
			"mocreatures:manticore > mocreatures:plainmanticore",
			"ebwizardry:wizard > primitivemobs:traveling_merchant",
			"ebwizardry:evil_wizard > minecraft:evocation_illager",
			"exoticbirds:owl > zawa:greathornedowl",
			"owls:owl > zawa:greathornedowl",
			"zawa:greathornedowl > owls:owl"
	};
	private static String[] defaultModDistanceFromSpawnArray = {
			"iceandfire,1000",
			"ebwizardry,500"
	};

	public AWCoreStatics(String modid) {
		super(modid);
	}

	@Override
	public void initializeValues() {
		/*
		 * general options
         */
		DEBUG = config.getBoolean("debug_ouput", generalOptions, false, "Enable extra debug console output and runtime checks.\n" + "Can degrade performance if left on and lead to large log files.");

        /*
		 * server options
         */
		fireBlockBreakEvents = config.getBoolean("fire_block_break_events", serverOptions, fireBlockBreakEvents, "Fire Block Break Events If set to false, block-break-events will not be posted for _any_ operations\n" + "effectively negating any block-protection mods/mechanims in place on the server.\n" + "If left at true, block-break events will be posted for any automation or vehicles\n" + "which are changing blocks in the world.  Most will use a reference to their owners-name\n" + "for permissions systems.");
		includeResearchInChests = config.getBoolean("include_research_in_chests", serverOptions, includeResearchInChests, "Include Research In Dungeon Loot Chests\n" + "If set to true, Research Note items will be added to dungeon-chest loot tables.\n" + "If set to false, no research will be added.\n" + "This is the global setting.  Individual research may be toggled in the Research\n" + "section of the config file.");
		energyPerWorkUnit = config.get(serverOptions, "energy_per_work_unit", energyPerWorkUnit, "Energy Per Work Unit\nDefault = 50\n" + "How much Torque energy is generated per worker work tick.\n" + "This is the base number and is further adjusted per worker by worker effectiveness.\n" + "Setting to 0 or below effectively disables  workers.").getDouble();

        /*
		 * client options
         */

        /*
		 * core module world-gen options
         */

        /*
         * research settings
         */
		energyPerResearchUnit = config.get(researchSettings, "energy_used per_research_tick", energyPerResearchUnit, "Energy Per Research Unit\nDefault = 1\n" + "How much energy is consumed per research tick.\n" + "Research generally ticks every game-tick if being worked at.\n" + "Setting to 0 will eliminate the energy/worker requirements for research.\n" + "Setting to higher than 1 will increase the amount of energy needed for research,\n" + "increasing the amount of time/resources required for all research.").getDouble();

		useResearchSystem = config.getBoolean("use_research_system", researchSettings, useResearchSystem, "If set to false, research system will be disabled and all recipes will be available in normal crafting station.");

		enableResearchResourceUse = config.getBoolean("use_research_resources", researchSettings, enableResearchResourceUse, "If set to false, research system will not use resources for research.");

		/*
		 * Tweaks
		 */
		npcDialogue = config.getBoolean("npc_dialogue", tweakOptions, true, "Toggles whether NPCs will chat with the player when right-clicked.");

		allowStealing = config.getBoolean("allow_stealing", tweakOptions, true, "Toggles whether players can steal from NPC loot chests when no one is looking.\n"+"No effect if loot_chest_protection is disabled.");
		chestProtection = config.getBoolean("loot_chest_protection", tweakOptions, true, "Toggles whether players need to steal or claim structures to open NPC loot chests.\n"+"If this is disabled, players can open any loot chests freely.");

		blockProtection = config.getBoolean("block_protection", tweakOptions, true, "Toggles whether (some) blocks in faction-owned structures are harder to mine through.\n"+"If true, (some) blocks on faction-owned land take <block_protection_multiplier> as long to mine.");
		blockProtectionMulti = config.getFloat("block_protection_multiplier", tweakOptions, 100.0f, 0.0f, 1000000.0f , "Controls how much longer it takes to mine blocks on faction-protected land.");

		conquerThreshold = config.getInt("conquer_threshold", tweakOptions, 5, 0, 1000000 , "Controls the max number of enemies that will flee rather than prevent players from claiming a structure.\n"+"For example, if this is set to 1, then even a single enemy left alive will prevent you from claiming a structure.");
		normalConquerResistance = config.getInt("normal_conquer_resistance", tweakOptions, 1, 0, 1000000 , "Controls how many points normal enemies are worth when calculating whether players can claim a structure.");
		spawnerConquerResistance = config.getInt("spawner_conquer_resistance", tweakOptions, 1, 0, 1000000 , "Controls how many points un-spawned enemies are worth when calculating whether players can claim a structure.");
		eliteConquerResistance = config.getInt("elite_conquer_resistance", tweakOptions, 2, 0, 1000000 , "Controls how many points elite enemies are worth when calculating whether players can claim a structure.");
		bossConquerResistance = config.getInt("boss_conquer_resistance", tweakOptions, 5, 0, 1000000 , "Controls how many points boss enemies are worth when calculating whether players can claim a structure.");

		glowDuration = config.getInt("highlight_duration", tweakOptions, 6000, 0, 1000000 , "Controls how long enemies and spawners glow when they are preventing you from claiming a structure or opening a chest, in ticks.\n"+"There are 20 ticks per second, so the default 6000 = 5 minutes.");

		modDistanceFromSpawnArray = config.getStringList("mod_distance_from_spawn", tweakOptions, defaultModDistanceFromSpawnArray, "Set a minimum distance from spawn for AW2 structures containing specific mods.\nFor example, the default prevents AW2 structures containing ElectroBlob's Wizardry mobs from generating within 500 blocks of spawn, and IceandFire structures within 1000 blocks of spawn.");
		// Populate modDistanceFromSpawnMap based on the array
		for (int i = 0; i < modDistanceFromSpawnArray.length; i++) {
			String line = modDistanceFromSpawnArray[i];
			if(line.contains(",")){
				String[] keyValue = line.split(",");
				if(keyValue.length != 2) {
					System.out.println("WARNING: syntax error in config. Line has too many/few commas: "+line);
					continue;
				}
				String modid = keyValue[0];
				String distanceString = keyValue[1];
				try {
					int distance = Integer.parseInt(distanceString);
					modDistanceFromSpawnMap.put(modid, distance);
				}
				catch (NumberFormatException e) {
					System.out.println("WARNING: syntax error in config. Distance must be an Integer: "+line);
					continue;
				}
			}
			else {
				System.out.println("WARNING: syntax error in config. Line missing comma separator: "+line);
				continue;
			}
		}
		mobReplacementArray = config.getStringList("mod_replacement", tweakOptions, defaultMobReplacementArray, "When an AW2 spawner fails to spawn an entity (for example, if the entity is from a mod that is not installed), attempt to spawn the replacement instead.\nIf that fails too, and the mob is hostile, it will spawn a zombie.\nNote that the replacement is not going to happen if the original mob spawns successfully.");
		// Populate mobReplacementMap based on the array
		for (int i = 0; i < mobReplacementArray.length; i++) {
			String line = mobReplacementArray[i];
			if(line.contains(">")){
				String[] keyValue = line.split(">");
				if(keyValue.length != 2) {
					System.out.println("WARNING: syntax error in config. Line has too many/few greater-than signs: "+line);
					continue;
				}
				String originalMob = keyValue[0].trim();
				String replacementMob = keyValue[1].trim();
				mobReplacementMap.put(originalMob, replacementMob);
			}
			else {
				System.out.println("WARNING: syntax error in config. Line missing greater-than separator: "+line);
				continue;
			}
		}
	}

	@Override
	public void initializeCategories() {
		config.addCustomCategoryComment(generalOptions, "General Options\n" + "Affect both client and server.  These configs must match for client and server, or\n" + "strange and probably BAD things WILL happen.");

		config.addCustomCategoryComment(serverOptions, "Server Options\n" + "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" + "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");

		config.addCustomCategoryComment(clientOptions, "Client Options\n" + "Affect only client-side operations.  Many of these options can be set from the in-game Options GUI.\n" + "Server admins can ignore these settings.");

		config.addCustomCategoryComment(tweakOptions, "Options added by AW2 Tweaked.");

		config.addCustomCategoryComment(worldGenSettings, "AW Core World Generation Settings\n" + "Server-side only settings.  These settings affect world generation settings for AWCore.");

		config.addCustomCategoryComment(researchSettings, "Research Settings Section\n" + "Affect both client and server.  These configs must match for client and server, or\n" + "strange and probably BAD things WILL happen.");

		config.addCustomCategoryComment(recipeDetailSettings, "Recipe Detail Settings Section\n" + "Configure recipe enable/disable per item.\n" + "Disabling the recipe effectively disables that item.\n" + "Affect both client and server.  These configs must match for client and server, or\n" + "strange and probably BAD things WILL happen.");

		config.addCustomCategoryComment(recipeResearchDetails, "Recipe Research Detail Settings Section\n" + "Configure enable/disable research for specific recipes.\n" + "Disabling the research removes all research requirements for that item.\n" + "Affect both client and server.  These configs must match for client and server, or\n" + "strange and probably BAD things WILL happen.");
	}

	public static boolean isItemCraftable(Item item) {
		String name = item.getRegistryName().toString();
		return get().getBoolean(name, recipeDetailSettings, true, "");
	}

	public static boolean isItemResearcheable(Item item) {
		String name = item.getRegistryName().toString();
		return get().getBoolean(name, recipeResearchDetails, true, "");
	}

	public static void update() {
		AncientWarfareCore.statics.save();
	}

	public static Configuration get() {
		return AncientWarfareCore.statics.getConfig();
	}
}
