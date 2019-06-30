package me.devkevin.practice.runnable;

import me.devkevin.practice.Practice;
import me.devkevin.practice.player.PlayerData;

public class SaveDataRunnable implements Runnable {
    private final Practice plugin;

    public SaveDataRunnable() {
        this.plugin = Practice.getInstance();
    }

    @Override
    public void run() {
        for (final PlayerData playerData : this.plugin.getPlayerManager().getAllData()) {
            this.plugin.getPlayerManager().saveData(playerData);
        }
    }
}
