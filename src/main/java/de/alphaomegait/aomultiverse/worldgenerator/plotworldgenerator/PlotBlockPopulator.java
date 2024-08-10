package de.alphaomegait.aomultiverse.worldgenerator.plotworldgenerator;

import de.alphaomegait.aomultiverse.AOMultiverse;
import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlotBlockPopulator extends BlockPopulator {

  private final int plotSize;
  private final int plotRoadWidth;
  private final int plotHeight;
  private final Material plotWallMaterial;
  private final Material plotRandMaterial;
  private final Material plotFloorMaterial;
  private final List<PlotLayer> plotLayers = new ArrayList<>();

  public PlotBlockPopulator(
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
    this.plotLayers.addAll(plotLayers);
  }

  @Override
  public void populate(
    final @NotNull WorldInfo worldInfo,
    final @NotNull Random random,
    final int chunkX,
    final int chunkZ,
    final @NotNull LimitedRegion limitedRegion
  ) {
    int startX = chunkX << 4;
    int startZ = chunkZ << 4;

    new BukkitRunnable() {

      int x = 0;
      int z = 0;

      @Override
      public void run() {
        if (
          x < 16
        ) {
          int absX = startX + x;
          int absZ = startZ + z;

          if (
            PlotBlockPopulator.this.isPlotBorder(absX, absZ)
          ) {
            for (
              int y = PlotBlockPopulator.this.plotHeight; y < PlotBlockPopulator.this.plotHeight + 4; y++
            ) limitedRegion.setType(absX, y, absZ, PlotBlockPopulator.this.plotWallMaterial);
            limitedRegion.setType(absX, PlotBlockPopulator.this.plotHeight, absZ, PlotBlockPopulator.this.plotRandMaterial);
          } else if (
            ! PlotBlockPopulator.this.isPlotRoad(absX, absZ)
          ) {
            for (
              final @NotNull PlotLayer plotLayer : PlotBlockPopulator.this.plotLayers
            ) {
              for (
                int y = plotLayer.getStartY(); y < plotLayer.getEndY(); y++
              ) limitedRegion.setType(absX, y, absZ, PlotBlockPopulator.this.plotFloorMaterial);
            }
          }

          z++;
          if (
            z >= 16
          ) {
            z = 0;
            x++;
          }
        } else {
          this.cancel();
        }
      }
    }.runTaskTimer(AOMultiverse.getProvidingPlugin(AOMultiverse.class), 0L, 1L);
  }

  private boolean isPlotBorder(
    final int absX,
    final int absZ
  ) {
    final int moduloX = absX % (this.plotSize + this.plotRoadWidth);
    final int moduloZ = absZ % (this.plotSize + this.plotRoadWidth);
    return
      (moduloX == this.plotRoadWidth || moduloX == this.plotSize + this.plotRoadWidth - 1) ||
      (moduloZ == this.plotRoadWidth || moduloZ == this.plotSize + this.plotRoadWidth - 1);
  }

  private boolean isPlotRoad(
    final int absX,
    final int absZ
  ) {
    return
      (absX % (this.plotSize + this.plotRoadWidth) < this.plotRoadWidth) ||
      (absZ % (this.plotSize + this.plotRoadWidth) < this.plotRoadWidth);
  }
}
