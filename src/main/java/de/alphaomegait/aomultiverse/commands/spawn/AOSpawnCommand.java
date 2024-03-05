package de.alphaomegait.aomultiverse.commands.spawn;

import de.alphaomegait.ao18n.I18n;
import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.daos.MultiverseWorldDao;
import de.alphaomegait.woocore.utilities.TeleportFactory;
import me.blvckbytes.bukkitcommands.PlayerCommand;
import me.blvckbytes.bukkitevaluable.ConfigManager;
import me.blvckbytes.bukkitevaluable.section.PermissionsSection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AOSpawnCommand extends PlayerCommand {

	private final AOMultiverse aoMultiverse;
	private final MultiverseWorldDao multiverseWorldDao;
	private final ConfigManager configManager;
	private final PermissionsSection permissionsSection;

	public AOSpawnCommand(
		final @NotNull AOMultiverse aoMultiverse,
		final @NotNull ConfigManager configManager
	) throws Exception {
		super(
			configManager
				.getMapper(
					"aomultiverse-config.yml"
				)
				.mapSection(
					"commands.spawn",
					AOSpawnCommandSection.class
				),
			aoMultiverse.getLogger()
		);
		this.aoMultiverse = aoMultiverse;
		this.permissionsSection = configManager
			.getMapper(
				"aomultiverse-config.yml"
			)
			.mapSection(
				"permissions",
				PermissionsSection.class
			);
		this.configManager = configManager;
		this.multiverseWorldDao = new MultiverseWorldDao(this.aoMultiverse);
	}

	@Override
	protected void onPlayerInvocation(
		final Player player,
		final String label,
		final String[] args
	) {
		if (
			! this.permissionsSection.hasPermission(player, EAOSpawnPermissionNode.SPAWN)
		) {
			this.permissionsSection.sendMissingMessage(player, EAOSpawnPermissionNode.SPAWN);
			return;
		}

		new I18n.Builder(
			"aospawn.spawn-will-be-located",
			player
		).hasPrefix(true).build().sendMessageAsComponent();

		CompletableFuture.supplyAsync(() -> {
			return this.multiverseWorldDao.getGlobalSpawn().orElse(this.multiverseWorldDao.findByName(player.getWorld().getName()).orElse(null));
		}).whenCompleteAsync(((multiverseWorld, throwable) -> {
			if (throwable != null)
				return;

			if (multiverseWorld == null) {
				new I18n.Builder(
					"aospawn.spawn-not-found",
					player
				).hasPrefix(true).build().sendMessageAsComponent();
				return;
			}

			new TeleportFactory(
				this.aoMultiverse,
				this.configManager
			).teleport(
				player,
				multiverseWorld.getSpawnLocation(),
				"aospawn.teleporting-to-spawn"
			);
		}));
	}

	@Override
	protected List<String> onTabComplete(
		final CommandSender commandSender,
		final String label,
		final String[] args
	) {
		return new ArrayList<>();
	}
}