package de.devversion.votemap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.devversion.bfcon.common.CommandInterface;
import de.devversion.bfcon.common.primitives.PlayerSubset;
import de.devversion.bfcon.common.primitives.PlayerSubset.Type;
import de.devversion.bfcon.event.EventHandler;
import de.devversion.bfcon.event.Listener;
import de.devversion.bfcon.event.player.chat.PlayerOnGlobalChatEvent;
import de.devversion.bfcon.event.player.chat.PlayerOnSquadChatEvent;
import de.devversion.bfcon.event.player.chat.PlayerOnTeamChatEvent;
import de.devversion.bfcon.plugin.Plugin;
import de.devversion.bfcon.protocol.ProtocolEvent;

public class VoteMap extends Plugin implements Listener {

	private CommandInterface ci;
	
	private final List<MapInfo> mapinfos = new ArrayList<MapInfo>();
	private final List<ModeInfo> modeinfos = new ArrayList<ModeInfo>();
	
	private final List<String> votedPlayers = new ArrayList<String>();
	private RoundInfo votedRound;
	
	@Override
	public void onDisable() {
		System.out.println("VoteMap successfully disabled");
	}

	@Override
	public void onEnable() {
		this.getBFCON().getEventManager().registerListener(this, ProtocolEvent.onChat);
		this.ci = this.getCommandInterface();
		
		// ALL GAMEMAPS
		mapinfos.add(new MapInfo("MP_Abandoned", "Zavod 331"));
		mapinfos.add(new MapInfo("MP_Damage", "Lancang Dam"));
		mapinfos.add(new MapInfo("MP_Flooded", "Flood Zone"));
		mapinfos.add(new MapInfo("MP_Journey", "Golmud Railway"));
		mapinfos.add(new MapInfo("MP_Naval", "Paracel Storm"));
		mapinfos.add(new MapInfo("MP_Prison", "Operation Locker"));
		mapinfos.add(new MapInfo("MP_Resort", "Hainan Resort"));
		mapinfos.add(new MapInfo("MP_Siege", "Siege of Shanghai"));
		mapinfos.add(new MapInfo("MP_TheDish", "Rogue Transmission"));
		mapinfos.add(new MapInfo("MP_Tremors", "Dawnbreaker"));
		mapinfos.add(new MapInfo("XP1_001", "Silk Road"));
		mapinfos.add(new MapInfo("XP1_002", "Altai Range"));
		mapinfos.add(new MapInfo("XP1_003", "Guilin Peak"));
		mapinfos.add(new MapInfo("XP1_004", "Dragon Pass"));
		mapinfos.add(new MapInfo("XP0_Caspian", "Caspian Border"));
		mapinfos.add(new MapInfo("XP0_Firestorm", "Operation Firestorm"));
		mapinfos.add(new MapInfo("XP0_Metro", "Operation Metro"));
		mapinfos.add(new MapInfo("XP0_Oman", "Gulf of Oman"));
		mapinfos.add(new MapInfo("XP2_001", "Lost Islands"));
		mapinfos.add(new MapInfo("XP2_002", "Nansha Strike"));
		mapinfos.add(new MapInfo("XP2_003", "Wavebreaker"));
		mapinfos.add(new MapInfo("XP2_004", "Operation Mortar"));
		mapinfos.add(new MapInfo("XP3_MarketPl", "Pearl Market"));
		mapinfos.add(new MapInfo("XP3_Prpganda", "Propaganda"));
		mapinfos.add(new MapInfo("XP3_UrbanGdn", "Lumphini Garden"));
		mapinfos.add(new MapInfo("XP3_WtrFront", "Sunken Dragon"));
		mapinfos.add(new MapInfo("XP4_Arctic", "Operation Whiteout"));
		mapinfos.add(new MapInfo("XP4_SubBase", "Hammerhead"));
		mapinfos.add(new MapInfo("XP4_Titan", "Hangar 21"));
		mapinfos.add(new MapInfo("XP4_WlkrFtry", "Giants Of Karelia"));
		// ALL GAMEMODES
		modeinfos.add(new ModeInfo("Chainlink0", "cl", "chainlink"));
		modeinfos.add(new ModeInfo("ConquestLarge0", "cql", "conquestlarge"));
		modeinfos.add(new ModeInfo("ConquestSmall0", "cqs", "conquestsmall"));
		modeinfos.add(new ModeInfo("ConquestAssaultSmall0", "cqas", "conquestassaultsmall", "cqas0"));
		modeinfos.add(new ModeInfo("ConquestAssaultSmall1", "cqas1", "conquestassaultsmall1", "cqas1"));
		modeinfos.add(new ModeInfo("ConquestAssaultLarge0", "cqal", "conquestassaultlarge" ));
		modeinfos.add(new ModeInfo("RushLarge0", "r", "rush", "rushlarge"));
		modeinfos.add(new ModeInfo("SquadRush0", "sr", "sqr", "squadrush"));
		modeinfos.add(new ModeInfo("SquaddeathMatch0", "sqdm", "squaddeathmatch"));
		modeinfos.add(new ModeInfo("TeamDeathMatch0", "tdm", "teamdeathmatch"));
		modeinfos.add(new ModeInfo("Domination0", "dom", "domination"));
		modeinfos.add(new ModeInfo("TeamDeathMatchC0", "tdmcq", "teamdeathmatchconquest"));
		modeinfos.add(new ModeInfo("Elimination0", "df", "elimination"));
		modeinfos.add(new ModeInfo("Scavenger0", "sc", "scavenger"));
		modeinfos.add(new ModeInfo("CaptureTheFlag0", "ctf", "capturetheflag"));
		modeinfos.add(new ModeInfo("AirSuperiority0", "as", "airsuperiority"));
		modeinfos.add(new ModeInfo("Obliteration", "ob", "obliteration"));
		modeinfos.add(new ModeInfo("GunMaster0", "gm", "gunmaster"));
		modeinfos.add(new ModeInfo("CarrierAssaultLarge0", "cal", "carrierassaultlarge"));
		modeinfos.add(new ModeInfo("CarrierAssaultSmall0", "cas", "carrierassaultsmall"));
		
		System.out.println("VoteMap successfully enabled.");
	}
	
	@EventHandler
	public void onGlobalChat(final PlayerOnGlobalChatEvent e) {
		processChatMessage(e.getMessage(), e.getSpeaker());
	}
	
	@EventHandler
	public void onTeamChat(final PlayerOnTeamChatEvent e) {
		processChatMessage(e.getMessage(), e.getSpeaker());
	}
	
	@EventHandler
	public void onSquadChat(final PlayerOnSquadChatEvent e) {
		processChatMessage(e.getMessage(), e.getSpeaker());
	}
	
	private void processChatMessage(final String message, final String speaker) {
		if (message.matches("[#!/](votemap).*")) {
			final String[] arguments = message.split(" ");
			if (arguments.length != 3) {
				if (arguments.length == 2 && arguments[1].toLowerCase().equals("about")) {
					ci.getAdmin().sayPlayer("(c) BFCON - (c) JCON - (c) " + getDescription().getName() + "(" + getDescription().getVersion() + ")", speaker);
					ci.getAdmin().sayPlayer("Developed by DevVersion (Die_Kampfgurke)", speaker);
					return;
				}
				
				if (votedRound != null) {
					ci.getAdmin().sayPlayer("Currently Voting: " + votedRound.getGameMap().getDescription(), speaker);
					ci.getAdmin().sayPlayer("Type /yes in chat to vote for it", speaker);
				} else {
					ci.getAdmin().sayPlayer("Usage: /votemap [map] [mode]", speaker);
					ci.getAdmin().sayPlayer("Example: /votemap shanghai cql", speaker);
				}
			} else {
				final MapInfo mapinfo = stringToMapInfo(arguments[1]);
				final ModeInfo modeinfo = stringToModeInfo(arguments[2]);
				
				if (mapinfo == null) {
					ci.getAdmin().sayPlayer("The entered map couldnt found.", speaker);
					return;
				} else if (modeinfo == null) {
					ci.getAdmin().sayPlayer("The entered GameMode couldnt found", speaker);
					return;
				}
				
				//TODO PRIVILIGES IN BFCON - TEMPORARY FIX
				if (speaker.equals("Die_Kampfgurke") || speaker.equals("RandyXpp")) {
					ci.getAdmin().sayPlayer("Map instantly changed because of the privileges.", speaker);
					changeMap(mapinfo, modeinfo, 2);
				} else {
					if (votedRound != null) {
						ci.getAdmin().sayPlayer("There is already a voting progress running!", speaker);
						return;
					} else if (ci.getAdmin().listPlayers(new PlayerSubset(Type.ALL)).size() == 1) {
						ci.getAdmin().sayPlayer("Map instantly changed because of no other players.", speaker);
						changeMap(mapinfo, modeinfo, 2);
						return;
					}
					
					votedRound = new RoundInfo(2, mapinfo, modeinfo);
					votedPlayers.add(speaker);
					
					ci.getAdmin().sayGlobal("######## VOTE ########");
					ci.getAdmin().sayGlobal("Voting started for " + mapinfo.getDescription() + " [" + modeinfo.getDescription()[0].toUpperCase() + "]");
					ci.getAdmin().sayGlobal("Type /yes to vote for it!");
					ci.getAdmin().sayGlobal("VoteMap started by " + speaker);
					ci.getAdmin().sayGlobal("####################");
					
					new Thread(() -> {
						try {
							Thread.sleep(60000L);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
						onVotingEnded();
					}).start();
				}
			}
		}
	}
	
	private void onVotingEnded() {
		boolean success = false;
		final int playercount = ci.getAdmin().listPlayers(new PlayerSubset(Type.ALL)).size();
		if (playercount > 2 && votedPlayers.size() > playercount * (2 / 3)) success = true;
		else if (playercount == 2) success = true;
		
		ci.getAdmin().sayGlobal("######## VOTE ########");
		ci.getAdmin().sayGlobal("The voting is over! " + votedPlayers.size() + "/" + playercount);
		if (success) {
			ci.getAdmin().sayGlobal("Most players voted for " + votedRound.getGameMap().getDescription());
			changeMap(votedRound.getGameMap(), votedRound.getGameMode(), votedRound.getRounds());
		} else {
			ci.getAdmin().sayGlobal("Not enough players voted with yes");
			ci.getAdmin().sayGlobal("Current map will stay");
		}
		ci.getAdmin().sayGlobal("####################");
		
		votedPlayers.clear();
		votedRound = null;
	}

	private MapInfo stringToMapInfo(final String mapname) {
		final Optional<MapInfo> filtered = mapinfos.stream().filter(info -> info.getDescription().toLowerCase().contains(mapname.toLowerCase())).findFirst();
		return filtered.isPresent() ? filtered.get() : null;
	}
	
	private ModeInfo stringToModeInfo(final String modename) {
		final Optional<ModeInfo> filtered = modeinfos.stream().filter(info -> {
			for (final String x : info.getDescription()) if (x.contains(modename.toLowerCase())) return true;
			return false;
		}).findFirst();
		return filtered.isPresent() ? filtered.get() : null;
	}

	private void changeMap(final MapInfo mapinfo, final ModeInfo modeinfo, final int rounds) {
		ci.getMapList().clear();
		ci.getMapList().add(mapinfo.getMapName(), modeinfo.getModeName(), rounds, 0);
		ci.getMapList().runNextRound();
	}
}
