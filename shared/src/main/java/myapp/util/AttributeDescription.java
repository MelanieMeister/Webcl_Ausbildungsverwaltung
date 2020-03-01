package myapp.util;

import java.util.List;
import java.util.Locale;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.BasePresentationModel;

import myapp.presentationmodel.PMDescription;
import myapp.util.validators.RegexValidator;
import myapp.util.validators.ValidationResult;
import myapp.util.validators.Validator;


public interface AttributeDescription<T> {
    Locale CH = new Locale("de", "CH");

    String name();

    Definition<T> getDefinition();

    PMDescription getPMDescription();

    default Attribute of(BasePresentationModel pm){
        return pm.getAt(name());
    }

    default T getValueFrom(BasePresentationModel pm){
        return (T) pm.getAt(name()).getValue();
    }

    default void setValueIn(BasePresentationModel pm, T value){
         pm.getAt(name()).setValue(value);
    }

    default ValueType getValueType() {
        return getDefinition().getValueType();
    }

    default RegexValidator getSyntaxValidator() {
        return getDefinition().getSyntaxValidator();
    }

    default List<Validator<T>> allValidators() {
        return getDefinition().getAllValidators();
    }

    default String getFormatPattern() {
        return getDefinition().getFormatPattern();
    }

    default String convertToUserFacingString(T value) {
        return String.format(CH, getFormatPattern(), value);
    }

    default ValidationResult isValid(String userInput) {
        ValidationResult result = syntacticallyCorrect(userInput);
        if (result.getResult()) {
            T newValue = convertToValue(userInput);
            result = isValueValid(newValue);
        }
        return result;
    }

    default ValidationResult syntacticallyCorrect(String userInput) {
        return getSyntaxValidator().validate(userInput);
    }

    default ValidationResult isValueValid(T value) {
        boolean       isValid      = true;
        StringBuilder errorMessage = new StringBuilder("");

        for (Validator<T> validator : allValidators()) {
            ValidationResult result = validator.validate(value);
            if (!result.getResult()) {
                isValid = false;
                if (errorMessage.length() > 0) {
                    errorMessage.append("\n");
                }
                errorMessage.append(result.getErrorMessage());
            }
        }

        return new ValidationResult(isValid, errorMessage.toString());
    }


    default T convertToValue(String userInput) {
        return getDefinition().getConverter().apply(userInput);
    }

    default boolean isEntityId(){
        return ValueSubType.ID.equals(getDefinition().getValueSubType());
    }

    default boolean isDirtyStatus(){
        return ValueSubType.DIRTY_STATUS.equals(getDefinition().getValueSubType());
    }

    default T getInitialValue(){
        return getDefinition().getInitialValue();
    }

    default boolean isInitiallyReadOnly() {
        return getDefinition().isInitiallyReadOnly();
    }

    default boolean isInitiallyMandatory() {
        return getDefinition().isInitiallyMandatory();
    }

    default boolean isUndoAble() {
        return getDefinition().isUndoAble();
    }

    default String qualifier(long entityId) {
        return getPMDescription().entityName() + "." + name() + ":" + entityId;
    }

    default String labelQualifier() {
        return getPMDescription().entityName() + "." + name() + ":Label";
    }

    default ValueSubType getValueSubType(){
        return getDefinition().getValueSubType();
    }
}
