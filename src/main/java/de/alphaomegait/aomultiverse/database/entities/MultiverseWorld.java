package de.alphaomegait.aomultiverse.database.entities;

import de.alphaomegait.aomultiverse.commands.aomultiverse.EAOMultiverseWorldType;
import de.alphaomegait.woocore.database.converter.LocationConverter;
import de.alphaomegait.woocore.database.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.hibernate.annotations.NamedQuery;
import org.jetbrains.annotations.NotNull;

@NamedQuery(
	name = "MultiverseWorld.findByName",
	query = "SELECT m FROM MultiverseWorld m WHERE m.worldName = :worldName"
)
@NamedQuery(
	name = "MultiverseWorld.findType",
	query = "SELECT m FROM MultiverseWorld m WHERE m.worldType = :worldType"
)
@NamedQuery(
	name = "MultiverseWorld.findAll",
	query = "SELECT m FROM MultiverseWorld m"
)
@NamedQuery(
	name = "MultiverseWorld.findByIsGlobalSpawn",
	query = "SELECT m FROM MultiverseWorld m WHERE m.hasGlobalSpawn = TRUE"
)
@Entity
@Table(name = "multiverse_world")
public class MultiverseWorld extends BaseEntity<MultiverseWorld> {

	@Column(
		name = "world_name",
		nullable = false,
		unique = true
	)
	private String worldName;

	@Column(
		name = "world_type",
		nullable = false
	)
	private String worldType;

	@Convert(converter = LocationConverter.class)
	@Column(
		name = "spawn_location",
		nullable = false
	)
	private Location spawnLocation;

	@Column(
		name = "world_size",
		nullable = false
	)
	private Long worldSize;

	@Column(
		name = "world_seed",
		nullable = false
	)
	private Long worldSeed;

	@Column(
		name = "allow_nether",
		nullable = false
	)
	private Boolean allowNether;

	@Column(
		name = "allow_the_end",
		nullable = false
	)
	private Boolean allowTheEnd;

	@Column(
		name = "allow_pvp",
		nullable = false
	)
	private Boolean allowPVP;

	@Column(
		name = "has_global_spawn",
		nullable = false
	)
	private Boolean hasGlobalSpawn;

	public MultiverseWorld() {}

	public MultiverseWorld(
		final @NotNull World world,
		final @NotNull EAOMultiverseWorldType worldType
	) {
		this.worldName = world.getName();
		this.spawnLocation = world.getSpawnLocation();
		this.worldSize = (long) world.getWorldBorder().getSize();
		this.worldSeed = world.getSeed();
		this.allowNether = Bukkit.getAllowNether();
		this.allowTheEnd = Bukkit.getAllowEnd();
		this.worldType = worldType.name();
		this.allowPVP = world.getPVP();
		this.hasGlobalSpawn = false;
	}

	@Override
	public MultiverseWorld getSelf() {
		return this;
	}

	public String getWorldName() {
		return this.worldName;
	}

	public String getWorldType() {
		return this.worldType;
	}

	public Location getSpawnLocation() {
		return this.spawnLocation;
	}

	public Long getWorldSize() {
		return this.worldSize;
	}

	public Long getWorldSeed() {
		return this.worldSeed;
	}

	public Boolean getAllowNether() {
		return this.allowNether;
	}

	public Boolean getAllowTheEnd() {
		return this.allowTheEnd;
	}

	public Boolean getAllowPVP() {
		return this.allowPVP;
	}

	public Boolean getHasGlobalSpawn() {
		return this.hasGlobalSpawn;
	}

	public void setWorldName(final String worldName) {
		this.worldName = worldName;
	}

	public void setWorldType(final String worldType) {
		this.worldType = worldType;
	}

	public void setSpawnLocation(final Location spawnLocation) {
		this.spawnLocation = spawnLocation;
	}

	public void setWorldSize(final Long worldSize) {
		this.worldSize = worldSize;
	}

	public void setWorldSeed(final Long worldSeed) {
		this.worldSeed = worldSeed;
	}

	public void setAllowNether(final Boolean allowNether) {
		this.allowNether = allowNether;
	}

	public void setAllowTheEnd(final Boolean allowTheEnd) {
		this.allowTheEnd = allowTheEnd;
	}

	public void setAllowPVP(final Boolean allowPVP) {
		this.allowPVP = allowPVP;
	}

	public void setHasGlobalSpawn(final Boolean hasGlobalSpawn) {
		this.hasGlobalSpawn = hasGlobalSpawn;
	}
}