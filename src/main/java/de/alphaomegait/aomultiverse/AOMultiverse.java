package de.alphaomegait.aomultiverse;

import de.alphaomegait.ao18n.AO18n;
import de.alphaomegait.aomultiverse.commands.aomultiverse.AOMultiverseCommand;
import de.alphaomegait.aomultiverse.commands.spawn.AOSpawnCommand;
import de.alphaomegait.aomultiverse.utilities.WorldFactory;
import de.alphaomegait.woocore.WooCore;
import de.alphaomegait.woocore.dependencies.LibraryLoader;
import de.alphaomegait.woocore.enums.GPADependency;
import de.alphaomegait.woocore.enums.LicenseType;
import de.alphaomegait.woocore.wooinv.InventoryFactory;
import me.blvckbytes.autowirer.AutoWirer;
import me.blvckbytes.bukkitboilerplate.PluginFileHandler;
import me.blvckbytes.bukkitevaluable.ConfigManager;
import me.blvckbytes.bukkitevaluable.IConfigPathsProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AOMultiverse extends JavaPlugin implements IConfigPathsProvider {

	private WooCore wooCore;

	private final Logger logger = Logger.getLogger("AOMultiverse");

	private InventoryFactory inventoryFactory;

	@Override
	public void onLoad() {

		if (
			this.getDataFolder().mkdir()
		) this.logger.info("Plugin folder got created.");

		List<LibraryLoader.Dependency> dependencies = new ArrayList<>();
		dependencies.addAll(GPADependency.HIBERNATE_ORM.getDependencies());
		dependencies.addAll(GPADependency.MYSQL.getDependencies());

		Arrays.stream(this.getConfigPaths()).toList().forEach(
			configPath -> {
				this.saveResource(
					configPath,
					false
				);
				this.logger.info("Config file created: " + configPath);
			});

		this.wooCore = new WooCore(
			this,
			LicenseType.FREE,
			dependencies,
			false
		);

		new AO18n(this, false);
		this.logger.info("AOMultiverse has been loaded!");
	}

	@Override
	public void onEnable() {
		final long beginTimestamp = System.nanoTime();

		AutoWirer autoWirer = new AutoWirer();
		autoWirer
			.addExistingSingleton(this)
			.addExistingSingleton(this.logger)
			.addSingleton(ConfigManager.class)
			.addSingleton(PluginFileHandler.class)
			.addSingleton(AOMultiverseCommand.class)
			.addSingleton(AOSpawnCommand.class)
			.addInstantiationListener(
				Command.class,
				(command, dependencies) -> {
					Bukkit.getCommandMap()
								.register(
									command.getName(),
									command
								);
				}
			)
			.onException(exception -> {
				this.logger.log(
					Level.SEVERE,
					"An exception occurred while loading the plugin: " + exception,
					exception
				);
				this.getServer().getScheduler().cancelTasks(this);
				this.getServer().getPluginManager().disablePlugin(this);
				this.onDisable();
			})
			.wire(wirer -> {
				this.logger.info(
					"Successfully loaded " + wirer.getInstancesCount() + " classes (" + ((System.nanoTime() - beginTimestamp) / 1000 / 1000) + "ms)"
				);
			});

		this.inventoryFactory = new InventoryFactory(this);

		new WorldFactory(this, this.logger).loadExistingWorlds();

		this.logger.info("AOMultiverse has been enabled!");
	}

	@Override
	public void onDisable() {
		this.logger.info("AOMultiverse has been disabled!");
	}

	@Override
	public String[] getConfigPaths() {
		return new String[] {
			"database-config.yml",
			"aomultiverse-config.yml",
			"utilities/teleport-config.yml"
		};
	}

	public WooCore getWooCore() {
		return this.wooCore;
	}

	@NotNull
	@Override
	public Logger getLogger() {
		return this.logger;
	}

	public InventoryFactory getInventoryFactory() {
		return this.inventoryFactory;
	}
}