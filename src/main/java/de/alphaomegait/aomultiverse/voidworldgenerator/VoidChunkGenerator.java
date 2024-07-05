package de.alphaomegait.aomultiverse.voidworldgenerator;

import org.bukkit.HeightMap;
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

public class VoidChunkGenerator extends ChunkGenerator {
    
    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.@NotNull ChunkData chunkData) {
        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                for(int y = worldInfo.getMinHeight(); y <= worldInfo.getMaxHeight(); ++y) {
                    chunkData.setBlock(x, y, z, Material.VOID_AIR);
                }
            }
        }
        
    }
    
    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.@NotNull ChunkData chunkData) {
        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                for(int y = worldInfo.getMinHeight(); y <= worldInfo.getMaxHeight(); ++y) {
                    chunkData.setBlock(x, y, z, Material.VOID_AIR);
                }
            }
        }
        
    }
    
    @Override
    public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.@NotNull ChunkData chunkData) {
        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                for(int y = worldInfo.getMinHeight(); y <= worldInfo.getMaxHeight(); ++y) {
                    chunkData.setBlock(x, y, z, Material.VOID_AIR);
                }
            }
        }
        
    }
    
    @Override
    public void generateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.@NotNull ChunkData chunkData) {
        // NOT NEEDED DUE TO VOID WORLD
    }
    
    @Override
    public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return new VoidBiomeProvider();
    }
    
    @Override
    public int getBaseHeight(@NotNull WorldInfo worldInfo, @NotNull Random random, int x, int z, @NotNull HeightMap heightMap) {
        return 96;
    }
    
    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(
      final @NotNull World world
    ) {
        return Collections.singletonList(new VoidBlockPopulator());
    }
    
    @Override
    public @Nullable Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 0.0, 96.0, 0.0);
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
        return true;
    }
    
    @Override
    public boolean shouldGenerateMobs(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
        return true;
    }
    
    @Override
    public boolean shouldGenerateStructures(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
        return false;
    }
}