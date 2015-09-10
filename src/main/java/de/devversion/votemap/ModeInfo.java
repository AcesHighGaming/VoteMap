package de.devversion.votemap;

public class ModeInfo {

	private final String modename;
	private final String[] description;
	
	public ModeInfo(final String modename, final String... description) {
		this.modename = modename;
		this.description = description;
	}
	
	public String getModeName() {
		return this.modename;
	}
	
	public String[] getDescription() {
		return this.description;
	}
}
