package io.github.aratakileo.greenhouses;

import io.github.aratakileo.greenhouses.util.Classes;
import net.minecraft.world.inventory.ContainerData;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class ContainerAutoData implements ContainerData {
    public final ArrayList<Field> fields = new ArrayList<>();

    public ContainerAutoData() {
        for (final var field: getClass().getDeclaredFields()) {
            if (hasUnsupportedType(field))
                throw new RuntimeException("Compound data field `%s` has unsupported type `%s`".formatted(
                        Classes.getFieldView(field),
                        field.getType()
                ));

            if (field.isAnnotationPresent(DataField.class))
                fields.add(field);
        }
    }

    @Override
    public int get(int index) {
        try {
            final var field = fields.get(index);

            if (field.getType() == boolean.class)
                return field.getBoolean(this) ? 1 : 0;

            return field.getInt(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(int index, int value) {
        try {
            final var field = fields.get(index);

            if (field.getType() == boolean.class)
                field.setBoolean(this, value == 1);
            else field.setInt(this, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount() {
        return fields.size();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DataField {}

    public static boolean hasUnsupportedType(@NotNull Field field) {
        return field.getType() != boolean.class && field.getType() != int.class;
    }
}
