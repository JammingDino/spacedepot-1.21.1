package com.jammingdino.jd_spacedepot.block;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import com.jammingdino.jd_spacedepot.block.entity.SpaceLauncherBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import com.jammingdino.jd_spacedepot.quest.QuestManager;
import com.jammingdino.jd_spacedepot.quest.DepotQuest;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public class SpaceLauncherBlock extends BaseEntityBlock {
    public static final MapCodec<SpaceLauncherBlock> CODEC = simpleCodec(SpaceLauncherBlock::new);

    public SpaceLauncherBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpaceLauncherBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SpaceLauncherBlockEntity launcher) {
                player.openMenu(launcher, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }
}