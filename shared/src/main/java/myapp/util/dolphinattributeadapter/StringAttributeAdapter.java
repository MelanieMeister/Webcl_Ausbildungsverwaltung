package myapp.util.dolphinattributeadapter;

import javafx.beans.property.SimpleStringProperty;

import org.opendolphin.core.Attribute;

/**
 * Adapter for a Dolphin Attribute of type String.
 */
public class StringAttributeAdapter extends SimpleStringProperty {
    private final ObjectAttributeAdapter<String> wrapper;

    public StringAttributeAdapter(Attribute attribute) {
        wrapper = new ObjectAttributeAdapter<>(attribute);
        wrapper.addListener((observable, oldValue, newValue) -> fireValueChangedEvent());
    }

    @Override
    public String getName() {
        return wrapper.getName();
    }

    @Override
    public void set(String newValue) {
        wrapper.set(newValue);
    }

    @Override
    public void setValue(String v) {
        set(v);
    }

    @Override
    public String get() {
        return wrapper.get();
    }

    @Override
    public String getValue() {
        return get();
    }
}
