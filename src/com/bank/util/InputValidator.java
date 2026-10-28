package com.bank.util;

import java.util.Scanner;

/**
 * Robust CLI input reader and validator preventing crashes from unexpected input.
 */
public final class InputValidator {

    private InputValidator() {}

    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(AnsiColor.CYAN + prompt + AnsiColor.RESET);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println(AnsiColor.RED + "Please enter a number between " + min + " and " + max + "." + AnsiColor.RESET);
            } catch (NumberFormatException e) {
                System.out.println(AnsiColor.RED + "Invalid number format. Please try again." + AnsiColor.RESET);
            }
        }
    }

    public static double readPositiveDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(AnsiColor.CYAN + prompt + AnsiColor.RESET);
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value > 0.0) {
                    return value;
                }
                System.out.println(AnsiColor.RED + "Amount must be strictly greater than 0.00." + AnsiColor.RESET);
            } catch (NumberFormatException e) {
                System.out.println(AnsiColor.RED + "Invalid monetary amount. Please try again." + AnsiColor.RESET);
            }
        }
    }

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(AnsiColor.CYAN + prompt + AnsiColor.RESET);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println(AnsiColor.RED + "Input cannot be empty. Please try again." + AnsiColor.RESET);
        }
    }

    public static String readPinOrPassword(Scanner scanner, String prompt) {
        return readNonEmptyString(scanner, prompt);
    }
}
