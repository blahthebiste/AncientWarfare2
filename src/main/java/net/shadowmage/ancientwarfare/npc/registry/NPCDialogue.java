package net.shadowmage.ancientwarfare.npc.registry;

/*
    Helper class for determining what an NPC will say when interacted with.
    Reads all dialogue from assets/ancientwarfare/registry/npc/dialogue.json
 */

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class NPCDialogue {

    public static DialogueJSON dialogueData;

    // Initializes the dialogue lists
    public static class Parser implements IRegistryDataParser {
        @Override
        public String getName() {
            return "dialogue";
        }

        @SuppressWarnings("squid:S2696")
        @Override
        public void parse(JsonObject json) {
            Gson gson = new Gson();
            dialogueData = gson.fromJson(json, DialogueJSON.class);
        }
    }


    // Figures out what message to send the given player, based on a variety of factors.
    public static void speakToPlayer(EntityPlayer player, NpcFaction npc) {
        boolean isFemale = npc.isFemale();
        boolean isHostile = npc.isHostileTowards(player);
        double seed = npc.dialogueSeed;
        String factionName = npc.getFaction();
        String npcName = npc.getName();
        String proffession = npc.getNpcType();
        String message = getRandomDialogue(factionName, isHostile, seed);
        speakToPlayer(player, TextFormatting.YELLOW+"[" + npcName + "] "+ TextFormatting.WHITE + message);
    }

    // Sends a chat message to the given player.
    public static void speakToPlayer(EntityPlayer player, String message) {
        player.sendMessage(new TextComponentString(message));
    }

    // Searches through all possible dialogue options and picks a valid one.
    // Uses the seeded RNG that each NPC has.
    private static String getRandomDialogue(String factionName, boolean isHostile, double seed) {
        FactionJSON factionJSON;
        ArrayList<String> validLines;
        switch (factionName) {
            case "empire":
                factionJSON = dialogueData.empire;
                break;
            case "norska":
                factionJSON = dialogueData.norska;
                break;
            case "sarkonid":
                factionJSON = dialogueData.sarkonid;
                break;
            case "xoltec":
                factionJSON = dialogueData.xoltec;
                break;
            case "witchbane":
                factionJSON = dialogueData.witchbane;
                break;
            case "nogg":
                factionJSON = dialogueData.nogg;
                break;
            case "reiksgard":
                factionJSON = dialogueData.reiksgard;
                break;
            case "shakayana":
                factionJSON = dialogueData.shakayana;
                break;
            case "zamurai":
                factionJSON = dialogueData.zamurai;
                break;
            case "buffloka":
                factionJSON = dialogueData.buffloka;
                break;
            case "zimba":
                factionJSON = dialogueData.zimba;
                break;
            case "kong":
                factionJSON = dialogueData.kong;
                break;
            case "orc":
                factionJSON = dialogueData.orc;
                break;
            case "brigand":
                factionJSON = dialogueData.brigand;
                break;
            case "pirate":
                factionJSON = dialogueData.pirate;
                break;
            case "elf":
                factionJSON = dialogueData.elf;
                break;
            case "dwarf":
                factionJSON = dialogueData.dwarf;
                break;
            case "hobbit":
                factionJSON = dialogueData.hobbit;
                break;
            case "ent":
                factionJSON = dialogueData.ent;
                break;
            case "gnome":
                factionJSON = dialogueData.gnome;
                break;
            case "gremlin":
                factionJSON = dialogueData.gremlin;
                break;
            case "giant":
                factionJSON = dialogueData.giant;
                break;
            case "smingol":
                factionJSON = dialogueData.smingol;
                break;
            case "mindflayer":
                factionJSON = dialogueData.mindflayer;
                break;
            case "minossian":
                factionJSON = dialogueData.minossian;
                break;
            case "guild":
                factionJSON = dialogueData.guild;
                break;
            case "sealsker":
                factionJSON = dialogueData.sealsker;
                break;
            case "vyncan":
                factionJSON = dialogueData.vyncan;
                break;
            case "lizardmen":
                factionJSON = dialogueData.lizardmen;
                break;
            case "coven":
                factionJSON = dialogueData.coven;
                break;
            case "rakshasa":
                factionJSON = dialogueData.rakshasa;
                break;
            case "icelord":
                factionJSON = dialogueData.icelord;
                break;
            case "amazon":
                factionJSON = dialogueData.amazon;
                break;
            case "ishtari":
                factionJSON = dialogueData.ishtari;
                break;
            case "barbarian":
                factionJSON = dialogueData.barbarian;
                break;
            case "klown":
                factionJSON = dialogueData.klown;
                break;
            case "evil":
                factionJSON = dialogueData.evil;
                break;
            case "good":
                factionJSON = dialogueData.good;
                break;
            case "undead":
                factionJSON = dialogueData.undead;
                break;
            case "monster":
                factionJSON = dialogueData.monster;
                break;
            case "vampire":
                factionJSON = dialogueData.vampire;
                break;
            case "beast":
                factionJSON = dialogueData.beast;
                break;
            case "wizardly":
                factionJSON = dialogueData.wizardly;
                break;
            case "demon":
                factionJSON = dialogueData.demon;
                break;
            default:
                AncientWarfareNPC.LOG.error("ERROR: faction " + factionName + " not recognized!");
                return "I have no valid dialogue! Please notify lumberjacksparrow in the AncientWarfare discord.";
        }
        if (isHostile) {
            validLines = factionJSON.hostile;
        } else {
            validLines = factionJSON.friendly;
        }
        // TODO: filter dialogue lines by gender and profession as well
        // Use the seed from the NPC to pick an option from the list:
        int index = (int) (seed * validLines.size());
        return validLines.get(index);
    }

    // Contains the data for all dialogue for all factions
    public class DialogueJSON {
        FactionJSON empire;
        FactionJSON norska;
        FactionJSON sarkonid;
        FactionJSON xoltec;
        FactionJSON witchbane;
        FactionJSON nogg;
        FactionJSON reiksgard;
        FactionJSON shakayana;
        FactionJSON zamurai;
        FactionJSON buffloka;
        FactionJSON zimba;
        FactionJSON kong;
        FactionJSON orc;
        FactionJSON brigand;
        FactionJSON pirate;
        FactionJSON elf;
        FactionJSON dwarf;
        FactionJSON hobbit;
        FactionJSON ent;
        FactionJSON gnome;
        FactionJSON gremlin;
        FactionJSON giant;
        FactionJSON smingol;
        FactionJSON mindflayer;
        FactionJSON minossian;
        FactionJSON guild;
        FactionJSON sealsker;
        FactionJSON vyncan;
        FactionJSON lizardmen;
        FactionJSON coven;
        FactionJSON rakshasa;
        FactionJSON icelord;
        FactionJSON amazon;
        FactionJSON ishtari;
        FactionJSON barbarian;
        FactionJSON klown;
        FactionJSON evil;
        FactionJSON good;
        FactionJSON undead;
        FactionJSON monster;
        FactionJSON vampire;
        FactionJSON beast;
        FactionJSON wizardly;
        FactionJSON demon;

        @Override
        public String toString() {
            return "DialogueJSON{" +
                    "empireJSON=" + empire +
                    ", norskaJSON=" + norska +
                    ", sarkonidJSON=" + sarkonid +
                    ", xoltecJSON=" + xoltec +
                    ", witchbaneJSON=" + witchbane +
                    ", noggJSON=" + nogg +
                    ", reiksgardJSON=" + reiksgard +
                    ", shakayanaJSON=" + shakayana +
                    ", zamuraiJSON=" + zamurai +
                    ", bufflokaJSON=" + buffloka +
                    ", zimbaJSON=" + zimba +
                    ", kongJSON=" + kong +
                    ", orcJSON=" + orc +
                    ", brigandJSON=" + brigand +
                    ", pirateJSON=" + pirate +
                    ", elfJSON=" + elf +
                    ", dwarfJSON=" + dwarf +
                    ", hobbitJSON=" + hobbit +
                    ", entJSON=" + ent +
                    ", gnomeJSON=" + gnome +
                    ", gremlinJSON=" + gremlin +
                    ", giantJSON=" + giant +
                    ", smingolJSON=" + smingol +
                    ", mindflayerJSON=" + mindflayer +
                    ", minossianJSON=" + minossian +
                    ", guildJSON=" + guild +
                    ", sealskerJSON=" + sealsker +
                    ", vyncanJSON=" + vyncan +
                    ", lizardmenJSON=" + lizardmen +
                    ", covenJSON=" + coven +
                    ", rakshasaJSON=" + rakshasa +
                    ", icelordJSON=" + icelord +
                    ", amazonJSON=" + amazon +
                    ", ishtariJSON=" + ishtari +
                    ", barbarianJSON=" + barbarian +
                    ", klownJSON=" + klown +
                    ", evilJSON=" + evil +
                    ", goodJSON=" + good +
                    ", undeadJSON=" + undead +
                    ", monsterJSON=" + monster +
                    ", vampireJSON=" + vampire +
                    ", beastJSON=" + beast +
                    ", wizardlyJSON=" + wizardly +
                    ", demonJSON=" + demon +
                    '}';
        }
    }

    // Only used inside of DialogueJSON.
    public class FactionJSON {
        ArrayList<String> friendly;
        ArrayList<String> hostile;

        @Override
        public String toString() {
            return "FactionJSON{" +
                    "friendlyDialogue=" + friendly +
                    ", hostileDialogue=" + hostile +
                    '}';
        }
    }

}
