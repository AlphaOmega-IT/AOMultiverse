package de.alphaomegait.aomultiverse.voidworldgenerator;

import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class VoidBlockPopulator extends BlockPopulator {
    
    @Override
    public void populate(
			@NotNull final WorldInfo worldInfo,
			@NotNull final Random random,
			final int chunkX,
			final int chunkZ,
			@NotNull final LimitedRegion limitedRegion
		) {
			// Empty world
    }
}