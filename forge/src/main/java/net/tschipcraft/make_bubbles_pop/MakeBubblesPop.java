package net.tschipcraft.make_bubbles_pop;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MakeBubblesPop.MODID)
public class MakeBubblesPop
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "make_bubbles_pop";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    public MakeBubblesPop()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the setup method for modloading
        modEventBus.addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("Make Bubbles Pop by Tschipcraft initialized!");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void RightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        BlockHitResult block = event.getHitVec();
        Level level = event.getLevel();
        if (block.getType() == HitResult.Type.BLOCK && !event.getEntity().isSpectator() && !event.getEntity().isCrouching()) {

            if (level.getBlockState(block.getBlockPos()).getBlock() instanceof ChestBlock) {
                // A chest block was right-clicked
                if (!ChestBlock.isChestBlockedAt(level, block.getBlockPos()) && event.getLevel().getFluidState(block.getBlockPos()).is(FluidTags.WATER)) {
                    // The chest block is waterlogged and not blocked -> show bubbles + play sound
                    for (int i = (int)(Math.random() * 5) + 5; i >= 0; i--) {
                        level.addParticle(ParticleTypes.BUBBLE, block.getBlockPos().getX() + 0.5 + ((0.5 - Math.random())/2.0), block.getBlockPos().getY() + 0.7 - (Math.random()/2.0), block.getBlockPos().getZ() + 0.5 + ((0.5 - Math.random())/2.0), 0f, 0.1f, 0f);
                        level.playLocalSound(block.getBlockPos().getX(), block.getBlockPos().getY(), block.getBlockPos().getZ(), SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.AMBIENT, 0.1f, 1.4f, false);
                    }

                }
            }
        }
    }
}