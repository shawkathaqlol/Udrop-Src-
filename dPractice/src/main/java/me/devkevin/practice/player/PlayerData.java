package me.devkevin.practice.player;

import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.kit.PlayerKit;
import me.devkevin.practice.settings.ProfileOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    public static final int DEFAULT_ELO = 1000;
    private final Map<String, Map<Integer, PlayerKit>> playerKits;
    private final Map<String, Integer> rankedLosses;
    private final Map<String, Integer> rankedWins;
    private final Map<String, Integer> rankedElo;
    private final Map<String, Integer> partyElo;
    private final UUID uniqueId;
    private PlayerState playerState;
    private UUID currentMatchID;
    private UUID duelSelecting;
    private ProfileOptions options;
    private int eloRange;
    private int pingRange;
    private int teamID;
    private int rematchID;
    private int missedPots;
    private int longestCombo;
    private int combo;
    private int hits;
    private int oitcEventKills;
    private int oitcEventDeaths;
    private int oitcEventWins;
    private int oitcEventLosses;
    private int sumoEventWins;
    private int sumoEventLosses;
    private int parkourEventWins;
    private int parkourEventLosses;
    private int redroverEventWins;
    private int redroverEventLosses;

    public PlayerData(final UUID uniqueId) {
        this.playerKits = new HashMap<>();
        this.rankedLosses = new HashMap<>();
        this.rankedWins = new HashMap<>();
        this.rankedElo = new HashMap<>();
        this.partyElo = new HashMap<>();
        this.playerState = PlayerState.LOADING;
        this.options = new ProfileOptions();
        this.eloRange = 250;
        this.pingRange = 50;
        this.teamID = -1;
        this.rematchID = -1;
        this.uniqueId = uniqueId;
    }

    public int getWins(final String kitName) {
        return this.rankedWins.computeIfAbsent(kitName, k -> 0);
    }

    public void setWins(final String kitName, final int wins) {
        this.rankedWins.put(kitName, wins);
    }

    public int getLosses(final String kitName) {
        return this.rankedLosses.computeIfAbsent(kitName, k -> 0);
    }

    public void setLosses(final String kitName, final int losses) {
        this.rankedLosses.put(kitName, losses);
    }

    public int getElo(final String kitName) {
        return this.rankedElo.computeIfAbsent(kitName, k -> 1000);
    }

    public void setElo(final String kitName, final int elo) {
        this.rankedElo.put(kitName, elo);
    }

    public int getPartyElo(final String kitName) {
        return this.partyElo.computeIfAbsent(kitName, k -> 1000);
    }

    public void setPartyElo(final String kitName, final int elo) {
        this.partyElo.put(kitName, elo);
    }

    public void addPlayerKit(final int index, final PlayerKit playerKit) {
        this.getPlayerKits(playerKit.getName()).put(index, playerKit);
    }

    public Map<Integer, PlayerKit> getPlayerKits(final String kitName) {
        return this.playerKits.computeIfAbsent(kitName, k -> new HashMap<>());
    }

    public int getGlobalStats(final String type) {
        int i = 0;
        int count = 0;
        for (final Kit kit : Practice.getInstance().getKitManager().getKits()) {
            final String upperCase = type.toUpperCase();
            int n = -1;
            switch (upperCase.hashCode()) {
                case 68744:
                    if (upperCase.equals("ELO")) {
                        n = 0;
                        break;
                    }
                    break;
                case 2664471:
                    if (upperCase.equals("WINS")) {
                        n = 1;
                        break;
                    }
                    break;
                case -2043639023:
                    if (upperCase.equals("LOSSES")) {
                        n = 2;
                        break;
                    }
                    break;
            }
            switch (n) {
                case 0:
                    i += this.getElo(kit.getName());
                    break;
                case 1:
                    i += this.getWins(kit.getName());
                    break;
                case 2:
                    i += this.getLosses(kit.getName());
                    break;
            }
            ++count;
        }
        if (i == 0) {
            i = 1;
        }
        if (count == 0) {
            count = 1;
        }
        return type.toUpperCase().equalsIgnoreCase("ELO") ? Math.round(i / count) : i;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public PlayerState getPlayerState() {
        return this.playerState;
    }

    public void setPlayerState(final PlayerState playerState) {
        this.playerState = playerState;
    }

    public UUID getCurrentMatchID() {
        return this.currentMatchID;
    }

    public void setCurrentMatchID(final UUID currentMatchID) {
        this.currentMatchID = currentMatchID;
    }

    public UUID getDuelSelecting() {
        return this.duelSelecting;
    }

    public void setDuelSelecting(final UUID duelSelecting) {
        this.duelSelecting = duelSelecting;
    }

    public ProfileOptions getOptions() {
        return this.options;
    }

    public void setOptions(final ProfileOptions options) {
        this.options = options;
    }

    public int getEloRange() {
        return this.eloRange;
    }

    public void setEloRange(final int eloRange) {
        this.eloRange = eloRange;
    }

    public int getPingRange() {
        return this.pingRange;
    }

    public void setPingRange(final int pingRange) {
        this.pingRange = pingRange;
    }

    public int getTeamID() {
        return this.teamID;
    }

    public void setTeamID(final int teamID) {
        this.teamID = teamID;
    }

    public int getRematchID() {
        return this.rematchID;
    }

    public void setRematchID(final int rematchID) {
        this.rematchID = rematchID;
    }

    public int getMissedPots() {
        return this.missedPots;
    }

    public void setMissedPots(final int missedPots) {
        this.missedPots = missedPots;
    }

    public int getLongestCombo() {
        return this.longestCombo;
    }

    public void setLongestCombo(final int longestCombo) {
        this.longestCombo = longestCombo;
    }

    public int getCombo() {
        return this.combo;
    }

    public void setCombo(final int combo) {
        this.combo = combo;
    }

    public int getHits() {
        return this.hits;
    }

    public void setHits(final int hits) {
        this.hits = hits;
    }

    public int getOitcEventKills() {
        return this.oitcEventKills;
    }

    public void setOitcEventKills(final int oitcEventKills) {
        this.oitcEventKills = oitcEventKills;
    }

    public int getOitcEventDeaths() {
        return this.oitcEventDeaths;
    }

    public void setOitcEventDeaths(final int oitcEventDeaths) {
        this.oitcEventDeaths = oitcEventDeaths;
    }

    public int getOitcEventWins() {
        return this.oitcEventWins;
    }

    public void setOitcEventWins(final int oitcEventWins) {
        this.oitcEventWins = oitcEventWins;
    }

    public int getOitcEventLosses() {
        return this.oitcEventLosses;
    }

    public void setOitcEventLosses(final int oitcEventLosses) {
        this.oitcEventLosses = oitcEventLosses;
    }

    public int getSumoEventWins() {
        return this.sumoEventWins;
    }

    public void setSumoEventWins(final int sumoEventWins) {
        this.sumoEventWins = sumoEventWins;
    }

    public int getSumoEventLosses() {
        return this.sumoEventLosses;
    }

    public void setSumoEventLosses(final int sumoEventLosses) {
        this.sumoEventLosses = sumoEventLosses;
    }

    public int getParkourEventWins() {
        return this.parkourEventWins;
    }

    public void setParkourEventWins(final int parkourEventWins) {
        this.parkourEventWins = parkourEventWins;
    }

    public int getParkourEventLosses() {
        return this.parkourEventLosses;
    }

    public void setParkourEventLosses(final int parkourEventLosses) {
        this.parkourEventLosses = parkourEventLosses;
    }

    public int getRedroverEventWins() {
        return this.redroverEventWins;
    }

    public void setRedroverEventWins(final int redroverEventWins) {
        this.redroverEventWins = redroverEventWins;
    }

    public int getRedroverEventLosses() {
        return this.redroverEventLosses;
    }

    public void setRedroverEventLosses(final int redroverEventLosses) {
        this.redroverEventLosses = redroverEventLosses;
    }
}
