package de.alphaomegait.aomultiverse.listener;

import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for player respawn events and sets the respawn location based on the configured worlds.
 */
public class OnRespawn implements Listener {

    private final AOMultiverse aoMultiverse;
	
	/**
	 * Constructs an OnRespawn object with the specified AOMultiverse instance.
	 * @param aoMultiverse The AOMultiverse instance to associate with.
	 */
    public OnRespawn(final @NotNull AOMultiverse aoMultiverse) {
        this.aoMultiverse = aoMultiverse;
    }
	
	/**
	 * Handles the player respawn event by setting the respawn location.
	 * If a global spawn is set, it takes precedence; otherwise, it uses the world's spawn location.
	 * @param event The PlayerRespawnEvent to handle.
	 */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(final @NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        for (MultiverseWorld world : this.aoMultiverse.getMultiverseWorlds().values()) {
            if (world.isHasGlobalSpawn()) {
                event.setRespawnLocation(world.getSpawnLocation());
	            this.aoMultiverse.getAoCore().getLogger().logDebug("Setting respawn location for player " + player.getName() + " to " + world.getSpawnLocation());
                return;
            }
        }

        MultiverseWorld multiverseWorld = this.aoMultiverse.getMultiverseWorlds().get(player.getWorld().getName());
        if (multiverseWorld != null) {
            event.setRespawnLocation(multiverseWorld.getSpawnLocation());
	        this.aoMultiverse.getAoCore().getLogger().logDebug("Setting respawn location for player " + player.getName() + " to " + multiverseWorld.getSpawnLocation());
        }
    }
}