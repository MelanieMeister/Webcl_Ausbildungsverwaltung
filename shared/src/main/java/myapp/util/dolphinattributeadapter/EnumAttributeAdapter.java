package myapp.util.dolphinattributeadapter;

import javafx.beans.property.SimpleObjectProperty;

import org.opendolphin.core.Attribute;

public class EnumAttributeAdapter<T extends Enum<T>> extends SimpleObjectProperty<T> {
    private final ObjectAttributeAdapter<T> wrapper;
    private final Class<T>                  clazz;


    public EnumAttributeAdapter(Attribute attribute, Class<T> clazz) {
        wrapper = new ObjectAttributeAdapter<>(attribute, new EnumConverter<>(clazz));
        wrapper.addListener((observable, oldValue, newValue) -> fireValueChangedEvent());
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return wrapper.getName();
    }

    @Override
    public void set(T newValue) {
        wrapper.set(newValue);
    }

    @Override
    public T get() {
        return wrapper.getValue();
    }

    @Override
    public T getValue() {
        return get();
    }

    private final class EnumConverter<E extends Enum<E>> implements AttributeValueConverter<E> {
        private final Class<E> enumClass;

        EnumConverter(Class<E> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public Object toAttributeValue(E value) {
            return value.name();
        }

        @Override
        public E toPropertyValue(Object value) {
            return (value == null || ((String)value).isEmpty()) ?
                   null :
                   Enum.valueOf(enumClass, (String) value);
        }
    }


}
