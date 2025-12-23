import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UIHelper {

    public static void printBorder(String title) {
        System.out.print("  ");
        for (int i = 0; i < 76; i++) System.out.print("=");
        System.out.println();

        if (title != null && !title.isEmpty()) {
            int padding = (76 - title.length()) / 2;
            System.out.print("  |");
            for (int i = 0; i < padding; i++) System.out.print(" ");
            System.out.print(title);
            for (int i = padding + title.length(); i < 76; i++) System.out.print(" ");
            System.out.println("|");
            System.out.print("  ");
            for (int i = 0; i < 76; i++) System.out.print("=");
            System.out.println();
        }

        System.out.print("  ");
        for (int i = 0; i < 10; i++) System.out.print("~");
        System.out.println();
    }

    public static void printSeparator() {
        System.out.print("  ");
        for (int i = 0; i < 76; i++) System.out.print("-");
        System.out.println();
    }

    public static void printFileTableHeader() {
        System.out.print("  " + String.format("%-4s", "N"));
        System.out.printf("%-40s", "Name");
        System.out.printf("%-15s", "Size");
        System.out.println("Type");
        printSeparator();
    }

    public static void printFileRow(int index, String name, boolean isDir, long size) {
        if (index > 0) {
            System.out.print("  " + String.format("%-4s", index));
        } else {
            System.out.print("  " + String.format("%-4s", " ")); // пустой номер для папок
        }

        String displayName = name.length() > 36 ? name.substring(0, 33) + "..." : name;
        System.out.printf("%-40s", displayName);

        if (isDir) {
            System.out.printf("%-15s", "<DIR>");
            System.out.println("Folder");
        } else {
            String sizeStr = size < 1024 ? size + " B" :
                    size < 1024 * 1024 ? String.format("%.1f KB", size / 1024.0) :
                            String.format("%.1f MB", size / (1024.0 * 1024.0));
            System.out.printf("%-15s", sizeStr);
            System.out.println("File");
        }
    }

    public static void displayFileList(String currentDirectory) {
        printBorder("СОДЕРЖИМОЕ КАТАЛОГА");
        System.out.println("  Путь: " + currentDirectory);

        List<List<Utils.FileInfo>> groups = Utils.getGroupedFiles(currentDirectory);

        int totalElements = groups.stream().mapToInt(List::size).sum();
        System.out.println("  Элементов: " + totalElements);
        printSeparator();

        if (totalElements == 0) {
            System.out.println("  Папка пуста");
            printSeparator();
            return;
        }

        printFileTableHeader();

        int globalFileNumber = 1; // сплошная нумерация только для файлов

        printGroupWithGlobalNumbers("АРХИВЫ > 100 МБ", groups.get(0), globalFileNumber);
        globalFileNumber += groups.get(0).size();

        printGroupWithGlobalNumbers("ФАЙЛЫ > 100 МБ", groups.get(1), globalFileNumber);
        globalFileNumber += groups.get(1).size();

        printGroupWithGlobalNumbers("АРХИВЫ ≤ 100 МБ", groups.get(2), globalFileNumber);
        globalFileNumber += groups.get(2).size();

        printGroupWithGlobalNumbers("ОСТАЛЬНЫЕ ФАЙЛЫ И ПАПКИ", groups.get(3), globalFileNumber);

        printSeparator();
    }

    private static void printGroupWithGlobalNumbers(String title, List<Utils.FileInfo> files, int startNumber) {
        if (files.isEmpty()) return;

        System.out.println("  >>> " + title + " (" + files.size() + ")");

        int currentNumber = startNumber;
        for (Utils.FileInfo fi : files) {
            int numberToPrint = fi.isDirectory ? 0 : currentNumber;
            printFileRow(numberToPrint, fi.name, fi.isDirectory, fi.size);
            if (!fi.isDirectory) {
                currentNumber++;
            }
        }
    }

    public static void displaySimplifiedMenu() {
        printBorder("FILEHELPER");

        System.out.println("  1.  Удалить файл");
        System.out.println("  2.  Перейти в папку");
        printSeparator();
        System.out.println("  99. Выход");
        printBorder("");

        System.out.println("  (╯°□°)╯︵ ┻━┻");
    }
}