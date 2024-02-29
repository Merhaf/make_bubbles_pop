package net.tschipcraft.make_bubbles_pop.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.WaterCurrentDownParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.tschipcraft.make_bubbles_pop.MakeBubblesPop;
import net.tschipcraft.make_bubbles_pop.MakeBubblesPopConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaterCurrentDownParticle.class)
public abstract class CurrentDownPop extends TextureSheetParticle {

    protected CurrentDownPop(ClientLevel clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    // Inject pop particle
    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/WaterCurrentDownParticle;remove()V", shift = At.Shift.AFTER))
    protected void makeBubblesPop$injectPopParticle(CallbackInfo info) {
        //TODO: Global bubble pop
        this.level.addParticle(ParticleTypes.BUBBLE_POP, this.x, this.y, this.z,
                MakeBubblesPopConfig.POPPED_BUBBLES_MAINTAIN_VELOCITY ? this.xd : 0,
                MakeBubblesPopConfig.POPPED_BUBBLES_MAINTAIN_VELOCITY ? this.yd : 0,
                MakeBubblesPopConfig.POPPED_BUBBLES_MAINTAIN_VELOCITY ? this.zd : 0
        );
    }


    // Decrease spiral radius
    @ModifyConstant(method = "tick()V", constant = @Constant(doubleValue = 0.07D), expect = 2)
    private double makeBubblesPop$injectDecreasedSpiralRadius(double value) {
        return 0.06D;
    }

    // Adjust initial particle starting position to make up for the decreased spiral radius
    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static double makeBubblesPop$injectXOffset(double d) {
        return d -= 0.025D;
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private static double makeBubblesPop$injectZOffset(double f) {
        return f += 0.025D;
    }

    // Make hitting the magma block/floor look nicer
    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/WaterCurrentDownParticle;move(DDD)V"))
    private void makeBubblesPop$injectFloorCollision(CallbackInfo ci) {
        if (!this.level.getFluidState(BlockPos.containing(this.x, this.y - this.quadSize * 1.5D, this.z)).is(FluidTags.WATER)) {
            this.yd = 0.01D;
            this.age = this.lifetime;
        }
    }

}