package myapp.util.forgivingformat;


public interface ForgivingFormat<T> {
    ForgivingFormat<Integer> STRICT_INTEGER = new ForgivingFormat<Integer>() {
        @Override
        public boolean isConvertible(String userInput) {
            return false;
        }

        @Override
        public Integer convertToValue(String userInput) {
            return null;
        }
    };

    ForgivingFormat<String> SIMPLE_STRING = new ForgivingFormat<String>() {
        @Override
        public boolean isConvertible(String userInput) {
            return false;
        }

        @Override
        public String convertToValue(String userInput) {
            return userInput;
        }
    };

    ForgivingFormat<Boolean> BOOLEAN = new ForgivingFormat<Boolean>() {
        @Override
        public boolean isConvertible(String userInput) {
            return true;
        }

        @Override
        public Boolean convertToValue(String userInput) {
            return userInput.equals("true");
        }
    };

    boolean isConvertible(String userInput);

    T convertToValue(String userInput);

}
