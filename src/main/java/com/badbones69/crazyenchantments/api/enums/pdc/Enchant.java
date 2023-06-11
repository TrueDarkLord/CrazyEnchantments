package com.badbones69.crazyenchantments.api.enums.pdc;

import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class Enchant implements PersistentDataType<CEnchantment, Integer> {



    @Override
    public @NotNull Class<CEnchantment> getPrimitiveType() {
        return CEnchantment.class;
    }

    @Override
    public @NotNull Class<Integer> getComplexType() {
        return Integer.class;
    }

    @Override
    public @NotNull CEnchantment toPrimitive(@NotNull Integer complex, @NotNull PersistentDataAdapterContext context) {
        return null;
    }

    @Override
    public @NotNull Integer fromPrimitive(@NotNull CEnchantment primitive, @NotNull PersistentDataAdapterContext context) {
        return null;
    }


}
