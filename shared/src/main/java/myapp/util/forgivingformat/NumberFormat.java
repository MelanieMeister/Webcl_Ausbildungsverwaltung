package myapp.util.forgivingformat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;


public abstract class NumberFormat<T extends Number> implements ForgivingFormat<T> {
    public  static final NumberFormat<Integer> INTEGER = new NumberFormat<Integer>() {
        @Override
        public Integer convertToValue(String userInput) {
            long value = Math.round(eval(userInput));
            if (value > Integer.MAX_VALUE) {
                value = Integer.MAX_VALUE;
            } else if (value < Integer.MIN_VALUE) {
                value = Integer.MIN_VALUE;
            }
            return (int) value;
        }
    };

    public  static final NumberFormat<Long> LONG = new NumberFormat<Long>() {
        @Override
        public Long convertToValue(String userInput) {
            return Math.round(eval(userInput));
        }
    };

    public  static final NumberFormat<Float> FLOAT = new NumberFormat<Float>() {
        @Override
        public Float convertToValue(String userInput) {
            double value = eval(userInput);
            if (value > Float.MAX_VALUE) {
                value = Float.MAX_VALUE;
            } else if (value < Float.MIN_VALUE) {
                value = Float.MIN_VALUE;
            }
            return (float) value;
        }
    };

    public  static final NumberFormat<Double> DOUBLE = new NumberFormat<Double>() {
        @Override
        public Double convertToValue(String userInput) {
            return eval(userInput);
        }
    };

    private static Pattern CONVERTIBLE_PATTERN = Pattern.compile("^$|[[0-9]+\\-*%/.'_,]+");

    @Override
    public boolean isConvertible(String userInput) {
        return CONVERTIBLE_PATTERN.matcher(userInput).matches();
    }
    

    protected double eval(String userInput) {
        return doEval(preProcessUserInput(userInput));
    }

    private String preProcessUserInput(String userInput){
        userInput = userInput.replaceAll("['_]", "");
        userInput = userInput.replaceAll(",", ".");

        // remove trailing ops
        if(userInput.matches(".*[+\\-*]")){
            userInput = userInput.substring(0, userInput.length() - 1);
        }

        if (userInput.equals("")) {
            return "0";
        }
        //convert -- to +
        if (userInput.contains("--")) {
            userInput = userInput.replaceAll("--", "+");
        }

        //manage percent operations :: "%" gets converted to "/100" on last term
        if (userInput.contains("%")) {
            //numbers only
            if (!userInput.matches("\\d+\\.?")) {
                //terms with ops
                String[] terms = userInput.split("[-+*/]");
                String tmp = userInput;
                String[] ops = tmp.replaceAll("[^-+*/]", "").split("(?<=.)(?=.)");
                for (int i = 0; i < terms.length; i++) {
                    if (terms[i].contains("%") && i > 0) {
                        terms[i] = terms[i - 1] + "*" + terms[i].replace("%", "/100");
                    } else if (terms[i].contains("%") && i < 1) {
                        terms[i] = terms[i].replace("%", "/100");
                    }
                }
                userInput = merge(new ArrayList<>(Arrays.asList(terms)), new ArrayList<>(Arrays.asList(ops)));
            }
        }
        return userInput;
    }

    /**
     * Evaluates arithmetic String and returns result as double back to evalString()
     * code by https://stackoverflow.com/users/964243/boann
     *
     * @param str
     * @return Returns evaluated double value.
     */
    private double doEval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ')
                    nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length())
                    throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+'))
                        x += parseTerm(); // addition
                    else if (eat('-'))
                        x -= parseTerm(); // subtraction
                    else
                        return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*'))
                        x *= parseFactor(); // multiplication
                    else if (eat('/'))
                        x /= parseFactor(); // division
                    else
                        return x;
                }
            }

            double parseFactor() {
                if (eat('+'))
                    return parseFactor(); // unary plus
                if (eat('-'))
                    return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.')
                        nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z')
                        nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt"))
                        x = Math.sqrt(x);
                    else if (func.equals("sin"))
                        x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos"))
                        x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan"))
                        x = Math.tan(Math.toRadians(x));
                    else
                        throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^'))
                    x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    /**
     * Converts two ArrayLists alternating into one string
     *
     * @param terms ArrayList with Terms to merge with b
     * @param ops   ArrayList with Operators
     * @return Returns new String of merged terms and ops
     */
    private String merge(ArrayList terms, ArrayList ops) {
        int c1 = 0, c2 = 0;
        ArrayList<String> res = new ArrayList<>();

        while (c1 < terms.size() || c2 < ops.size()) {
            if (c1 < terms.size())
                res.add((String) terms.get(c1++));
            if (c2 < ops.size())
                res.add((String) ops.get(c2++));
        }
        StringBuilder sb = new StringBuilder();
        for (String s : res) {
            sb.append(s);
        }
        return sb.toString();
    }
}
