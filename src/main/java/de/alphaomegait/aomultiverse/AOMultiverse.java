package de.alphaomegait.aomultiverse;

import de.alphaomegait.aocore.AOCore;
import de.alphaomegait.aocore.aoinv.AOInventoryEvents;
import de.alphaomegait.aocore.licensing.ELicenseType;
import de.alphaomegait.aomultiverse.api.MultiverseAdapter;
import de.alphaomegait.aomultiverse.commands.aomultiverse.AOMultiverseCommand;
import de.alphaomegait.aomultiverse.commands.spawn.AOSpawnCommand;
import de.alphaomegait.aomultiverse.database.daos.MultiverseWorldDao;
import de.alphaomegait.aomultiverse.listener.OnJoin;
import de.alphaomegait.aomultiverse.listener.OnRespawn;
import de.alphaomegait.aomultiverse.utilities.WorldFactory;
import me.blvckbytes.bukkitevaluable.IConfigPathsProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class AOMultiverse extends JavaPlugin implements IConfigPathsProvider {

	private AOCore aoCore;
	
	private MultiverseWorldDao multiverseWorldDao;
	
	private MultiverseAdapter multiverseAdapter;

	@Override
	public void onLoad() {
		AOCore.generateConfigFiles(this, this.getConfigPaths());
	}

	@Override
	public void onEnable() {
		long beginTimestamp = System.nanoTime();
		this.registerMultiverseAdapter();
		
		this.aoCore = new AOCore(this, ELicenseType.FREE, true);
		this.aoCore.getAutoWirer()
			.addSingleton(AOInventoryEvents.class)
			.addSingleton(AOMultiverseCommand.class)
			.addSingleton(AOSpawnCommand.class)
			.addSingleton(OnRespawn.class)
			.addSingleton(OnJoin.class)
			.addInstantiationListener(
				Listener.class,
				(listener, dependency) -> Bukkit.getPluginManager().registerEvents(listener, this)
			)
			.addInstantiationListener(
				Command.class,
				(command, dependency) -> Bukkit.getCommandMap().register(command.getName(), command)
			)
			.onException(exception -> {
				this.getAoCore().getLogger().logError("An error occurred while setting up the plugin: ", exception);
				this.getServer().getPluginManager().disablePlugin(this);
			})
			.wire(wirer -> {
				this.aoCore.getLogger().logDebug("Successfully loaded " + wirer.getInstancesCount() + " classes (" + ((System.nanoTime() - beginTimestamp) / 1000 / 1000) + "ms)");
				
				this.multiverseWorldDao = new MultiverseWorldDao(this.aoCore);
				new WorldFactory(this).loadExistingWorlds();
			});
	}
	
	@Override
	public void onDisable() {
		this.aoCore.disable();
	}
	
	@Override
	public String[] getConfigPaths() {
		return new String[]{
			"database/database-config.yml",
			"translations/i18n.yml",
			"commands/aomultiverse-config.yml"
		};
	}
	
	public AOCore getAoCore() {
		return this.aoCore;
	}
	
	public MultiverseWorldDao getMultiverseWorldDao() {
		return this.multiverseWorldDao;
	}
	
	public MultiverseAdapter getMultiverseAdapter() {
		return this.multiverseAdapter;
	}
	
	private void registerMultiverseAdapter() {
		this.multiverseAdapter = new MultiverseAdapter(this);
		
		this.getServer().getServicesManager().register(MultiverseAdapter.class, this.multiverseAdapter, this, ServicePriority.Highest);
	}
}