package de.alphaomegait.aomultiverse.database.entities;

import de.alphaomegait.aocore.database.converter.LocationConverter;
import de.alphaomegait.aocore.database.entities.BaseEntity;
import de.alphaomegait.aomultiverse.commands.aomultiverse.EAOMultiverseWorldType;
import jakarta.persistence.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a world in the multiverse.
 */
@Entity
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
@Table(name = "ao_multiverse_world")
@SequenceGenerator(allocationSize = 1, name = "SQ_GEN_HIBERNATE", sequenceName = "SQ_GEN_HIBERNATE")
public class MultiverseWorld extends BaseEntity<MultiverseWorld> {
	
    @Column(nullable = false, unique = true, name = "world_name")
    private String worldName;

    @Column(nullable = false, name = "world_type")
    private String worldType;

    @Convert(converter = LocationConverter.class)
    @Column(nullable = false, columnDefinition = "LONGTEXT", name = "spawn_location")
    private Location spawnLocation;

    @Column(nullable = false, name = "world_size")
    private Long worldSize;

    @Column(nullable = false, name = "world_seed")
    private Long worldSeed;

    @Column(nullable = false, name = "allow_nether")
    private boolean allowNether = false;

    @Column(nullable = false, name = "allow_the_end")
    private boolean allowTheEnd = false;

    @Column(nullable = false, name = "allow_pvp")
    private boolean allowPVP = false;

    @Column(nullable = false, name = "has_global_spawn")
    private boolean hasGlobalSpawn;

    @Column(name = "enter_permission")
    private String enterPermission;

    public MultiverseWorld() {}
	
	/**
	 * Constructs a MultiverseWorld object with the given World and world type.
	 * @param world The Bukkit World object.
	 * @param worldType The type of the world.
	 */
    public MultiverseWorld(@NotNull World world, @NotNull EAOMultiverseWorldType worldType) {
        this(world, worldType, "");
    }
	
	/**
	 * Constructs a MultiverseWorld object with the given World, world type, and enter permission.
	 * @param world The Bukkit World object.
	 * @param worldType The type of the world.
	 * @param enterPermission The permission required to enter the world.
	 */
    public MultiverseWorld(@NotNull World world, @NotNull EAOMultiverseWorldType worldType, @NotNull String enterPermission) {
        this.worldName = world.getName();
        this.spawnLocation = world.getSpawnLocation().toCenterLocation().add(0, 1, 0);
        this.worldSize = (long) world.getWorldBorder().getSize();
        this.worldSeed = world.getSeed();
        this.allowNether = world.getEnvironment() == World.Environment.NETHER;
        this.allowTheEnd = world.getEnvironment() == World.Environment.THE_END;
        this.worldType = worldType.name();
        this.allowPVP = world.getPVP();
        this.hasGlobalSpawn = false;
        this.enterPermission = enterPermission;
    }

    @Override
    public MultiverseWorld getSelf() {
        return this;
    }
	
	public String getWorldName() {
		return worldName;
	}
	
	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}
	
	public String getWorldType() {
		return worldType;
	}
	
	public void setWorldType(String worldType) {
		this.worldType = worldType;
	}
	
	public Location getSpawnLocation() {
		return spawnLocation;
	}
	
	public void setSpawnLocation(Location spawnLocation) {
		this.spawnLocation = spawnLocation;
	}
	
	public Long getWorldSize() {
		return worldSize;
	}
	
	public void setWorldSize(Long worldSize) {
		this.worldSize = worldSize;
	}
	
	public Long getWorldSeed() {
		return worldSeed;
	}
	
	public void setWorldSeed(Long worldSeed) {
		this.worldSeed = worldSeed;
	}
	
	public boolean isAllowNether() {
		return allowNether;
	}
	
	public void setAllowNether(boolean allowNether) {
		this.allowNether = allowNether;
	}
	
	public boolean isAllowTheEnd() {
		return allowTheEnd;
	}
	
	public void setAllowTheEnd(boolean allowTheEnd) {
		this.allowTheEnd = allowTheEnd;
	}
	
	public boolean isAllowPVP() {
		return allowPVP;
	}
	
	public void setAllowPVP(boolean allowPVP) {
		this.allowPVP = allowPVP;
	}
	
	public boolean isHasGlobalSpawn() {
		return hasGlobalSpawn;
	}
	
	public void setHasGlobalSpawn(boolean hasGlobalSpawn) {
		this.hasGlobalSpawn = hasGlobalSpawn;
	}
	
	public String getEnterPermission() {
		return enterPermission;
	}
	
	public void setEnterPermission(String enterPermission) {
		this.enterPermission = enterPermission;
	}
}