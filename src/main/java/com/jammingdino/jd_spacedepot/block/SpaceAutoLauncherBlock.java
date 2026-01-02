package com.jammingdino.jd_spacedepot.block;

import com.jammingdino.jd_spacedepot.block.entity.SpaceAutoLauncherBlockEntity;
import com.jammingdino.jd_spacedepot.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SpaceAutoLauncherBlock extends BaseEntityBlock {
    public static final MapCodec<SpaceAutoLauncherBlock> CODEC = simpleCodec(SpaceAutoLauncherBlock::new);
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public SpaceAutoLauncherBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TRIGGERED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpaceAutoLauncherBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SpaceAutoLauncherBlockEntity launcher) {
                player.openMenu(launcher, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean hasSignal = level.hasNeighborSignal(pos);
        boolean isTriggered = state.getValue(TRIGGERED);

        if (hasSignal && !isTriggered) {
            // Rising Edge: Trigger the BE
            level.setBlock(pos, state.setValue(TRIGGERED, true), 4);
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SpaceAutoLauncherBlockEntity launcher) {
                launcher.onRedstoneSignal();
            }
        } else if (!hasSignal && isTriggered) {
            // Falling Edge: Reset state
            level.setBlock(pos, state.setValue(TRIGGERED, false), 4);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) return null;
        return createTickerHelper(blockEntityType, ModBlockEntities.SPACE_AUTO_LAUNCHER_BE.get(), SpaceAutoLauncherBlockEntity::tick);
    }
}