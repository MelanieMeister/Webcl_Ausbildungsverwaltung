package myapp.util;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.Slot;

import myapp.presentationmodel.PMDescription;


public interface DTOMixin {

    default long createNewId() {
        return System.nanoTime();
    }

    default Slot createSlot(AttributeDescription att, Object value, long entityId) {
        return new Slot(att.name(), value, att.qualifier(entityId));
    }

    default DTO createDTO(PMDescription pmDescription, long entityId) {
        List<Slot> slots = Arrays.stream(pmDescription.getAttributeDescriptions())
                                 .map(attributeDescription -> createSlot(attributeDescription,
                                                                         attributeDescription.isEntityId() ? entityId : attributeDescription.getInitialValue(),
                                                                         entityId))
                                 .collect(Collectors.toList());
        return new DTO(slots);
    }

    default long entityId(DTO dto) {
        return entityId(dto.getSlots().get(0).getQualifier());
    }

    default long entityId(String pmId) {
        return Long.valueOf(pmId.split(":")[1]);
    }

    default DTO merge(DTO modifiedDTO, DTO persistedDTO) {
        for(Slot slot : persistedDTO.getSlots()){
            Slot modifiedSlot = getSlot(modifiedDTO, slot.getPropertyName());
            if(modifiedSlot != null){
                slot.setValue(modifiedSlot.getValue());
            }
        }
        return persistedDTO;
    }

    default DTO getDTO(long id, List<DTO> dtos) {
        return dtos.stream()
                   .filter(dto -> entityId(dto) == id)
                   .findAny()
                   .orElse(null);
    }

    default Slot getSlot(DTO dto, String propertyName){
        return dto.getSlots().stream()
                  .filter(slot -> slot.getPropertyName().equals(propertyName))
                  .findAny()
                  .orElse(null);
    }

    default Slot getSlot(DTO dto, AttributeDescription att) {
        return dto.getSlots().stream()
                  .filter(slot -> slot.getPropertyName().equals(att.name()))
                  .findAny()
                  .orElseThrow(NoSuchElementException::new);
    }

    default <T> T getValue(DTO dto, AttributeDescription att) {
        return (T) getSlot(dto, att).getValue();
    }

}
