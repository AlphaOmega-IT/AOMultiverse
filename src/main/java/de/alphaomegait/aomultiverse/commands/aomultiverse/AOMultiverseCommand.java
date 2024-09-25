package de.alphaomegait.aomultiverse.commands.aomultiverse;

import de.alphaomegait.ao18n.i18n.I18n;
import de.alphaomegait.aomultiverse.AOMultiverse;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import de.alphaomegait.aomultiverse.inventories.MultiverseWorldEditorUI;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AOMultiverseCommand extends PlayerCommand {

	private final static String MAPPING_PATH = "commands/aomultiverse-config.yml";
	private final static String ROOT_SECTION = "commands.aomultiverse";
	
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
				MAPPING_PATH
				)
				.mapSection(
					ROOT_SECTION,
					AOMultiverseCommandSection.class
				),
			aoMultiverse.getLogger()
		);

		this.aoMultiverse = aoMultiverse;
		this.worldFactory = new WorldFactory(this.aoMultiverse);
		this.permissionsSection = configManager
			.getMapper(
			MAPPING_PATH
			)
			.mapSection(
				"permissions",
				PermissionsSection.class
			);
	}

	/**
	 * A method that handles player invocation with various actions and permissions.
	 *
	 * @param  player  the player invoking the method
	 * @param  label   the label associated with the invocation
	 * @param  args    an array of strings containing arguments for the invocation
	 */
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
			case CREATE -> {
				if (
					! this.permissionsSection.hasPermission(player, EAOMultiversePermissionNode.AOMULTIVERSE_CREATE_WORLD)
				) {
					this.permissionsSection.sendMissingMessage(player, EAOMultiversePermissionNode.AOMULTIVERSE_CREATE_WORLD);
					return;
				}
				this.create(worldName, this.enumParameterOrElse(args, 2, EAOMultiverseWorldType.class, EAOMultiverseWorldType.DEFAULT), player);
			}
			case DELETE -> {
				if (
					! this.permissionsSection.hasPermission(player, EAOMultiversePermissionNode.AOMULTIVERSE_DELETE_WORLD)
				) {
					this.permissionsSection.sendMissingMessage(player, EAOMultiversePermissionNode.AOMULTIVERSE_DELETE_WORLD);
					return;
				}

				this.delete(worldName, player);
			}
			case EDIT -> {
				if (
					! this.permissionsSection.hasPermission(player, EAOMultiversePermissionNode.AOMULTIVERSE_EDIT)
				) {
					this.permissionsSection.sendMissingMessage(player, EAOMultiversePermissionNode.AOMULTIVERSE_EDIT);
					return;
				}

				new MultiverseWorldEditorUI(
					this.aoMultiverse,
					player,
					worldName
				).get(player).ifPresent(ui -> ui.display(player));
			}
			case FORCE_WORLD -> {
				if (
					! this.permissionsSection.hasPermission(player, EAOMultiversePermissionNode.AOMULTIVERSE_FORCE_WORLD)
				) {
					this.permissionsSection.sendMissingMessage(player, EAOMultiversePermissionNode.AOMULTIVERSE_FORCE_WORLD);
					return;
				}

				this.forceWorld(worldName, this.enumParameterOrElse(args, 2, EAOMultiverseWorldType.class, EAOMultiverseWorldType.DEFAULT), player);
			}
			case LOAD -> {
				if (
					! this.permissionsSection.hasPermission(player, EAOMultiversePermissionNode.AOMULTIVERSE_LOAD_WORLD)
				) {
					this.permissionsSection.sendMissingMessage(player, EAOMultiversePermissionNode.AOMULTIVERSE_LOAD_WORLD);
					return;
				}

				this.loadWorld(worldName, this.enumParameterOrElse(args, 2, EAOMultiverseWorldType.class, EAOMultiverseWorldType.DEFAULT), player);
			}
			case TELEPORT -> {
				if (
					! this.permissionsSection.hasPermission(player, EAOMultiversePermissionNode.AOMULTIVERSE_TELEPORT)
				) {
					this.permissionsSection.sendMissingMessage(player, EAOMultiversePermissionNode.AOMULTIVERSE_TELEPORT);
					return;
				}
				
				CompletableFuture.runAsync(() -> {
					final Optional<MultiverseWorld> multiverseWorld = this.aoMultiverse.getMultiverseWorldDao().findByName(worldName);
					if (
						multiverseWorld.isEmpty()
					) {
						new I18n.Builder(
							"aomultiverse-world_does_not_exist",
							player
						).hasPrefix(true).setArgs(worldName).build().sendMessageAsComponent();
						return;
					}
					
					this.teleport(multiverseWorld.get(), player);
				});
			}
			default -> this.sendHelp(player);
		}
	}

	/**
	 * A description of the entire Java function.
	 *
	 * @param  commandSender	description of parameter
	 * @param  label	description of parameter
	 * @param  args	description of parameter
	 * @return         	description of return value
	 */
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
				"force_world",
				"help",
				"load",
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
				"plot",
				"void"
			)
		);

		if (args.length == 1) {
			return StringUtil.copyPartialMatches(args[0].toLowerCase(), completionsArg1, new ArrayList<>());
		}

		if (args.length == 2) {
			return StringUtil.copyPartialMatches(args[1].toLowerCase(), completionsArg2, new ArrayList<>());
		}

		if (
			args.length == 3 &&
			args[0].equalsIgnoreCase(EAOMultiverseAction.CREATE.name()) ||
			args[0].equalsIgnoreCase(EAOMultiverseAction.FORCE_WORLD.name())
		) {
			return StringUtil.copyPartialMatches(args[2].toLowerCase(), completionsArg3, new ArrayList<>());
		}

		return new ArrayList<>();
	}

	/**
	 * Create a new world.
	 *
	 * @param  worldName  the name of the world to create
	 * @param  worldType  the type of the world to create
	 * @param  player     the player initiating the creation of the world
	 */
	private void create(
		final @NotNull String worldName,
		final @NotNull EAOMultiverseWorldType worldType,
		final @NotNull Player player
	) {
		this.worldFactory.createWorld(
			worldName,
			worldType,
			player,
			false
		);
	}

	/**
	 * A description of the entire Java function.
	 *
	 * @param  worldName	description of parameter
	 * @param  player	    description of parameter
	 */
	private void delete(
		final @NotNull String worldName,
		final @NotNull Player player
	) {
		this.worldFactory.deleteWorld(
			worldName,
			player
		);
	}

	/**
	 * A method to teleport a player to a specified world.
	 *
	 * @param  multiverseWorld  the world to teleport to
	 * @param  player     the player to teleport
	 */
	private void teleport(
		final @NotNull MultiverseWorld multiverseWorld,
		final @NotNull Player player
	) {
		this.worldFactory.teleport(
			multiverseWorld,
			player
		);
	}

	/**
	 * Sends help to the specified player.
	 *
	 * @param  player	the player to send help to
	 */
	private void sendHelp(
		final @NotNull Player player
	) {
		new I18n.Builder(
			"aomultiverse-help",
			player
		).hasPrefix(true)
		 .build().sendMessageAsComponent();
	}

	private void loadWorld(
		final @NotNull String worldName,
		final @NotNull EAOMultiverseWorldType worldType,
		final @NotNull Player player
	) {
		this.forceWorld(
			worldName,
			worldType,
			player
		);
	}

	private void forceWorld(
		final @NotNull String worldName,
		final @NotNull EAOMultiverseWorldType worldType,
		final @NotNull Player player
	) {
		this.worldFactory.createWorld(
			worldName,
			worldType,
		  player,
			true
		);
	}
}