package de.alphaomegait.aomultiverse.worldgenerator.plotworldgenerator;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
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
	private final boolean fillEntirePlot;

	public PlotBlockPopulator(
		final int plotSize,
		final int plotRoadWidth,
		final int plotHeight,
		final @NotNull Material plotWallMaterial,
		final @NotNull Material plotRandMaterial,
		final @NotNull Material plotFloorMaterial,
		final @NotNull List<PlotLayer> plotLayers,
		final boolean fillEntirePlot
	) {
		this.plotSize = plotSize;
		this.plotRoadWidth = plotRoadWidth;
		this.plotHeight = plotHeight;
		this.plotWallMaterial = plotWallMaterial;
		this.plotRandMaterial = plotRandMaterial;
		this.plotFloorMaterial = plotFloorMaterial;
		this.plotLayers.addAll(plotLayers);
		this.fillEntirePlot = fillEntirePlot;
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
		int minY = worldInfo.getMinHeight();

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int absX = startX + x;
				int absZ = startZ + z;

				// Set Bedrock layer at the bottom
				limitedRegion.setType(absX, minY, absZ, Material.BEDROCK);

				if (isPlotBorder(absX, absZ)) {
					for (int y = minY + 1; y < plotHeight; y++) {
						limitedRegion.setType(absX, y, absZ, plotWallMaterial);
					}
					limitedRegion.setType(absX, plotHeight, absZ, plotRandMaterial);
				} else if (isPlotRoad(absX, absZ)) {
					limitedRegion.setType(absX, plotHeight - 1, absZ, Material.GRASS_BLOCK);
				} else {
					if (fillEntirePlot) {
						for (int y = minY + 1; y < plotHeight; y++) {
							limitedRegion.setType(absX, y, absZ, plotFloorMaterial);
						}
					} else {
						for (PlotLayer plotLayer : plotLayers) {
							for (int y = plotLayer.getStartY(); y < plotLayer.getEndY(); y++) {
								limitedRegion.setType(absX, y, absZ, plotFloorMaterial);
							}
						}
					}
				}
			}
		}
	}

	private boolean isPlotBorder(final int absX, final int absZ) {
		final int moduloX = Math.floorMod(absX, this.plotSize + this.plotRoadWidth);
		final int moduloZ = Math.floorMod(absZ, this.plotSize + this.plotRoadWidth);
		return (moduloX == this.plotRoadWidth || moduloX == this.plotSize + this.plotRoadWidth - 1) ||
		       (moduloZ == this.plotRoadWidth || moduloZ == this.plotSize + this.plotRoadWidth - 1);
	}

	private boolean isPlotRoad(final int absX, final int absZ) {
		return (Math.floorMod(absX, this.plotSize + this.plotRoadWidth) < this.plotRoadWidth) ||
		       (Math.floorMod(absZ, this.plotSize + this.plotRoadWidth) < this.plotRoadWidth);
	}
}