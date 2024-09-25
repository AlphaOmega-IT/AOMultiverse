package de.alphaomegait.aomultiverse.commands.spawn;

import de.alphaomegait.ao18n.i18n.I18n;
import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import me.blvckbytes.bukkitcommands.PlayerCommand;
import me.blvckbytes.bukkitevaluable.ConfigManager;
import me.blvckbytes.bukkitevaluable.section.PermissionsSection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Represents the command for handling player spawn-related actions.
 */
public class AOSpawnCommand extends PlayerCommand {

    private static final String MAPPING_PATH = "commands/aomultiverse-config.yml";
    private static final String ROOT_SECTION = "commands.spawn";

    private final AOMultiverse aoMultiverse;
    private final PermissionsSection permissionsSection;
	
	/**
	 * Constructs the AOSpawnCommand object.
	 *
	 * @param aoMultiverse   The AOMultiverse instance.
	 * @param configManager  The ConfigManager instance.
	 * @throws Exception if an error occurs during initialization.
	 */
    public AOSpawnCommand(
            @NotNull AOMultiverse aoMultiverse,
            @NotNull ConfigManager configManager
    ) throws Exception {
        super(configManager.getMapper(MAPPING_PATH).mapSection(ROOT_SECTION, AOSpawnCommandSection.class), aoMultiverse.getLogger());
        this.aoMultiverse = aoMultiverse;
        this.permissionsSection = configManager.getMapper(MAPPING_PATH).mapSection("permissions", PermissionsSection.class);
    }

    @Override
    protected void onPlayerInvocation(Player player, String label, String[] args) {
        if (!permissionsSection.hasPermission(player, EAOSpawnPermissionNode.SPAWN)) {
            permissionsSection.sendMissingMessage(player, EAOSpawnPermissionNode.SPAWN);
            return;
        }

        new I18n.Builder("aospawn-spawn_will_be_located", player).hasPrefix(true).build().sendMessageAsComponent();
	    
	    CompletableFuture.runAsync(() -> {
		    this.aoMultiverse.getMultiverseWorldDao().findAll().stream().filter(MultiverseWorld::isHasGlobalSpawn).findFirst().ifPresentOrElse(
			    multiverseWorld -> player.teleportAsync(multiverseWorld.getSpawnLocation()),
			    () -> this.aoMultiverse.getMultiverseWorldDao().findByName(player.getWorld().getName()).ifPresent(
				    multiverseWorld -> player.teleportAsync(multiverseWorld.getSpawnLocation())
			    )
		    );
	    });
    }

    @Override
    protected List<String> onTabComplete(CommandSender commandSender, String label, String[] args) {
        return new ArrayList<>();
    }
}