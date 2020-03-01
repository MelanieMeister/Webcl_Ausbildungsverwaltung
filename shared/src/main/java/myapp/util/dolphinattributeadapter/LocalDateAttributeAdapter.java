package myapp.util.dolphinattributeadapter;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.opendolphin.core.Attribute;

import java.time.LocalDate;
import java.util.Date;

public class LocalDateAttributeAdapter extends SimpleObjectProperty {
    private final ObjectAttributeAdapter<LocalDate> wrapper;

    public LocalDateAttributeAdapter(Attribute attribute) {
        wrapper = new ObjectAttributeAdapter<>(attribute);
        wrapper.addListener((observable, oldValue, newValue) -> fireValueChangedEvent());
    }
    @Override
    public String getName() {
        return wrapper.getName();
    }


    public void set(LocalDate newValue) {
        wrapper.set(newValue);
    }


    public void setValue(Date v) {
        set(v.toString());
    }


    public LocalDate get() {
        return wrapper.getValue();
    }

    @Override
    public LocalDate getValue() {
        return get();
    }

}
