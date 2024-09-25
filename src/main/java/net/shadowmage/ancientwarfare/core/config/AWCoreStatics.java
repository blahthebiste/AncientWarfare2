package net.shadowmage.ancientwarfare.core.config;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.util.ArrayList;
import java.util.HashMap;

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
	public static int batteringRamBaseDamage = 5;
	public static int nemesisRepChange = 1;

	public static float invisibilityFollowRangePenalty = 0.1f;
	public static float sneakingFollowRangePenalty = 0.5f;
	public static float obscuredFollowRangePenalty = 0.75f;
	public static float blockProtectionMulti = 100.0f;

	public static boolean combatNPCsRequireFood = true;
	public static boolean npcDialogue = true;
	public static boolean allowStealing = true;
	public static boolean chestProtection = true;
	public static boolean blockProtection = true;
	public static boolean floatingIslands = false;
	public static boolean demonsImmuneToFire = true;
	public static boolean nemesisFactions = true;
	public static boolean showSmallNemesisRepChanges = true;
	public static boolean showLargeNemesisRepChanges = true;
//	public static boolean spawnersRequireLineOfSight = false;

	public static HashMap modDistanceFromSpawnMap = new HashMap<String, Integer>();
	public static HashMap mobReplacementMap = new HashMap<String, String>();
	public static HashMap nemesisFactionsMap = new HashMap<String, String>();
	public static ArrayList<ResourceLocation> medicItems = new ArrayList<>();
    public static float meleeReachModifier = 0.0F;

    private static String[] modDistanceFromSpawnArray;
	private static String[] mobReplacementArray;
	private static String[] nemesisFactionsArray;
	private static String[] medicItemsPlaceholder;
	private static String[] defaultMobReplacementArray = {
			"primitivemobs:bewitched_tome > twilightforest:death_tome",
			"primitivemobs:support_creeper > minecraft:creeper",
			"primitivemobs:skeleton_warrior > minecraft:skeleton",
			"primitivemobs:baby_spider > minecraft:spider",
			"primitivemobs:treasure_slime > minecraft:slime",
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
			"grimoireofgaia:harpy_wizard > twilightforest:skeleton_druid",
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
	private static String[] defaultMedicItems = {
			"minecraft:apple"
	};
	private static String[] defaultNemesisFactionsArray = {
			"good < evil",
			"evil < good",
			"pirate < empire",
			"empire < buffloka",
			"norska < icelord",
			"icelord < norska",
			"wizardly < witchbane",
			"witchbane < wizardly",
			"zimba < kong",
			"kong < zimba",
			"gnome < giant",
			"giant < gnome"
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

		nemesisFactions = config.getBoolean("nemesis_factions", tweakOptions, true, "Enables/disables the Nemesis Factions mechanic. This causes the player to gain rep when killing an NPC, in all factions that hate that NPC's faction.");
		showSmallNemesisRepChanges = config.getBoolean("show_small_nemesis_rep_changes", tweakOptions, false, "Toggles whether players receive messages in chat telling them whenever they gain rep from the Nemesis Faction mechanic.");
		showLargeNemesisRepChanges = config.getBoolean("show_large_nemesis_rep_changes", tweakOptions, true, "Toggles whether players receive messages in chat telling them that they have gained favor with a previously hostile faction via the Nemesis Faction mechanic.");

		allowStealing = config.getBoolean("allow_stealing", tweakOptions, true, "Toggles whether players can steal from NPC loot chests when no one is looking.\n"+"No effect if loot_chest_protection is disabled.");
		chestProtection = config.getBoolean("loot_chest_protection", tweakOptions, true, "Toggles whether players need to steal or claim structures to open NPC loot chests.\n"+"If this is disabled, players can open any loot chests freely.");

//		spawnersRequireLineOfSight = config.getBoolean("spawners_require_line_of_sight", tweakOptions, true, "Toggles whether advanced spawners require line of sight to a player to spawn things.\n"+"In the original AW2, this is false.");

		demonsImmuneToFire = config.getBoolean("demons_immune_to_fire", tweakOptions, true, "Toggles whether NPCs from the demon faction are immune to fire and lava damage.");
		combatNPCsRequireFood = config.getBoolean("combat_NPCs_require_food", tweakOptions, true, "Toggles whether player-owned combat NPCs require food the same way that workers do. If this is set to false, combat NPCs will NEVER need to eat.");

		blockProtection = config.getBoolean("block_protection", tweakOptions, true, "Toggles whether (some) blocks in faction-owned structures are harder to mine through.\n"+"If true, (some) blocks on faction-owned land take <block_protection_multiplier> as long to mine.");
		blockProtectionMulti = config.getFloat("block_protection_multiplier", tweakOptions, 100.0f, 0.0f, 1000000.0f , "Controls how much longer it takes to mine blocks on faction-protected land.");

		invisibilityFollowRangePenalty = config.getFloat("invisibility_follow_range_penalty", tweakOptions, 0.1f, 0.0f, 1.0f , "NPCs follow range is multiplied by this when they are targeting invisible entities.\n"+"For example, the default value of 0.1 means that you cannot be targeted by NPCs while invisible until you are 90% of the way to them (very close).\n"+"If you set this to 1, then NPCs can target invisible entities just as well as non-invisible ones.");
		sneakingFollowRangePenalty = config.getFloat("sneaking_follow_range_penalty", tweakOptions, 0.5f, 0.0f, 1.0f , "NPCs follow range is multiplied by this when they are targeting sneaking entities.\n"+"For example, the default value of 0.5 means that you cannot be targeted by NPCs while sneaking until you are 50% of the way to them.\n"+"If you set this to 1, then NPCs can target sneaking entities just as well as non-sneaking ones.");
		obscuredFollowRangePenalty = config.getFloat("obscured_follow_range_penalty", tweakOptions, 0.75f, 0.0f, 1.0f , "NPCs follow range is multiplied by this when they are targeting entities obscured behind blocks.\n"+"For example, the default value of 0.75 means that you cannot be targeted by NPCs while they do until you are 75% of the way to them.\n"+"If you set this to 1, then NPCs can target obscured entities just as well as ones they can directly see.");
		meleeReachModifier = config.getFloat("melee_reach_modifier", tweakOptions, 0.0f, -10.0f, +10.0f , "Add this number to the melee reach of NPCs. Put a negative number here to lower their reach.\n"+"Default reach when this is 0 is about 2.5 blocks.");

//		floatingIslands = config.getBoolean("floating_islands", tweakOptions, false, "Toggles whether island structures in the ocean float on top of the water, or fill in the space beneath them with solid blocks.\n"+"\ttrue = islands float above water\n"+"\tfalse = islands replace all water beneath them with solid blocks (original AW2 style)");

		batteringRamBaseDamage = config.getInt("battering_ram_base_damage", tweakOptions, 5, 0, 1000000 , "Controls the amount of damage battering rams deal (before their material bonus is applied.)");

		conquerThreshold = config.getInt("conquer_threshold", tweakOptions, 5, 0, 1000000 , "Controls the max number of enemies that will flee rather than prevent players from claiming a structure.\n"+"For example, if this is set to 1, then even a single enemy left alive will prevent you from claiming a structure.");
		normalConquerResistance = config.getInt("normal_conquer_resistance", tweakOptions, 1, 0, 1000000 , "Controls how many points normal enemies are worth when calculating whether players can claim a structure.");
		spawnerConquerResistance = config.getInt("spawner_conquer_resistance", tweakOptions, 1, 0, 1000000 , "Controls how many points un-spawned enemies are worth when calculating whether players can claim a structure.");
		eliteConquerResistance = config.getInt("elite_conquer_resistance", tweakOptions, 2, 0, 1000000 , "Controls how many points elite enemies are worth when calculating whether players can claim a structure.");
		bossConquerResistance = config.getInt("boss_conquer_resistance", tweakOptions, 5, 0, 1000000 , "Controls how many points boss enemies are worth when calculating whether players can claim a structure.");

		glowDuration = config.getInt("highlight_duration", tweakOptions, 6000, 0, 1000000 , "Controls how long enemies and spawners glow when they are preventing you from claiming a structure or opening a chest, in ticks.\n"+"There are 20 ticks per second, so the default 6000 = 5 minutes.");

		nemesisRepChange = config.getInt("nemesis_rep_change", tweakOptions, 1, -1000, 1000 , "Controls how much rep you gain/lose in a nemesis faction when you kill a member of their nemesis faction.\n"+"Does nothing if the nemesis_factions option is false.");

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
		mobReplacementArray = config.getStringList("mob_replacement", tweakOptions, defaultMobReplacementArray, "When an AW2 spawner fails to spawn an entity (for example, if the entity is from a mod that is not installed), attempt to spawn the replacement instead.\nIf that fails too, and the mob is hostile, it will spawn a zombie.\nNote that the replacement is not going to happen if the original mob spawns successfully.");
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

		nemesisFactionsArray = config.getStringList("nemesis_factions_list", tweakOptions, defaultNemesisFactionsArray, "Defines faction Nemesis relations. Whenever you kill a member of the faction before the '<',\n"+"you gain favor with the faction after the '<'. Amount of rep gained is defined by nemesis_rep_change.");
		// Populate mobReplacementMap based on the array
		for (int i = 0; i < nemesisFactionsArray.length; i++) {
			String line = nemesisFactionsArray[i];
			if(line.contains("<")){
				String[] keyValue = line.split("<");
				if(keyValue.length != 2) {
					System.out.println("WARNING: syntax error in config. Line has too many/few less-than signs: "+line);
					continue;
				}
				String dyingFaction = keyValue[0].trim(); // The faction being killed
				String beneficiaryFaction = keyValue[1].trim(); // The faction happy about it
				nemesisFactionsMap.put(dyingFaction, beneficiaryFaction);
			}
			else {
				System.out.println("WARNING: syntax error in config. Line missing less-than separator: "+line);
				continue;
			}
		}

		medicItemsPlaceholder = config.getStringList("medic_items", tweakOptions, defaultMedicItems, "Defines valid item IDs that will be used by NPC medics. Giving an NPC one of these items to equip turns them into a medic.\n"+"This must not be empty! You need to define at least 1 item for medics to use.\n"+"If you do not, the default item list will be used.");
		// Populate medicItems based on the array
		for (int i = 0; i < medicItemsPlaceholder.length; i++) {
			String line = medicItemsPlaceholder[i].trim();
			ResourceLocation itemResourceLocation = new ResourceLocation(line);
			if(Item.REGISTRY.getObject(itemResourceLocation) != null) {
				medicItems.add(itemResourceLocation);
				System.out.println("INFO: medic item \""+line+"\" added.");
			}
			else {
				System.out.println("WARNING: medic item \""+line+"\" could not be found in the registry. Skipping.");
			}
		}
		if(medicItems.size() < 1) {
			System.out.println("WARNING: no valid items found in medic_items! Using default instead.");
			for (int i = 0; i < defaultMedicItems.length; i++) {
				String line = defaultMedicItems[i].trim();
				ResourceLocation itemResourceLocation = new ResourceLocation(line);
				medicItems.add(itemResourceLocation);
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
