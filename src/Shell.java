import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Shell {
    private final String vfsName;
    private final VirtualFileSystem vfs;
    private VfsNode currentDirectory;
    private final List<String> history;
    private final long startTime;

    public Shell(String vfsName, VirtualFileSystem vfs) {
        this.vfsName = vfsName;
        this.vfs = vfs;
        this.currentDirectory = vfs.getRoot();
        this.history = new ArrayList<>();
        this.startTime = System.currentTimeMillis();
    }

    public void run(Scanner scanner) {
        while (true) {
            System.out.print(vfsName + ":" +
                    vfs.getPath(currentDirectory) + "> ");

            if (!scanner.hasNextLine()) {
                return;
            }

            if (execute(scanner.nextLine())) {
                return;
            }
        }
    }

    public void runScript(Path scriptPath) {
        List<String> lines;

        try {
            lines = Files.readAllLines(scriptPath);
        } catch (IOException e) {
            System.out.println(
                    "Ошибка: не удалось прочитать скрипт " + scriptPath);
            return;
        }

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            System.out.println(vfsName + ":" +
                    vfs.getPath(currentDirectory) + "> " + line);

            if (execute(line)) {
                return;
            }
        }
    }

    private boolean execute(String line) {
        List<String> parts;

        try {
            parts = Parser.parse(line);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return false;
        }

        if (parts.isEmpty()) {
            return false;
        }

        history.add(line);

        String command = parts.get(0);
        List<String> arguments = parts.subList(1, parts.size());

        switch (command) {
            case "ls":
                ls(arguments);
                break;

            case "cd":
                cd(arguments);
                break;

            case "uptime":
                uptime(arguments);
                break;

            case "whoami":
                whoami(arguments);
                break;

            case "history":
                showHistory(arguments);
                break;

            case "exit":
                if (arguments.isEmpty()) {
                    return true;
                }

                System.out.println("Ошибка: exit не принимает аргументы");
                break;

            default:
                System.out.println("Ошибка: неизвестная команда");
        }

        return false;
    }

    private void ls(List<String> arguments) {
        if (arguments.size() > 1) {
            System.out.println("Ошибка: ls принимает не более одного пути");
            return;
        }

        VfsNode directory = currentDirectory;

        if (!arguments.isEmpty()) {
            try {
                directory = vfs.find(arguments.get(0), currentDirectory);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
                return;
            }
        }

        if (!directory.isDirectory()) {
            System.out.println(directory.getName());
            return;
        }

        for (VfsNode child : directory.getChildren()) {
            System.out.println(child.getName());
        }
    }

    private void cd(List<String> arguments) {
        if (arguments.size() > 1) {
            System.out.println("Ошибка: cd принимает не более одного пути");
            return;
        }

        if (arguments.isEmpty()) {
            currentDirectory = vfs.getRoot();
            return;
        }

        try {
            VfsNode node = vfs.find(arguments.get(0), currentDirectory);

            if (!node.isDirectory()) {
                System.out.println("Ошибка: это не каталог");
                return;
            }

            currentDirectory = node;
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void uptime(List<String> arguments) {
        if (!arguments.isEmpty()) {
            System.out.println("Ошибка: uptime не принимает аргументы");
            return;
        }

        long seconds = (System.currentTimeMillis() - startTime) / 1000;

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        System.out.println("Время работы: " + hours + " ч. " +
                minutes + " мин. " + remainingSeconds + " сек.");
    }

    private void whoami(List<String> arguments) {
        if (!arguments.isEmpty()) {
            System.out.println("Ошибка: whoami не принимает аргументы");
            return;
        }

        System.out.println(System.getProperty("user.name"));
    }

    private void showHistory(List<String> arguments) {
        if (!arguments.isEmpty()) {
            System.out.println("Ошибка: history не принимает аргументы");
            return;
        }

        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + "  " + history.get(i));
        }
    }
}