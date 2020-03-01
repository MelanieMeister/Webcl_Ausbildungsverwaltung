package myapp.presentationmodel;

import java.beans.PropertyChangeListener;

import java.util.List;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.BasePresentationModel;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.Tag;

import myapp.util.AttributeDescription;

public interface PmMixin {

    default <E extends BasePresentationModel> E getProxyPM(PMDescription pmDescription) {
        return (E) getDolphin().getAt(pmDescription.getProxyPmId());
    }

    default <E extends BasePresentationModel> List<E> all(PMDescription pmDescription){
        return getDolphin().findAllPresentationModelsByType(pmDescription.pmName());
    }


    default <T> PropertyChangeListener addListener(Attribute attribute, DolphinAttributeListener<T> listener){
        PropertyChangeListener propertyChangeListener = evt -> listener.changed(attribute,
                                                                                (T) evt.getOldValue(),
                                                                                (T) evt.getNewValue());
        attribute.addPropertyChangeListener(Attribute.VALUE, propertyChangeListener);
        try{
            listener.changed(attribute, (T) attribute.getValue(), (T) attribute.getValue());
        }catch (ClassCastException e){
            Long l;
            Integer i;
            if(attribute.getValue().toString().equals("")){
                i=0;
            }else{
                i =  Integer.parseInt((attribute.getValue().toString())); //example

            }
            l = Long.valueOf(i.longValue());

            try{
                listener.changed(attribute,(T) l,(T)l);
            }catch (ClassCastException e2){
                listener.changed(attribute,(T) l.toString(),(T)l.toString());
            }

        }


        return propertyChangeListener;
    }


    default <T> T getValue(BasePresentationModel pm, AttributeDescription attributeDescription){
        return getValue(pm, attributeDescription, Tag.VALUE);
    }

    default <T> void setValue(BasePresentationModel pm, AttributeDescription attributeDescription, T newValue){
         pm.getAt(attributeDescription.name()).setValue(newValue);
    }

    default <T> T getValue(BasePresentationModel pm, AttributeDescription attributeDescription, Tag tag){
        return (T)pm.getAt(attributeDescription.name(), tag).getValue();
    }

    default <A extends Attribute> A getSelectedIDAttribute(PMDescription pmDescription){
        return (A)getProxyPM(PMDescription.APPLICATION_STATE).getAt(pmDescription.selectedIdPropertyName());
    }

    Dolphin getDolphin();

    @FunctionalInterface
    public interface DolphinAttributeListener<T> {
        void changed(Attribute attribute, T oldValue, T newValue);
    }
}
