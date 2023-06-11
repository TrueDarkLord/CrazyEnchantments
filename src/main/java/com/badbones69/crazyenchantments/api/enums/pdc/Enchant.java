package com.badbones69.crazyenchantments.api.enums.pdc;

import com.badbones69.crazyenchantments.api.objects.CEnchantment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Enchant implements Serializable {

    private final HashMap<CEnchantment, Integer> enchants;

    public Enchant(HashMap<CEnchantment, Integer> enchants) {
        this.enchants = enchants;
    }

    /**
     *
     * @param enchantment
     * @return true if the item has the specified enchantment.
     */
    public boolean hasEnchantment(CEnchantment enchantment) { return this.enchants.containsKey(enchantment); }

    /**
     *
     * @return Hashmap of all enchantments and their corresponding levels.
     */
    public HashMap<CEnchantment, Integer> getFullEnchantments() { return this.enchants; }

    /**
     *
     * @return
     */
    public Set<CEnchantment> getEnchantments() { return this.enchants.keySet(); }

    public Integer getLevel(CEnchantment enchantment) { return this.enchants.get(enchantment); }

    /**
     *
     * @param enchantment
     */
    public void addEnchantments(Map<CEnchantment, Integer> enchantment) { this.enchants.putAll(enchantment); }

    /**
     *
     * @param enchantment
     * @param level
     */
    public void addEnchantment(CEnchantment enchantment, Integer level) { this.enchants.put(enchantment, level); }

    /**
     *
     * @param enchantment
     */
    public void removeEnchantment(CEnchantment enchantment) { this.enchants.remove(enchantment); }

}
