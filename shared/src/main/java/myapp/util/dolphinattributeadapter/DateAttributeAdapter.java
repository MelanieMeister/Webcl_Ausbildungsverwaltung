package myapp.util.dolphinattributeadapter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.opendolphin.core.Attribute;

import java.util.Date;

public  class DateAttributeAdapter extends SimpleObjectProperty {
    private final ObjectAttributeAdapter<Date> wrapper;

    public DateAttributeAdapter(Attribute attribute) {
        wrapper = new ObjectAttributeAdapter<>(attribute);
        wrapper.addListener((observable, oldValue, newValue) -> fireValueChangedEvent());
    }
    @Override
    public String getName() {
        return wrapper.getName();
    }


    public void set(Date newValue) {
        wrapper.set(newValue);
    }


    public void setValue(Date v) {
        set(v.toString());
    }


    public Date get() {
        return wrapper.getValue();
    }

    @Override
    public Date getValue() {
        return get();
    }

}
