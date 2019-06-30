package me.devkevin.practice.player;

import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OfflinePlayerData
{
    private final UUID uniqueId;
    private final Map<String, Integer> rankedLosses;
    private final Map<String, Integer> rankedWins;
    private final Map<String, Integer> rankedElo;
    
    public OfflinePlayerData(final UUID uniqueId) {
        this.rankedLosses = new HashMap<String, Integer>();
        this.rankedWins = new HashMap<String, Integer>();
        this.rankedElo = new HashMap<String, Integer>();
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
    
    public int getGlobalStats(final String type) {
        int i = 0;
        int count = 0;
        for (final Kit kit : Practice.getInstance().getKitManager().getKits()) {
            final String upperCase = type.toUpperCase();
            switch (upperCase) {
                case "ELO": {
                    i += this.getElo(kit.getName());
                    break;
                }
                case "WINS": {
                    i += this.getWins(kit.getName());
                    break;
                }
                case "LOSSES": {
                    i += this.getLosses(kit.getName());
                    break;
                }
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
}
