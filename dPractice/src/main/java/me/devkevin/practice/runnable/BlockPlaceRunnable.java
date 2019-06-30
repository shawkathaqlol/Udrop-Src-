package me.devkevin.practice.runnable;

import com.boydti.fawe.util.EditSessionBuilder;
import com.boydti.fawe.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class BlockPlaceRunnable extends BukkitRunnable {
    private final ConcurrentMap<Location, Block> blocks;
    private final int totalBlocks;
    private final Iterator<Location> iterator;
    private World world;
    private int blockIndex;
    private int blocksPlaced;
    private boolean completed;

    public BlockPlaceRunnable(final World world, final Map<Location, Block> blocks) {
        this.blockIndex = 0;
        this.blocksPlaced = 0;
        this.completed = false;
        this.world = world;
        (this.blocks = new ConcurrentHashMap<Location, Block>()).putAll(blocks);
        this.totalBlocks = blocks.keySet().size();
        this.iterator = blocks.keySet().iterator();
    }

    public void run() {
        if (this.blocks.isEmpty() || !this.iterator.hasNext()) {
            this.finish();
            this.completed = true;
            this.cancel();
            return;
        }
        TaskManager.IMP.async(() -> {
            EditSession editSession = new EditSessionBuilder(this.world.getName()).fastmode(true).allowedRegionsEverywhere().autoQueue(false).limitUnlimited().build();
            for (Map.Entry entry : this.blocks.entrySet()) {
                try {
                    editSession.setBlock(new Vector((double)((Location)entry.getKey()).getBlockX(), (double)((Location)entry.getKey()).getBlockY(), ((Location)entry.getKey()).getZ()), new BaseBlock(((Block)entry.getValue()).getTypeId(), (int)((Block)entry.getValue()).getData()));
                }
                catch (Exception exception) {}
            }
            editSession.flushQueue();
            TaskManager.IMP.task(this.blocks::clear);
        });
    }

    public abstract void finish();

    public World getWorld() {
        return this.world;
    }

    public ConcurrentMap<Location, Block> getBlocks() {
        return this.blocks;
    }

    public int getTotalBlocks() {
        return this.totalBlocks;
    }

    public Iterator<Location> getIterator() {
        return this.iterator;
    }

    public int getBlockIndex() {
        return this.blockIndex;
    }

    public int getBlocksPlaced() {
        return this.blocksPlaced;
    }

    public boolean isCompleted() {
        return this.completed;
    }
}
