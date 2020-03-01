package myapp.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import myapp.util.validators.DoubleRangeValidator;
import myapp.util.validators.IntegerRangeValidator;
import myapp.util.validators.RegexValidator;
import myapp.util.validators.StringLengthValidator;
import myapp.util.validators.Validator;


public final class Definition<T> {

    public static Definition<Long> ID() {
        return new Definition<Long>(ValueType.LONG)
                .valueSubType(ValueSubType.ID)
                .undoAble(false)
                .mandatory(true)
                .readOnly(true)
                .syntaxValidator(RegexValidator.forPositiveLong("NOT_AN_ID"))
                .formatPattern("%d")
                .converter(Long::parseLong);
    }

    public static Definition<Integer> INT() {
        return new Definition<Integer>(ValueType.INT)
                .syntaxValidator(RegexValidator.forFormattedInt("NOT_AN_INT"))
                .formatPattern("%,d")
                .converter(s -> (s ==  null || s.isEmpty()) ? 0 : Integer.parseInt(s.replaceAll("'", "")));
    }

    public static Definition<Integer> YEAR() {
        return new Definition<Integer>(ValueType.INT)
                .valueSubType(ValueSubType.YEAR)
                .syntaxValidator(RegexValidator.forPattern("^[0-9]{4}$", "NOT_A_YEAR"))
                .formatPattern("%d")
                .converter(Integer::parseInt)
                .valueValidators(IntegerRangeValidator.between(1800, 2050, "YEAR_OUT_OF_RANGE"));
    }
    public static Definition<LocalDate> LOCALDATE() {
        return new Definition<LocalDate>(ValueType.DATE)
                .valueSubType(ValueSubType.TEXT)
                .syntaxValidator(RegexValidator.forPattern("^[0-9]{4}$", "NOT_A_YEAR"))
                .formatPattern("d/M/y")
                .converter(LocalDate::parse);
    }
    public static Definition<Date> DATE() {
        return new Definition<Date>(ValueType.DATE)
                .valueSubType(ValueSubType.TEXT)
                .syntaxValidator(RegexValidator.forPattern("^[0-9]{4}$", "NOT_A_YEAR"))
                .formatPattern("d/M/y")
                .converter(Date::new);
    }


    public static Definition<Long> LONG() {
        return new Definition<Long>(ValueType.LONG)
                .syntaxValidator(RegexValidator.forFormattedLong("NOT_A_LONG"))
                .formatPattern("%,d")
                .converter(s -> Long.parseLong(s.replaceAll("'", "")));
    }

    public static Definition<Float> FLOAT() {
        return new Definition<Float>(ValueType.FLOAT)
                .syntaxValidator(RegexValidator.forFormattedFloatingPointNumber("NOT_A_FLOAT"))
                .formatPattern("%,.2f")
                .converter(s -> Float.parseFloat(s.replaceAll("'", "")));
    }

    public static Definition<Double> DOUBLE() {
        return new Definition<Double>(ValueType.DOUBLE)
                .syntaxValidator(RegexValidator.forFormattedFloatingPointNumber("NOT_A_DOUBLE"))
                .formatPattern("%,.0f")
                .converter(s -> s.isEmpty() ? 0 : Double.parseDouble(s.replaceAll("'", "")));
    }

   
    public static Definition<Double> LONGITUDE() {
        return new Definition<Double>(ValueType.DOUBLE)
                .valueSubType(ValueSubType.LONGITUDE)
                .syntaxValidator(RegexValidator.forPattern("^[-+]?[0-9]*\\.?[0-9]*$", "NOT_A_DOUBLE"))
                .formatPattern("%.8f")
                .converter(Double::parseDouble)
                .valueValidators(DoubleRangeValidator.between(-180.0, 180.0, "NOT_A_LONGITUDE"));
    }

    public static Definition<Double> LATITUDE() {
        return new Definition<Double>(ValueType.DOUBLE)
                .valueSubType(ValueSubType.LATITUDE)
                .syntaxValidator(RegexValidator.forPattern("^[-+]?[0-9]*\\.?[0-9]*$", "NOT_A_DOUBLE"))
                .formatPattern("%.8f")
                .converter(Double::parseDouble)
                .valueValidators(DoubleRangeValidator.between(-90.0, 90.0, "NOT_A_LATITUDE"));
    }

    public static Definition<String> STRING() {
        return new Definition<String>(ValueType.STRING)
                .syntaxValidator(RegexValidator.forAnything(""))
                .formatPattern("%s")
                .converter(s -> s)
                .valueValidators(StringLengthValidator.upTo(80, "STRING_TOO_LONG"));
    }

    public static Definition<String> TEXT() {
        return new Definition<String>(ValueType.STRING)
                .valueSubType(ValueSubType.TEXT)
                .syntaxValidator(RegexValidator.forAnything(""))
                .formatPattern("%s")
                .converter(s -> s);
    }



    public static Definition<String> IMAGE_URL() {
        return new Definition<String>(ValueType.STRING)
                .valueSubType(ValueSubType.LONG_STRING)
                .syntaxValidator(RegexValidator.forURL("NOT_AN_URL"))
                .formatPattern("%s")
                .converter(s -> s);
    }

    public static Definition<Boolean> BOOLEAN() {
        return new Definition<Boolean>(ValueType.BOOLEAN)
                .syntaxValidator(RegexValidator.forBoolean("NOT_A_BOOLEAN"))
                .formatPattern("%s")
                .converter(Boolean::parseBoolean);
    }

    public static Definition<Boolean> DIRTY_STATUS() {
        return new Definition<Boolean>(ValueType.BOOLEAN)
                .valueSubType(ValueSubType.DIRTY_STATUS)
                .syntaxValidator(RegexValidator.forBoolean("NOT_A_BOOLEAN"))
                .formatPattern("%s")
                .converter(Boolean::parseBoolean)
                .initialValue(false);
    }

    private final List<Validator<T>> allValidators = new ArrayList<>();

    private final ValueType valueType;

    private T initialValue;

    private ValueSubType valueSubType;

    private boolean initiallyMandatory = false;
    private boolean initiallyReadOnly  = false;

    private RegexValidator      syntaxValidator;
    private String              formatPattern;
    private Function<String, T> converter;

    private boolean isUndoAble = true;

    private Definition(ValueType valueType) {
        this.valueType = valueType;
        initialValue = (T) valueType.getInitialValue();
    }

    T getInitialValue(){
        return initialValue;
    }

    boolean isInitiallyReadOnly() {
        return initiallyReadOnly;
    }

    boolean isInitiallyMandatory() {
        return initiallyMandatory;
    }

    ValueType getValueType() {
        return valueType;
    }

    List<Validator<T>> getAllValidators() {
        return allValidators;
    }

    RegexValidator getSyntaxValidator() {
        return syntaxValidator;
    }

    String getFormatPattern() {
        return formatPattern;
    }

    Function<String, T> getConverter() {
        return converter;
    }

    boolean isUndoAble() {
        return isUndoAble;
    }


    public ValueSubType getValueSubType() {
        return valueSubType;
    }

    public Definition<T> valueSubType(ValueSubType subType){
        this.valueSubType = subType;

        return this;
    }

    public Definition<T> initialValue(T value) {
        this.initialValue = value;
        return this;
    }

    public Definition<T> mandatory(boolean initiallyMandatory) {
        this.initiallyMandatory = initiallyMandatory;
        return this;
    }

    public Definition<T> readOnly(boolean initiallyReadOnly) {
        this.initiallyReadOnly = initiallyReadOnly;
        return this;
    }

    public Definition<T> syntaxValidator(RegexValidator syntaxValidator) {
        this.syntaxValidator = syntaxValidator;

        return this;
    }

    public Definition<T> valueValidators(Validator<T>... validator) {
        allValidators.addAll(Arrays.asList(validator));
        return this;
    }

    public Definition<T> formatPattern(String formatPattern) {
        this.formatPattern = formatPattern;
        return this;
    }

    public Definition<T> converter(Function<String, T> converter) {
        this.converter = converter;
        return this;
    }

    public Definition<T> undoAble(boolean undoAble) {
        this.isUndoAble = undoAble;
        return this;
    }

}
