package de.alphaomegait.aomultiverse.commands.aomultiverse;

import de.alphaomegait.ao18n.I18n;
import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.inventories.MultiverseWorldEditorInventory;
import de.alphaomegait.aomultiverse.utilities.WorldFactory;
import me.blvckbytes.bukkitcommands.PlayerCommand;
import me.blvckbytes.bukkitevaluable.ConfigManager;
import me.blvckbytes.bukkitevaluable.section.PermissionsSection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AOMultiverseCommand extends PlayerCommand {

	private final AOMultiverse aoMultiverse;
	private final WorldFactory worldFactory;
	private final PermissionsSection permissionsSection;

	public AOMultiverseCommand(
		final @NotNull AOMultiverse aoMultiverse,
		final @NotNull ConfigManager configManager
		) throws Exception {
		super(
			configManager
				.getMapper(
					"aomultiverse-config.yml"
				)
				.mapSection(
					"commands.aomultiverse",
					AOMultiverseCommandSection.class
				),
			aoMultiverse.getLogger()
		);

		this.aoMultiverse = aoMultiverse;
		this.worldFactory = new WorldFactory(this.aoMultiverse, this.aoMultiverse.getLogger());
		this.permissionsSection = configManager
			.getMapper(
				"aomultiverse-config.yml"
			)
			.mapSection(
				"permissions",
				PermissionsSection.class
			);
	}

	@Override
	protected void onPlayerInvocation(
		final Player player,
		final String label,
		final String[] args
	) {
		if (
			! this.permissionsSection.hasPermission(player, EAOMultiversePermissionNode.AOMULTIVERSE)
		) {
			this.permissionsSection.sendMissingMessage(player, EAOMultiversePermissionNode.AOMULTIVERSE);
			return;
		}

		if (args.length == 0) {
			//TODO OPEN INVENTORY
			return;
		}

		final EAOMultiverseAction action = this.enumParameter(args, 0, EAOMultiverseAction.class);

		if (action.equals(EAOMultiverseAction.HELP)) {
			this.sendHelp(player);
			return;
		}

		final String worldName = this.stringParameter(args, 1);
		switch (action) {
			case CREATE: {
				if (
					! this.permissionsSection.hasPermission(player, EAOMultiversePermissionNode.AOMULTIVERSE_CREATE_WORLD)
				) {
					this.permissionsSection.sendMissingMessage(player, EAOMultiversePermissionNode.AOMULTIVERSE_CREATE_WORLD);
					return;
				}
				this.create(worldName, this.enumParameterOrElse(args, 2, EAOMultiverseWorldType.class, EAOMultiverseWorldType.DEFAULT), player);
				break;
			}
			case DELETE: {
				if (
					! this.permissionsSection.hasPermission(player, EAOMultiversePermissionNode.AOMULTIVERSE_DELETE_WORLD)
				) {
					this.permissionsSection.sendMissingMessage(player, EAOMultiversePermissionNode.AOMULTIVERSE_DELETE_WORLD);
					return;
				}

				this.delete(worldName, player);
				break;
			}
			case EDIT: {
				if (
					! this.permissionsSection.hasPermission(player, EAOMultiversePermissionNode.AOMULTIVERSE_EDIT)
				) {
					this.permissionsSection.sendMissingMessage(player, EAOMultiversePermissionNode.AOMULTIVERSE_EDIT);
					return;
				}

				new MultiverseWorldEditorInventory(
					this.aoMultiverse,
					player,
					worldName
				).getInventory(player).open(player);
				break;
			}
			case TELEPORT: {
				if (
					! this.permissionsSection.hasPermission(player, EAOMultiversePermissionNode.AOMULTIVERSE_TELEPORT)
				) {
					this.permissionsSection.sendMissingMessage(player, EAOMultiversePermissionNode.AOMULTIVERSE_TELEPORT);
					return;
				}

				this.teleport(worldName, player);
				break;
			}
			default: {
				this.sendHelp(player);
			}
		}
	}

	@Override
	protected List<String> onTabComplete(
		final CommandSender commandSender,
		final String label,
		final String[] args
	) {
		List<String> completionsArg1 = new ArrayList<>(
			List.of(
				"create",
				"delete",
				"edit",
				"help",
				"teleport"
			)
		);

		List<String> completionsArg2 = new ArrayList<>(
			args[0].equalsIgnoreCase(EAOMultiverseAction.CREATE.name()) ?
			List.of(
				UUID.randomUUID().toString().substring(0, 8).replace("-", "_") + "_world"
			) :
			Bukkit.getWorlds().stream().map(World::getName).toList()
		);

		List<String> completionsArg3 = new ArrayList<>(
			List.of(
				"default",
				"void"
			)
		);

		if (args.length == 1) {
			return StringUtil.copyPartialMatches(args[0].toLowerCase(), completionsArg1, new ArrayList<>());
		}

		if (args.length == 2) {
			return StringUtil.copyPartialMatches(args[1].toLowerCase(), completionsArg2, new ArrayList<>());
		}

		if (args.length == 3 && args[0].equalsIgnoreCase(EAOMultiverseAction.CREATE.name())) {
			return StringUtil.copyPartialMatches(args[2].toLowerCase(), completionsArg3, new ArrayList<>());
		}

		return new ArrayList<>();
	}

	private void create(
		final @NotNull String worldName,
		final @NotNull EAOMultiverseWorldType worldType,
		final @NotNull Player player
	) {
		this.worldFactory.createWorld(
			worldName,
			worldType,
			player
		);
	}

	private void delete(
		final @NotNull String worldName,
		final @NotNull Player player
	) {
		this.worldFactory.deleteWorld(
			worldName,
			player
		);
	}

	private void teleport(
		final @NotNull String worldName,
		final @NotNull Player player
	) {
		this.worldFactory.teleport(
			worldName,
			player
		);
	}

	private void sendHelp(
		final @NotNull Player player
	) {
		new I18n.Builder(
			"aomultiverse.help",
			player
		).hasPrefix(true)
		 .build().sendMessageAsComponent();
	}
}