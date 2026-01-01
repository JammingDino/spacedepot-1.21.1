package com.jammingdino.jd_spacedepot.block.crate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SpaceCrateBlock extends BaseEntityBlock {
    public static final MapCodec<SpaceCrateBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    StringRepresentable.fromEnum(CrateTier::values).fieldOf("tier").forGetter(SpaceCrateBlock::getTier),
                    propertiesCodec()
            ).apply(instance, SpaceCrateBlock::new)
    );

    private final CrateTier tier;

    public SpaceCrateBlock(CrateTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public CrateTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpaceCrateBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    // --- COMPARATOR SUPPORT ---
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return net.minecraft.world.inventory.AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    // --- INTERACTION ---
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SpaceCrateBlockEntity crate) {
                player.openMenu(crate, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    // --- DROPS LOGIC (Handles Pistons, Explosions, and Survival Players) ---
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        // Get the Block Entity from the loot parameters
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof SpaceCrateBlockEntity crateBE) {
            ItemStack itemStack = new ItemStack(this);

            // Copy items into the stack
            if (!crateBE.isEmpty()) {
                ItemContainerContents contents = ItemContainerContents.fromItems(crateBE.getItems());
                itemStack.set(DataComponents.CONTAINER, contents);
            }

            // Copy custom name
            if (crateBE.hasCustomName()) {
                itemStack.set(DataComponents.CUSTOM_NAME, crateBE.getCustomName());
            }

            // Return the crate as the drop
            return Collections.singletonList(itemStack);
        }

        // Fallback (shouldn't happen)
        return super.getDrops(state, params);
    }

    // --- REMOVAL ---
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        // We removed the drop logic from here because getDrops handles it now.
        // This method is primarily used for special logic if a player breaks it (like stats),
        // or preventing drops in Creative mode (which Minecraft handles automatically by not calling getDrops).
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // NOTE: We do NOT call Containers.dropContents here, because we want the contents
            // to stay inside the crate item, not spill onto the floor.
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}