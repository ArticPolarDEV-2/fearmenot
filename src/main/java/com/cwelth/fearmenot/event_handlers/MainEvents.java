package com.cwelth.fearmenot.event_handlers;

import com.cwelth.fearmenot.ModMain;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// MainEvents.java
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MainEvents {
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent e) {
        if (ModMain.Configuration.MOD_ENABLED.get() && e.getEntity() instanceof PathfinderMob) {
            PathfinderMob entity = (PathfinderMob) e.getEntity();

            // Aplicar comportamento apenas a animais, excluindo monstros.
            if (entity.getType().getCategory().isFriendly()) {
                // Conversão explícita de Double para float
                entity.goalSelector.addGoal(0, new AvoidEntityGoal<Player>(
                        entity,
                        Player.class,
                        player -> isPlayerTooClose(player, entity), // Verificando se é Player
                        ModMain.Configuration.AVOID_DISTANCE.get().floatValue(),
                        ModMain.Configuration.FAR_SPEED.get(),
                        ModMain.Configuration.NEAR_SPEED.get(),
                        player -> player.isAlive()
                ));
            }
        }
    }

    private static boolean isPlayerTooClose(LivingEntity livingEntity, PathfinderMob entity) {
        if (livingEntity instanceof Player) {
            Player player = (Player) livingEntity; // Converte para Player
            double distance = player.isCrouching() ?
                    ModMain.Configuration.CROUCH_DISTANCE.get() : ModMain.Configuration.AVOID_DISTANCE.get();
            return entity.distanceTo(player) <= distance;
        }
        return false; // Retorna falso se não for um Player
    }
}
