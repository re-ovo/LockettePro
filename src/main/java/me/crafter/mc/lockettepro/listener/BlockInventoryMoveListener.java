package me.crafter.mc.lockettepro.listener;

import me.crafter.mc.lockettepro.api.LocketteProAPI;
import me.crafter.mc.lockettepro.config.Config;
import me.crafter.mc.lockettepro.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Hopper;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BlockInventoryMoveListener implements Listener {
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryMove(InventoryMoveItemEvent event){
        if (Config.isItemTransferOutBlocked() || Config.getHopperMinecartAction() != (byte)0){
            if (isInventoryLocked(event.getSource())){
                if (Config.isItemTransferOutBlocked()){
                    event.setCancelled(true);
                }
                // Additional Hopper Minecart Check
                if (event.getDestination().getHolder() instanceof HopperMinecart) {
                    byte hopperminecartaction = Config.getHopperMinecartAction();
                    switch (hopperminecartaction){
                    // case 0 - Impossible
                    case (byte)1: // Cancel only, it is not called if !Config.isItemTransferOutBlocked()
                        event.setCancelled(true);
                        break;
                    case (byte)2: // Extra action - HopperMinecart removal
                        event.setCancelled(true);
                        ((HopperMinecart)event.getDestination().getHolder()).remove();
                        break;
                    }
                } else if (event.getDestination().getHolder() instanceof Hopper || event.getSource().getHolder() instanceof Hopper) {
                	if (isInventoryLocked(event.getDestination())) {
                		Block sourceInventoryBlock = event.getSource().getLocation().getBlock();
                		Block destinationInventoryBlock = event.getDestination().getLocation().getBlock();
                		if (sourceInventoryBlock != null && destinationInventoryBlock != null) {
                			String sourceOwner = LocketteProAPI.getOwner(sourceInventoryBlock);
                			String destOwner = LocketteProAPI.getOwner(destinationInventoryBlock);
                			 if (destOwner != null && sourceOwner != null) {
                				 if (sourceOwner.matches(destOwner)) {
                         			event.setCancelled(false);
                         		}
                			 }
                		}
                	}
                }
                return;
            }
        }
        if (Config.isItemTransferInBlocked()){
            if (isInventoryLocked(event.getDestination())){
                event.setCancelled(true);
                return;
            }
        }
    }
    
    public boolean isInventoryLocked(Inventory inventory){
        InventoryHolder inventoryholder = inventory.getHolder();
        if (inventoryholder instanceof DoubleChest){
            inventoryholder = ((DoubleChest)inventoryholder).getLeftSide();
        }
        if (inventoryholder instanceof BlockState){
            Block block = ((BlockState)inventoryholder).getBlock();
            if (Config.isCacheEnabled()){ // Cache is enabled
                if (Utils.hasValidCache(block)){
                    return Utils.getAccess(block);
                } else {
                    if (LocketteProAPI.isLocked(block)){
                        Utils.setCache(block, true);
                        return true;
                    } else {
                        Utils.setCache(block, false);
                        return false;
                    }
                }
            } else { // Cache is disabled
                if (LocketteProAPI.isLocked(block)){
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
    
}
