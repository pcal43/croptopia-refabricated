package com.epherical.croptopia.client;

import com.epherical.croptopia.ClientFunctions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;


@Environment(EnvType.CLIENT)
public class CroptopiaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientFunctions functions = new ClientFunctions();
        ColorProviderRegistry.BLOCK.register(functions.registerLeafColors(), functions.leaves());
        functions.registerBlockLayers(this::registerCropBlockLayer);
    }

    public void registerCropBlockLayer(Block block) {
        BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT);
    }
}
