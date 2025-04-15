package Evaluator;

import java.util.*;
import java.util.function.Function;

/**
 * Класс Evaluator позволяет вычислять арифметические выражения,
 * поддерживая переменные, базовые математические функции и операторы.
 * Реализация использует алгоритм перевода в обратную польскую нотацию (ОПН)
 * и стековую модель вычисления.
 */
public class Evaluator {

    /** Хранилище переменных и их значений */
    private final Map<String, Double> variables = new HashMap<>();

    /** Сканер для чтения значений переменных с консоли */
    private final Scanner scanner = new Scanner(System.in);

    /** Поддерживаемые операторы */
    private static final Set<String> OPERATORS = Set.of("+", "-", "*", "/", "^");

    /** Приоритеты операторов */
    private static final Map<String, Integer> PRECEDENCE = Map.of(
            "+", 1, "-", 1, "*", 2, "/", 2, "^", 3
    );

    /** Поддерживаемые математические функции */
    private static final Map<String, Function<Double, Double>> FUNCTIONS = Map.of(
            "sin", Math::sin,
            "cos", Math::cos,
            "tan", Math::tan,
            "sqrt", Math::sqrt,
            "abs", Math::abs
    );

    /**
     * Главный метод вычисления выражения.
     *
     * @param expression строка выражения
     * @return результат вычисления
     */
    public double evaluate(String expression) {
        List<String> tokens = parseTokens(expression.replaceAll("\\s+", ""));
        List<String> postfix = convertToPostfix(tokens);
        return evaluatePostfix(postfix);
    }

    /**
     * Разделяет строку выражения на токены (числа, переменные, функции, операторы).
     *
     * @param input строка выражения
     * @return список токенов
     */
    private List<String> parseTokens(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int i = 0;

        while (i < input.length()) {
            char ch = input.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                sb.setLength(0);
                while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                    sb.append(input.charAt(i++));
                }
                tokens.add(sb.toString());
            } else if (Character.isLetter(ch)) {
                sb.setLength(0);
                while (i < input.length() && Character.isLetter(input.charAt(i))) {
                    sb.append(input.charAt(i++));
                }
                tokens.add(sb.toString());
            } else if ("+-*/^(),".indexOf(ch) >= 0) {
                tokens.add(String.valueOf(ch));
                i++;
            } else {
                throw new IllegalArgumentException("Недопустимый символ: " + ch);
            }
        }

        return tokens;
    }

    /**
     * Преобразует токены выражения в обратную польскую нотацию.
     *
     * @param tokens список токенов
     * @return список токенов в ОПН
     */
    private List<String> convertToPostfix(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String token : tokens) {
            if (isNumber(token) || isVariable(token)) {
                output.add(token);
            } else if (FUNCTIONS.containsKey(token)) {
                stack.push(token);
            } else if (token.equals(",")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
            } else if (OPERATORS.contains(token)) {
                while (!stack.isEmpty() && OPERATORS.contains(stack.peek()) &&
                        PRECEDENCE.get(stack.peek()) >= PRECEDENCE.get(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) throw new IllegalArgumentException("Несогласованные скобки");
                stack.pop();
                if (!stack.isEmpty() && FUNCTIONS.containsKey(stack.peek())) {
                    output.add(stack.pop());
                }
            } else {
                throw new IllegalArgumentException("Неизвестный токен: " + token);
            }
        }

        while (!stack.isEmpty()) {
            if (stack.peek().equals("(")) throw new IllegalArgumentException("Несогласованные скобки");
            output.add(stack.pop());
        }

        return output;
    }

    /**
     * Вычисляет выражение в обратной польской нотации.
     *
     * @param tokens список токенов в ОПН
     * @return результат вычисления
     */
    private double evaluatePostfix(List<String> tokens) {
        Deque<Double> stack = new ArrayDeque<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isVariable(token)) {
                stack.push(getVariableValue(token));
            } else if (FUNCTIONS.containsKey(token)) {
                if (stack.isEmpty()) throw new IllegalArgumentException("Недостаточно аргументов для функции: " + token);
                double arg = stack.pop();
                stack.push(FUNCTIONS.get(token).apply(arg));
            } else if (OPERATORS.contains(token)) {
                if (stack.size() < 2) throw new IllegalArgumentException("Недостаточно операндов для операции: " + token);
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyOperator(token, a, b));
            }
        }

        if (stack.size() != 1) throw new IllegalArgumentException("Ошибка при вычислении выражения");
        return stack.pop();
    }

    /**
     * Применяет оператор к двум числам.
     *
     * @param op оператор
     * @param a первый аргумент
     * @param b второй аргумент
     * @return результат операции
     */
    private double applyOperator(String op, double a, double b) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) throw new IllegalArgumentException("Деление на ноль");
                yield a / b;
            }
            case "^" -> Math.pow(a, b);
            default -> throw new IllegalArgumentException("Неизвестная операция: " + op);
        };
    }

    /**
     * Проверяет, является ли токен числом.
     *
     * @param token токен
     * @return true, если это число
     */
    private boolean isNumber(String token) {
        return token.matches("\\d+(\\.\\d+)?");
    }

    /**
     * Проверяет, является ли токен переменной.
     *
     * @param token токен
     * @return true, если это переменная
     */
    private boolean isVariable(String token) {
        return token.matches("[a-zA-Z]+") && !FUNCTIONS.containsKey(token);
    }

    /**
     * Получает значение переменной, запрашивая у пользователя, если оно ещё не задано.
     *
     * @param var имя переменной
     * @return значение переменной
     */
    private double getVariableValue(String var) {
        if (!variables.containsKey(var)) {
            System.out.print("Введите значение переменной " + var + ": ");
            double value = scanner.nextDouble();
            variables.put(var, value);
        }
        return variables.get(var);
    }
}