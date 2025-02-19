import java.util.Scanner;

class CalculatorModel {
    public double evaluate(String expression) {
        ExpressionParser parser = new ExpressionParser(expression);
        return parser.parse();
    }

    private static class ExpressionParser {
        private final String expression;
        private int pos = -1;
        private int ch;

        ExpressionParser(String expression) {
            this.expression = expression;
        }

        double parse() {
            nextChar();
            double result = parseExpression();
            if (pos < expression.length()) {
                throw new RuntimeException("Unexpected: " + (char) ch);
            }
            return result;
        }

        private void nextChar() {
            ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
        }

        private boolean eat(int charToEat) {
            while (ch == ' ') nextChar();
            if (ch == charToEat) {
                nextChar();
                return true;
            }
            return false;
        }

        private double parseExpression() {
            double result = parseTerm();
            while (true) {
                if (eat('+')) result += parseTerm();
                else if (eat('-')) result -= parseTerm();
                else return result;
            }
        }

        private double parseTerm() {
            double result = parseFactor();
            while (true) {
                if (eat('*')) result *= parseFactor();
                else if (eat('/')) result /= parseFactor();
                else return result;
            }
        }

        private double parseFactor() {
            if (eat('+')) return parseFactor();
            if (eat('-')) return -parseFactor();

            double result;
            int startPos = this.pos;
            if ((ch >= '0' && ch <= '9')  ch == '.') {
                while ((ch >= '0' && ch <= '9')  ch == '.') nextChar();
                result = Double.parseDouble(expression.substring(startPos, this.pos));
            } else if (eat('(')) {
                result = parseExpression();
                eat(')');
            } else {
                throw new RuntimeException("Unexpected: " + (char) ch);
            }

            if (eat('^')) result = Math.pow(result, parseFactor());

            return result;
        }
    }
}

class CalculatorView {
    private final Scanner scanner = new Scanner(System.in);

    public String getInput() {
        System.out.print("Введите математическое выражение: ");
        return scanner.nextLine();
    }

    public void displayResult(double result) {
        System.out.println("Результат: " + result);
    }
}

class CalculatorController {
    private final CalculatorModel model;
    private final CalculatorView view;

    public CalculatorController(CalculatorModel model, CalculatorView view) {
        this.model = model;
        this.view = view;
    }

    public void execute() {
        String expression = view.getInput();
        try {
            double result = model.evaluate(expression);
            view.displayResult(result);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}

public class CalculatorApp {
    public static void main(String[] args) {
        CalculatorModel model = new CalculatorModel();
        CalculatorView view = new CalculatorView();
        CalculatorController controller = new CalculatorController(model, view);

        controller.execute();
    }
}
