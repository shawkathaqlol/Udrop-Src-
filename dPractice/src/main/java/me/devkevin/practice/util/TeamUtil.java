package me.devkevin.practice.util;

import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.team.KillableTeam;
import me.devkevin.practice.tournament.TournamentTeam;

import java.util.UUID;

public class TeamUtil {
    public static String getNames(final KillableTeam team) {
        String names = "";
        for (int i = 0; i < team.getPlayers().size(); ++i) {
            final UUID teammateUUID = team.getPlayers().get(i);
            final Player teammate = Practice.getInstance().getServer().getPlayer(teammateUUID);
            String name = "";
            if (teammate == null) {
                if (team instanceof TournamentTeam) {
                    name = ((TournamentTeam) team).getPlayerName(teammateUUID);
                }
            } else {
                name = teammate.getName();
            }
            final int players = team.getPlayers().size();
            if (teammate != null) {
                names = names + name + ((players - 1 == i) ? "" : ((players - 2 == i) ? (((players > 2) ? "," : "") + " & ") : ", "));
            }
        }
        return names;
    }
}
