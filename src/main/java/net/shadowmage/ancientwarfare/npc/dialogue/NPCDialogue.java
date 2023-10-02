package net.shadowmage.ancientwarfare.npc.dialogue;

/*
    Helper class for determining what an NPC will say when interacted with.
 */

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

import java.util.ArrayList;
import java.util.Random;

public class NPCDialogue {
    // List of all possible dialogue messages
    public static ArrayList<PossibleMessage> dialogueListAll = new ArrayList<>();

    // Initializes the dialogue lists
    public static void init(){
        dialogueListAll.add(new PossibleMessage("Snoo-snoo? Never heard of it.","amazon"));
        dialogueListAll.add(new PossibleMessage("I wanted to multiclass into Wizard, but my DM wouldn't allow it.","barbarian"));
        dialogueListAll.add(new PossibleMessage("*Low growl*","beast"));
        dialogueListAll.add(new PossibleMessage("Hand over yer valuables! Hah, I'm just kidding.","brigand"));
        dialogueListAll.add(new PossibleMessage("Friends of the Empire are not welcome here.","buffloka"));
        dialogueListAll.add(new PossibleMessage("Hello there, my pretty!","coven"));
        dialogueListAll.add(new PossibleMessage("Why would I ever go back to the Nether? The weather is much nicer up here!","demon"));
        dialogueListAll.add(new PossibleMessage("Diggy diggy hole!","dwarf"));
        dialogueListAll.add(new PossibleMessage("Stay out of the Ominous Woods. It's dangerous for a mortal like you.","elf"));
        dialogueListAll.add(new PossibleMessage("The Empire just wants to share its prosperity. Why do so many resist?","empire"));
        dialogueListAll.add(new PossibleMessage("*Tree noises*","ent"));
        dialogueListAll.add(new PossibleMessage("Come to the dark side! We have cookies!","evil"));
        dialogueListAll.add(new PossibleMessage("Eek! A gnome!","giant"));
        dialogueListAll.add(new PossibleMessage("Eek! A giant!","gnome"));
        dialogueListAll.add(new PossibleMessage("Light's blessings upon you!","good"));
        dialogueListAll.add(new PossibleMessage("Klee-hee!","gremlin"));
        dialogueListAll.add(new PossibleMessage("Ah, I see you are an aspiring adventurer!","guild"));
        dialogueListAll.add(new PossibleMessage("I never should have left the Shire...","hobbit"));
        dialogueListAll.add(new PossibleMessage("Winter is coming!","icelord"));
        dialogueListAll.add(new PossibleMessage("Who has disturbed the dead? They need their rest.","ishtari"));
        dialogueListAll.add(new PossibleMessage("*HONK HONK*","klown"));
        dialogueListAll.add(new PossibleMessage("Brave of an outsider like yourself to step foot in our domain.","kong"));
        dialogueListAll.add(new PossibleMessage("The spiders around here are the tastiest!","lizardman"));
        dialogueListAll.add(new PossibleMessage("Flayed any good minds lately?","mindflayer"));
        dialogueListAll.add(new PossibleMessage("You are on sacred ground.","minossian"));
        dialogueListAll.add(new PossibleMessage("*Growls at you*","monster"));
        dialogueListAll.add(new PossibleMessage("We are always accommodating to travelers like yourself. Feel free to stay a while!","nogg"));
        dialogueListAll.add(new PossibleMessage("Come on inside, out of the cold!","norska"));
        dialogueListAll.add(new PossibleMessage("I smell the blood of my brothers on you... You are worthy.","orc"));
        dialogueListAll.add(new PossibleMessage("Ahoy, matey!","pirate"));
        dialogueListAll.add(new PossibleMessage("You are puny... Not even a morsel for The Great Tiger.","rakshasa"));
        dialogueListAll.add(new PossibleMessage("Soon, ze Empire shall lick our boots!","reiksgard"));
        dialogueListAll.add(new PossibleMessage("Ours is the grandest library in the world!","sarkonid"));
        dialogueListAll.add(new PossibleMessage("Have you heard the legend of Wally the Walrus?","sealsker"));
        dialogueListAll.add(new PossibleMessage("The Empire encroaches far too close to our borders.","shakayana"));
        dialogueListAll.add(new PossibleMessage("Stay out of my yurt!","smingol"));
        dialogueListAll.add(new PossibleMessage("Uuuuuuhhhhh...","undead"));
        dialogueListAll.add(new PossibleMessage("I vant to suck you blood! ...But, I vill control myself.","vampire"));
        dialogueListAll.add(new PossibleMessage("Halt, tresspasser!","vyncan"));
        dialogueListAll.add(new PossibleMessage("This land must be cleansed!","witchbane"));
        dialogueListAll.add(new PossibleMessage("Not now, I am conducting important research!","wizardly"));
        dialogueListAll.add(new PossibleMessage("Blood for the blood gods!","xoltec"));
        dialogueListAll.add(new PossibleMessage("How did you find our Hidden Leaf Village?","zamurai"));
        dialogueListAll.add(new PossibleMessage("These are our hunting grounds.","zimba"));
    }

    // Figures out what message to send the given player, based on a variety of factors.
    public static void speakToPlayer(EntityPlayer player, NpcFaction npc) {
        boolean isFemale = npc.isFemale();
        boolean isHostile = npc.isHostileTowards(player);
        String factionName = npc.getFaction();
        String npcName = npc.getName();
        String proffession = npc.getNpcType();
        System.out.println("Faction: "+factionName);
        System.out.println("Type: "+proffession);
        String message = getRandomDialogue(isFemale, isHostile, factionName, proffession);
        speakToPlayer(player, npcName+": \""+message+"\"");
    }

    // Sends a chat message to the given player.
    public static void speakToPlayer(EntityPlayer player, String message) {
        player.sendMessage(new TextComponentString(message));
    }

    // Searches through all possible dialogue options and picks a valid one
    private static String getRandomDialogue(boolean isFemale, boolean isHostile, String factionName, String proffession){
        // Iterate over dialogueListAll, and compile a new list of only valid dialogue options:
        ArrayList<PossibleMessage> validDialogueOptions = new ArrayList<>();
        for(PossibleMessage msg : dialogueListAll) {
            if(msg.inCombatOnly != isHostile) continue;
            if(msg.femaleOnly && !isFemale) continue;
            if(msg.validFactions.contains(factionName) && msg.validProfessions.contains(proffession)) {
                validDialogueOptions.add(msg);
            }
        }
        if(validDialogueOptions.size() < 1) {
            return "I have no valid dialogue! Please notify lumberjacksparrow in the AncientWarfare discord.";
        }
        // Now pick a random valid option:
        PossibleMessage selectedDialogue = validDialogueOptions.get(new Random().nextInt(validDialogueOptions.size()));
        return selectedDialogue.message;
    }

    public static class PossibleMessage {
        public String message; // The message that will appear in chat
        public boolean femaleOnly; // Whether this message can only be spoken by female NPCs
        public boolean inCombatOnly; // Whether this message can only be spoken by NPCs who are actively hostile
        public ArrayList<String> validFactions; // A list of factions that are allowed to use this dialogue
        public ArrayList<String> validProfessions; // A list of types of NPCs that are allowed to use this dialogue

        // Default constructor, allows any NPC to say it
        public PossibleMessage(String message) {
            this.message = message;
            this.femaleOnly = false;
            this.inCombatOnly = false;
            this.validFactions = new ArrayList<String>();
            validFactions.add("amazon");
            validFactions.add("barbarian");
            validFactions.add("beast");
            validFactions.add("brigand");
            validFactions.add("buffloka");
            validFactions.add("coven");
            validFactions.add("demon");
            validFactions.add("dwarf");
            validFactions.add("elf");
            validFactions.add("empire");
            validFactions.add("ent");
            validFactions.add("evil");
            validFactions.add("giant");
            validFactions.add("gnome");
            validFactions.add("good");
            validFactions.add("gremlin");
            validFactions.add("guild");
            validFactions.add("hobbit");
            validFactions.add("icelord");
            validFactions.add("ishtari");
            validFactions.add("klown");
            validFactions.add("kong");
            validFactions.add("lizardman");
            validFactions.add("mindflayer");
            validFactions.add("minossian");
            validFactions.add("monster");
            validFactions.add("nogg");
            validFactions.add("norska");
            validFactions.add("orc");
            validFactions.add("pirate");
            validFactions.add("rakshasa");
            validFactions.add("reiksgard");
            validFactions.add("sarkonid");
            validFactions.add("sealsker");
            validFactions.add("shakayana");
            validFactions.add("smingol");
            validFactions.add("undead");
            validFactions.add("vampire");
            validFactions.add("vyncan");
            validFactions.add("witchbane");
            validFactions.add("wizardly");
            validFactions.add("xoltec");
            validFactions.add("zamurai");
            validFactions.add("zimba");
            this.validProfessions = new ArrayList<String>();
            validProfessions.add("archer.elite");
            validProfessions.add("archer");
            validProfessions.add("bard");
            validProfessions.add("cavalry");
            validProfessions.add("civilian.female");
            validProfessions.add("civilian.male");
            validProfessions.add("leader.elite");
            validProfessions.add("leader");
            validProfessions.add("mounted_archer");
            validProfessions.add("priest");
            validProfessions.add("siege_engineer");
            validProfessions.add("soldier.elite");
            validProfessions.add("soldier");
            validProfessions.add("spellcaster");
            validProfessions.add("trader"); // May remove traders in the future
        }

        // Constructor for specific faction
        public PossibleMessage(String message, String faction) {
            this.message = message;
            this.femaleOnly = false;
            this.inCombatOnly = false;
            this.validFactions = new ArrayList<String>();
            validFactions.add(faction);
            this.validProfessions = new ArrayList<String>();
            validProfessions.add("archer.elite");
            validProfessions.add("archer");
            validProfessions.add("bard");
            validProfessions.add("cavalry");
            validProfessions.add("civilian.female");
            validProfessions.add("civilian.male");
            validProfessions.add("leader.elite");
            validProfessions.add("leader");
            validProfessions.add("mounted_archer");
            validProfessions.add("priest");
            validProfessions.add("siege_engineer");
            validProfessions.add("soldier.elite");
            validProfessions.add("soldier");
            validProfessions.add("spellcaster");
            validProfessions.add("trader"); // May remove traders in the future
        }

        // Constructor for specific faction and NPC type
        public PossibleMessage(String message, String faction, String npcType) {
            this.message = message;
            this.femaleOnly = false;
            this.inCombatOnly = false;
            this.validFactions = new ArrayList<String>();
            validFactions.add(faction);
            this.validProfessions = new ArrayList<String>();
            validProfessions.add(npcType);
        }
    }

}
