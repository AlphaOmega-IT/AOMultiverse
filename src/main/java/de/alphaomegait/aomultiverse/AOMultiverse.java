package de.alphaomegait.aomultiverse;

import de.alphaomegait.aocore.AOCore;
import de.alphaomegait.aocore.aoinv.AOInventoryEvents;
import de.alphaomegait.aocore.licensing.ELicenseType;
import de.alphaomegait.aomultiverse.api.MultiverseAdapter;
import de.alphaomegait.aomultiverse.commands.aomultiverse.AOMultiverseCommand;
import de.alphaomegait.aomultiverse.commands.spawn.AOSpawnCommand;
import de.alphaomegait.aomultiverse.database.daos.MultiverseWorldDao;
import de.alphaomegait.aomultiverse.database.entities.MultiverseWorld;
import de.alphaomegait.aomultiverse.listener.OnJoin;
import de.alphaomegait.aomultiverse.listener.OnRespawn;
import de.alphaomegait.aomultiverse.utilities.WorldFactory;
import me.blvckbytes.bukkitevaluable.IConfigPathsProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AOMultiverse extends JavaPlugin implements IConfigPathsProvider {

	private AOCore aoCore;
	
	private MultiverseWorldDao multiverseWorldDao;
	
	private MultiverseAdapter multiverseAdapter;
	
	private Map<String, MultiverseWorld> multiverseWorlds = new ConcurrentHashMap<>();

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
			.addSingleton(MultiverseWorldDao.class)
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
				
				if (
					this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null
				) //this.aoCore.initPlaceholderAPI(new Placeholder(this));
				
				this.multiverseWorldDao = new MultiverseWorldDao(this);
				this.multiverseWorlds = new WorldFactory(this).loadExistingWorlds();
			});
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
	
	public Map<String, MultiverseWorld> getMultiverseWorlds() {
		return this.multiverseWorlds;
	}
	
	public MultiverseAdapter getMultiverseAdapter() {
		return this.multiverseAdapter;
	}
	
	private void registerMultiverseAdapter() {
		this.multiverseAdapter = new MultiverseAdapter(this);
		
		this.getServer().getServicesManager().register(MultiverseAdapter.class, this.multiverseAdapter, this, ServicePriority.Highest);
	}
}