package me.fauxle.customenchants.enchants;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EnchantMachineGun implements Enchant {

    private final Set<UUID> tracked_arrows = new HashSet<>();

    @Override
    public String getName() {
        return "Machine Gun";
    }

    @Override
    public boolean isEnchantable(Material type) {
        return type == Material.STICK;
    }

    @Override
    public boolean shouldEnchant(EnchantItemEvent event, List<String> lore) {
        return false;
    }

    public void onProjectileHit(ProjectileHitEvent event) {
        if (tracked_arrows.remove(event.getEntity().getUniqueId())) {
            event.getEntity().remove();
        }
    }

    public void execute(LivingEntity shooter) {
        Arrow arrow = shooter.launchProjectile(Arrow.class, shooter.getEyeLocation().getDirection().multiply(5));

        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setShooter(shooter);
        arrow.setColor(Color.RED);

        tracked_arrows.add(arrow.getUniqueId());
        //arrow.setCritical(true);
    }

}
