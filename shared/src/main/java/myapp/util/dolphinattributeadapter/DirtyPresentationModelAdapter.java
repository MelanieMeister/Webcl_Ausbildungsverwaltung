package myapp.util.dolphinattributeadapter;

import java.lang.ref.WeakReference;

import javafx.beans.property.ReadOnlyBooleanPropertyBase;

import org.opendolphin.core.PresentationModel;


public class DirtyPresentationModelAdapter extends ReadOnlyBooleanPropertyBase {
    private final WeakReference<PresentationModel> pmRef;
    private final String                           name;

    public DirtyPresentationModelAdapter(PresentationModel presentationModel) {
        this.pmRef = new WeakReference<>(presentationModel);
        this.name = presentationModel.getId();
        presentationModel.addPropertyChangeListener(PresentationModel.DIRTY_PROPERTY, propertyChangeEvent -> fireValueChangedEvent());
    }

    @Override
    public Object getBean() {
        return pmRef.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean get() {
        PresentationModel pm = pmRef.get();
        return pm != null && pm.isDirty();
    }

    @Override
    public Boolean getValue() {
        return get();
    }
}
