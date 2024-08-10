package de.alphaomegait.aomultiverse.worldgenerator.plotworldgenerator;

import de.alphaomegait.aomultiverse.worldgenerator.voidworldgenerator.VoidBiomeProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlotChunkGenerator extends ChunkGenerator {

  private final int plotSize;
  private final int plotRoadWidth;
  private final int plotHeight;
  private final Material plotWallMaterial;
  private final Material plotRandMaterial;
  private final Material plotFloorMaterial;
  private final List<PlotLayer> plotLayers;

  public PlotChunkGenerator(
    final int plotSize,
    final int plotRoadWidth,
    final int plotHeight,
    final @NotNull Material plotWallMaterial,
    final @NotNull Material plotRandMaterial,
    final @NotNull Material plotFloorMaterial,
    final @NotNull List<PlotLayer> plotLayers
  ) {
    this.plotSize = plotSize;
    this.plotRoadWidth = plotRoadWidth;
    this.plotHeight = plotHeight;
    this.plotWallMaterial = plotWallMaterial;
    this.plotRandMaterial = plotRandMaterial;
    this.plotFloorMaterial = plotFloorMaterial;
    this.plotLayers = plotLayers;
  }

  public PlotChunkGenerator(
    final int plotSize,
    final int plotRoadWidth,
    final int plotHeight,
    final @NotNull Material plotWallMaterial,
    final @NotNull Material plotRandMaterial,
    final @NotNull Material plotFloorMaterial
  ) {
    this(
      plotSize,
      plotRoadWidth,
      plotHeight,
      plotWallMaterial,
      plotRandMaterial,
      plotFloorMaterial,
      List.of(
        new PlotLayer(1, plotHeight - 1, Material.DIRT),
        new PlotLayer(plotHeight, plotHeight + 1, Material.GRASS_BLOCK)
      )
    );
  }

  @Override
  public void generateNoise(
    final @NotNull WorldInfo worldInfo,
    final @NotNull Random random,
    final int chunkX,
    final int chunkZ,
    final @NotNull ChunkData chunkData
  ) {
    // NOT NEEDED DUE TO VOID WORLD
  }

  @Override
  public void generateSurface(
    final @NotNull WorldInfo worldInfo,
    final @NotNull Random random,
    final int chunkX,
    final int chunkZ,
    final @NotNull ChunkData chunkData
  ) {
    // NOT NEEDED DUE TO VOID WORLD
  }

  @Override
  public void generateBedrock(
    final @NotNull WorldInfo worldInfo,
    final @NotNull Random random,
    final int chunkX,
    final int chunkZ,
    final @NotNull ChunkData chunkData
  ) {
    // NOT NEEDED DUE TO VOID WORLD
  }

  @Override
  public void generateCaves(
    final @NotNull WorldInfo worldInfo,
    final @NotNull Random random,
    final int chunkX,
    final int chunkZ,
    final @NotNull ChunkData chunkData
  ) {
    // NOT NEEDED DUE TO VOID WORLD
  }

  @Override
  public @Nullable BiomeProvider getDefaultBiomeProvider(
    final @NotNull WorldInfo worldInfo
  ) {
    return new PlotBiomeProvider();
  }

  @Override
  public
  final @NotNull List<BlockPopulator> getDefaultPopulators(
    final @NotNull World world
  ) {
    return Collections.singletonList(
      new PlotBlockPopulator(
        this.plotSize,
        this.plotRoadWidth,
        this.plotHeight,
        this.plotWallMaterial,
        this.plotRandMaterial,
        this.plotFloorMaterial,
        this.plotLayers
      )
    );
  }

  @Override
  public @Nullable Location getFixedSpawnLocation(
    final @NotNull World world,
    final @NotNull Random random
  ) {
    return new Location(world, 0.0, this.plotHeight, 0.0);
  }

  @Override
  public boolean shouldGenerateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
    return false;
  }

  @Override
  public boolean shouldGenerateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
    return false;
  }

  @Override
  public boolean shouldGenerateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
    return false;
  }

  @Override
  public boolean shouldGenerateDecorations(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
    return false;
  }

  @Override
  public boolean shouldGenerateMobs() {
    return false;
  }

  @Override
  public boolean shouldGenerateMobs(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
    return false;
  }

  @Override
  public boolean shouldGenerateStructures(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
    return false;
  }
}
