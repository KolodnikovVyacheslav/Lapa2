package org.example;

import Evaluator.Evaluator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Evaluator evaluator = new Evaluator();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите арифметическое выражение (например: x + y - 10)");
        System.out.print("> ");
        String input = scanner.nextLine();

        try {
            double result = evaluator.evaluate(input);
            System.out.println("Результат: " + result);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}