package net.tschipcraft.make_bubbles_pop.mixin.client;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.tschipcraft.make_bubbles_pop.MakeBubblesPop;
import net.tschipcraft.make_bubbles_pop.MakeBubblesPopConfig;
import net.tschipcraft.make_bubbles_pop.impl.BarrelBlockEntityInterface;
import net.tschipcraft.make_bubbles_pop.impl.BarrelBubbler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

/**
 * This mixin injects into the BarrelBlock class to add bubbles to opening barrels underwater.
 */
@Mixin(BarrelBlock.class)
public abstract class BarrelOnUse extends BlockWithEntity {

    @Unique
    private static final List<BarrelBlockEntity> OPENED_BARRELS = new ArrayList<>();

    protected BarrelOnUse(Settings settings) {
        super(settings);
    }

    // Experimental - register BarrelBlock to tick on the client instead of using packets
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? checkType(type, BlockEntityType.BARREL, this::clientTick) : null;
    }

    @Unique
    public void clientTick(World world, BlockPos pos, BlockState state, BarrelBlockEntity blockEntity) {
        if (world != null && world.isClient && (!MakeBubblesPop.MIDNIGHTLIB_INSTALLED || MakeBubblesPopConfig.BARREL_BUBBLES_ENABLED)) {
            if (((BarrelBlockEntityInterface)blockEntity).makeBubblesPop$wasLoaded()) {
                // Get direction of barrel block and test if its underwater
                Direction facing = state.getOrEmpty(BarrelBlock.FACING).orElse(Direction.NORTH);
                boolean open = state.getOrEmpty(BarrelBlock.OPEN).orElse(false);
                if (world.isWater(pos.offset(facing)) && open) {
                    if (!OPENED_BARRELS.contains(blockEntity)) {
                        // A barrel block has been opened underwater
                        OPENED_BARRELS.add(blockEntity);
                        BarrelBubbler.spawnBubbles(world, pos, facing, world.random);
                    }
                } else {
                    // Barrel block closed
                    OPENED_BARRELS.remove(blockEntity);
                }
            } else {
                Direction facing = state.getOrEmpty(BarrelBlock.FACING).orElse(Direction.NORTH);
                boolean open = state.getOrEmpty(BarrelBlock.OPEN).orElse(false);
                if (world.isWater(pos.offset(facing)) && open) {
                    if (!OPENED_BARRELS.contains(blockEntity)) {
                        // Mark barrel as open to prevent it from creating bubbles upon loading if already open
                        OPENED_BARRELS.add(blockEntity);
                    }
                }
                ((BarrelBlockEntityInterface)blockEntity).makeBubblesPop$setLoaded(true);
            }
        }
    }

    // Prevent memory leaks
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        OPENED_BARRELS.clear();
    }
}
