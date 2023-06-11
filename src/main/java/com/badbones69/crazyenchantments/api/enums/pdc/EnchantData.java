package com.badbones69.crazyenchantments.api.enums.pdc;

import org.apache.commons.lang3.SerializationUtils;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jline.utils.Log;

import java.io.*;

public class EnchantData implements PersistentDataType<byte[], Enchant> {
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<Enchant> getComplexType() {
        return Enchant.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull Enchant complex, @NotNull PersistentDataAdapterContext context) {
        return SerializationUtils.serialize(complex);
    }

    @Override
    public @NotNull Enchant fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        try {
            InputStream is = new ByteArrayInputStream(primitive);
            ObjectInputStream o = new ObjectInputStream(is);
            return (Enchant) o.readObject();
        } catch(IOException | ClassNotFoundException e) {
            Log.error(e);
        }
        return null;
    }

}
