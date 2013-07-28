package org.samson.bukkit.plugin.bouncingarrows;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class BouncingArrowsEventListener implements Listener {

	private BouncingArrows plugin;

	public BouncingArrowsEventListener(BouncingArrows plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityShootBowEvent(EntityShootBowEvent event) {
		
		LivingEntity entity = event.getEntity();
		Entity projectile = event.getProjectile();
		
		if (entity instanceof Player && projectile.getType() == EntityType.ARROW) {
			
			ItemStack theBow = event.getBow();
			
			if (theBow.hasItemMeta() && theBow.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
				projectile.setMetadata("bouncing", new FixedMetadataValue(plugin, event.getForce()));
			}
			
		}
		
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onProjectileHitEvent(ProjectileHitEvent event) {
		
		Projectile entity = event.getEntity();
		
		LivingEntity shooter = entity.getShooter();
		
		if (shooter != null 
				&& shooter instanceof Player 
				&& entity.getType() == EntityType.ARROW
				&& entity.hasMetadata("bouncing")) {

			Vector arrowVector = entity.getVelocity();
			
			Location hitLoc = entity.getLocation();
			
			BlockIterator b = new BlockIterator(hitLoc.getWorld(), 
					hitLoc.toVector(), arrowVector, 0, 3);
			
			Block hitBlock = event.getEntity().getLocation().getBlock();
			
			Block blockBefore = hitBlock;
			Block nextBlock = b.next();
			
			while (b.hasNext() && nextBlock.getType() == Material.AIR)
			{
				blockBefore = nextBlock;
				nextBlock = b.next();
			}
			
			BlockFace blockFace = nextBlock.getFace(blockBefore);

			if (blockFace != null) {

				Vector hitPlain = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
				
				double dotProduct = arrowVector.dot(hitPlain);
				Vector u = hitPlain.multiply(dotProduct).multiply(2.0);
				
				float speed = 0.6F;
				
				List<MetadataValue> metaDataValues = entity.getMetadata("bouncing");
				if (metaDataValues.size() > 0) {
					speed = metaDataValues.get(0).asFloat();
					speed *= 1.25F;
				}
				
				Arrow newArrow = entity.getWorld().spawnArrow(entity.getLocation(), arrowVector.subtract(u), speed, 12.0F);
				newArrow.setShooter(shooter);
				newArrow.setFireTicks(entity.getFireTicks());
			
				entity.remove();
				
			}
		
		}
	}	
	
}
