package net.shadowmage.ancientwarfare.npc.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;

public class NpcRandomNames {
    private List<String> names = new ArrayList<>();
    private final Random rand = new Random();
    
    public void loadNames() {
	    InputStream is = getClass().getResourceAsStream("/assets/ancientwarfarenpc/npc_names_list.txt");
	    if (is == null) {
	        System.out.println("npc_names_list.txt not found");
	        return;
	    }
	    try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            names.add(line);
	        }
	    } catch (IOException e) {
	        System.out.println("Exception while loading names: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
    
	public String getRandomName() {
	    if (names.isEmpty()) {
	        System.out.println("Names not loaded. size:" + names.size()); //Debug print
	        return "Human";
	    } else {
	        return names.get(rand.nextInt(names.size()));
	    }
	}
}
