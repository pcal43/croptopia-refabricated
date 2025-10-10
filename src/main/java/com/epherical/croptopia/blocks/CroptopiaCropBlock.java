package com.epherical.croptopia.blocks;

import com.epherical.croptopia.items.SeedItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CroptopiaCropBlock extends CropBlock {
    protected static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    private SeedItem seed;

    public CroptopiaCropBlock(Properties settings) {
        super(settings);
    }

    public void setSeed(SeedItem seed) {
        this.seed = seed;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return AGE_TO_SHAPE[state.getValue(this.getAgeProperty())];
    }

    @Override // JANK
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (world.getChunk(pos).getHighestGeneratedStatus().getIndex() < ChunkStatus.FULL.getIndex()) {
            // ON WORLD GENERATION
            if (seed.getCategory() != null && world.getBiome(pos).is(seed.getCategory())) {
                return super.canSurvive(state, world, pos);
            }
        } else if (world.getChunk(pos).getHighestGeneratedStatus().getIndex() == ChunkStatus.FULL.getIndex()) {
            // ON PLAYER PLACEMENT
            return super.canSurvive(state, world, pos);
        }
        return false;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seed != null ? seed : Items.AIR;
    }
}
