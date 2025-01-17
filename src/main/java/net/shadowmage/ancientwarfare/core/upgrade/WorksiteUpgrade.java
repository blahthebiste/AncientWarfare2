package net.shadowmage.ancientwarfare.core.upgrade;

import java.util.HashSet;
import java.util.Set;

public enum WorksiteUpgrade {
	/*
	 * DO NOT EVER CHANGE ENUM ORDERING, WILL FUBAR LOAD/SAVE VALUES FOR ALL WORKSITES, AS THEY ARE RESTORED VIA ORDINAL
	 */
	SIZE_MEDIUM(new int[] {}, new int[] {}), SIZE_LARGE(new int[] {0}, new int[] {0}), QUARRY_MEDIUM(new int[] {}, new int[] {}),
	QUARRY_LARGE(new int[] {2}, new int[] {2}), ENCHANTED_TOOLS_1(new int[] {}, new int[] {}), ENCHANTED_TOOLS_2(new int[] {4}, new int[] {4}),
	TOOL_QUALITY_1(new int[] {}, new int[] {}), TOOL_QUALITY_2(new int[] {6}, new int[] {6}), TOOL_QUALITY_3(new int[] {6, 7}, new int[] {6, 7}),
	BASIC_CHUNK_LOADER(new int[] {}, new int[] {}), QUARRY_CHUNK_LOADER(new int[] {}, new int[] {});

	private Set<Integer> exclusive;
	private Set<Integer> overrides;

	private WorksiteUpgrade(int[] exc, int[] ovr) {
		this.exclusive = new HashSet<>(exc.length);
		this.overrides = new HashSet<>(ovr.length);
		for (int anExc : exc) {
			exclusive.add(anExc);
		}
		for (int anOvr : ovr) {
			overrides.add(anOvr);
		}
	}

	/*
	 * return true if THIS should override INPUT (input will be removed, this will be applied)
	 */
	public boolean overrides(WorksiteUpgrade ug) {
		return overrides.contains(ug.ordinal());
	}

	/*
	 * return true if THIS prevents the INPUT from being applied (input will not be applied, no change to upgrades)
	 */
	public boolean exclusive(WorksiteUpgrade ug) {
		return exclusive.contains(ug.ordinal());
	}
}
