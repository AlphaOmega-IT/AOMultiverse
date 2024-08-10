package de.alphaomegait.aomultiverse.worldgenerator.plotworldgenerator;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class PlotLayer {

  private final int startY;
  private final int endY;
  private final Material material;

  public PlotLayer(
    final int startY,
    final int endY,
    final @NotNull Material material
  ) {
    this.startY = startY;
    this.endY = endY;
    this.material = material;
  }

  public int getStartY() {
    return this.startY;
  }

  public int getEndY() {
    return this.endY;
  }

  public Material getMaterial() {
    return this.material;
  }
}
