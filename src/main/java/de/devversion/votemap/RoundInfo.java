package de.devversion.votemap;

public class RoundInfo {
	
	private final int rounds;
	private final MapInfo gamemap;
	private final ModeInfo gamemode;
	
	public RoundInfo(final int rounds, final MapInfo gamemap, final ModeInfo gamemode) {
		this.rounds = rounds;
		this.gamemap = gamemap;
		this.gamemode = gamemode;
	}
	
	public int getRounds() {
		return this.rounds;
	}
	
	public MapInfo getGameMap() {
		return this.gamemap;
	}

	public ModeInfo getGameMode() {
		return this.gamemode;
	}
}
