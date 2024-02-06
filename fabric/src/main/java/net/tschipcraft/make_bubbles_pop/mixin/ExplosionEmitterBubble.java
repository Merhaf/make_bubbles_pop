package net.tschipcraft.make_bubbles_pop.mixin;

import net.minecraft.client.particle.ExplosionEmitterParticle;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * This mixin injects into the ExplosionEmitter particle class to add bubbles to explosions underwater.
 */
@Mixin(ExplosionEmitterParticle.class)
public abstract class ExplosionEmitterBubble extends NoRenderParticle {

    @Unique
    private static final Random random = new Random();

    protected ExplosionEmitterBubble(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V", shift = At.Shift.AFTER))
    protected void injectBubbleParticle(CallbackInfo info) {
        // Add bubble particles to explosions underwater
        if (this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).isIn(FluidTags.WATER)) {
            for (int i = 0; i < 2; i++) {
                double dx = (random.nextDouble() - random.nextDouble());
                double dy = (random.nextDouble() - random.nextDouble());
                double dz = (random.nextDouble() - random.nextDouble());
                double x = this.x + dx * 4.0;
                double y = this.y + dy * 4.0;
                double z = this.z + dz * 4.0;
                this.world.addParticle(ParticleTypes.BUBBLE, x, y, z, dx * 3, dy * 3, dz * 3);
            }
        }
    }
}
