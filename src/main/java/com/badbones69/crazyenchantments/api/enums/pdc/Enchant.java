package com.badbones69.crazyenchantments.api.enums.pdc;

import com.badbones69.crazyenchantments.api.objects.CEnchantment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class Enchant implements Serializable {

    private final HashMap<CEnchantment, Integer> enchants;

    public Enchant(HashMap<CEnchantment, Integer> enchants){
        this.enchants = enchants;
    }

    public boolean hasEnchantment(CEnchantment enchantment) {
        return this.enchants.containsKey(enchantment);
    }

    public HashMap<CEnchantment, Integer> getFullEnchantments() {
        return this.enchants;
    }

    public Set<CEnchantment> getEnchantments() {
        return this.enchants.keySet();
    }

    public Integer getLevel(CEnchantment enchantment) {
        return this.enchants.get(enchantment);
    }

    public void addEnchantments() {
        this.enchants.put(enchantment, level);
    }

    public void addEnchantment(CEnchantment enchantment, Integer level) {
        this.enchants.put(enchantment, level);
    }

    public void removeEnchantment(CEnchantment enchantment) {
        this.enchants.remove(enchantment);
    }

}
