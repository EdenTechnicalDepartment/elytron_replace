package me.jandy.eden.elytron_replace.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elytron_replaceClient implements ClientModInitializer {
    // 添加日志记录器
    private static final Logger LOGGER = LoggerFactory.getLogger(Elytron_replaceClient.class);

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerEntity player = client.player;
            if (player != null) {
                ItemStack elytra = player.getEquippedStack(EquipmentSlot.CHEST);
                if (elytra.getItem() instanceof ElytraItem && elytra.getDamage() >= elytra.getMaxDamage() - 10) { // 检查鞘翅耐久度是否低于10
                    LOGGER.info("Elytra durability is low, searching for a new one."); // 添加日志
                    DefaultedList<ItemStack> inventory = player.getInventory().main;
                    boolean foundNewElytra = false;
                    for (int i = 0; i < inventory.size(); i++) {
                        ItemStack itemStack = inventory.get(i);
                        if (itemStack.getItem() instanceof ElytraItem && itemStack.getDamage() < itemStack.getMaxDamage() - 10) {
                            boolean foundSpace = false;
                            for (int j = 0; j < inventory.size(); j++) {
                                if (inventory.get(j).isEmpty()) {
                                    inventory.set(j, elytra); // 将旧鞘翅放回背包的空位
                                    foundSpace = true;
                                    break;
                                }
                            }
                            if (!foundSpace) {
                                LOGGER.info("No space in inventory to store old elytra, discarding it.");
                            }
                            player.equipStack(EquipmentSlot.CHEST, itemStack); // 装备新的鞘翅
                            LOGGER.info("New elytra found and equipped.");
                            foundNewElytra = true;
                            break;
                        }
                    }
                    if (!foundNewElytra) {
                        LOGGER.info("No new elytra found in inventory.");
                    }
                } 
            }
        });
    }
}