package de.devversion.votemap;

public class MapInfo {

	private final String mapname;
	private final String description;

	public MapInfo(final String mapname, final String description) {
		this.mapname = mapname;
		this.description = description;
	}

	public String getMapName() {
		return this.mapname;
	}
	
	public String getDescription() {
		return this.description;
	}
}
