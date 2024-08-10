package de.alphaomegait.aomultiverse.worldgenerator.plotworldgenerator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlotBiomeProvider extends BiomeProvider {

	/**
	 * A description of the entire Java function.
	 *
	 * @param  info	description of parameter
	 * @param  x	    description of parameter
	 * @param  y	    description of parameter
	 * @param  z	    description of parameter
	 * @return        description of return value
	 */
	@Override
	public @NotNull Biome getBiome(
		@NotNull final WorldInfo info,
		final int x,
		final int y,
		final int z
	) {
		return Biome.PLAINS;
	}

	/**
	 * Retrieves a list of biomes based on the provided world information.
	 *
	 * @param  info  the world information used to determine the biomes
	 * @return       a list of biomes
	 */
	@Override
	public @NotNull List<Biome> getBiomes(
		@NotNull final WorldInfo info
	) {
		return new ArrayList<>(List.of(Biome.PLAINS));
	}
}