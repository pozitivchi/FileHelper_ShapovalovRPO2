import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FileHelper {

    private static final Scanner scanner = new Scanner(System.in);
    private static String currentDirectory = ".";

    public static void main(String[] args) {
        while (true) {
            clearScreen();
            UIHelper.displayFileList(currentDirectory);

            UIHelper.displaySimplifiedMenu();

            System.out.print("\n  Ваш выбор: ");
            int choice = readChoice();

            if (choice == 99) {
                handleExit();
                break;
            }

            handleMenuChoice(choice);
        }
    }

    private static int readChoice() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        } finally {
            scanner.nextLine();
        }
    }

    private static void handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                handleDeleteFile();
                break;
            case 2:
                handleNavigateDirectory();
                break;
            default:
                System.out.println("\n  ❌ Неверный выбор! Попробуйте снова.");
                pressEnterToContinue();
        }
    }

    private static void handleDeleteFile() {
        List<Utils.FileInfo> allFiles = Utils.getAllFilesInOrder(currentDirectory);

        long fileCount = allFiles.stream().filter(fi -> !fi.isDirectory).count();
        if (fileCount == 0) {
            System.out.println("\n  Нет файлов для удаления!");
            pressEnterToContinue();
            return;
        }

        System.out.print("\n  Выберите номер файла для удаления: ");
        int selectedNumber;
        try {
            selectedNumber = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("  Неверный ввод!");
            pressEnterToContinue();
            return;
        }

        if (selectedNumber < 1 || selectedNumber > fileCount) {
            System.out.println("  Неверный номер!");
            pressEnterToContinue();
            return;
        }

        // Находим файл по номеру
        int currentFileNum = 1;
        Utils.FileInfo targetFile = null;
        for (Utils.FileInfo fi : allFiles) {
            if (!fi.isDirectory) {
                if (currentFileNum == selectedNumber) {
                    targetFile = fi;
                    break;
                }
                currentFileNum++;
            }
        }

        if (targetFile == null) {
            System.out.println("  Ошибка: файл не найден.");
            pressEnterToContinue();
            return;
        }

        String fullPath = currentDirectory + File.separator + targetFile.name;

        // Подтверждение
        System.out.println("\n  Файл: " + targetFile.name);
        System.out.println("  Размер: " + formatSize(targetFile.size));
        System.out.print("\n  Вы действительно хотите удалить этот файл? (y/n): ");

        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y") && !confirm.equals("yes") && !confirm.equals("да")) {
            System.out.println("  Удаление отменено.");
            pressEnterToContinue();
            return;
        }


        deleteFile(fullPath);
        pressEnterToContinue();
    }


    private static void deleteFile(String filepath) {
        try {
            Files.deleteIfExists(Paths.get(filepath));
            System.out.println("\n  Файл успешно удалён!");
        } catch (DirectoryNotEmptyException e) {
            System.out.println("\n  Ошибка: папка не пуста (но вы и не должны её удалять).");
        } catch (AccessDeniedException e) {
            System.out.println("\n  Ошибка: доступ запрещён.");
        } catch (Exception e) {
            System.out.println("\n  Ошибка при удалении: " + e.getMessage());
        }
    }

    private static String formatSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024L * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }

    private static void handleNavigateDirectory() {
        System.out.print("\n  Введите путь: ");
        String input = scanner.nextLine().trim();

        File newDir;

        if (input.isEmpty()) {
            pressEnterToContinue();
            return;
        }

        if (input.equals("..")) {
            File cur = new File(currentDirectory);
            File parent = cur.getParentFile();
            newDir = (parent != null) ? parent : cur;
        } else {
            File attempt = new File(input);
            if (attempt.isAbsolute()) {
                newDir = attempt;
            } else {
                newDir = new File(currentDirectory, input);
            }
        }

        if (newDir.isDirectory() && newDir.exists()) {
            currentDirectory = newDir.getAbsolutePath();
            System.out.println("  Перешли в: " + currentDirectory);
        } else {
            System.out.println("  Папка не найдена или недоступна!");
        }

        pressEnterToContinue();
    }

    private static void handleExit() {
        clearScreen();
        UIHelper.printBorder("ВЫХОД");
        System.out.println("  Спасибо за использование ФайлХелпера");
        System.out.println("  До свидания!");
        UIHelper.printBorder("");
    }

    private static void pressEnterToContinue() {
        System.out.print("\n  Нажмите Enter для продолжения...");
        scanner.nextLine();
    }

    private static void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Ошибка очистки: " + e.getMessage());
        }
    }
}