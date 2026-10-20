import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Shell {
    private final String vfsName;

    public Shell(String vfsName) {
        this.vfsName = vfsName;
    }

    public void run(Scanner scanner) {
        while (true) {
            System.out.print(vfsName + "> ");

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
            System.out.println("Ошибка: не удалось прочитать скрипт " + scriptPath);
            return;
        }

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            System.out.println(vfsName + "> " + line);

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

        String command = parts.get(0);
        List<String> arguments = parts.subList(1, parts.size());

        switch (command) {
            case "ls":
                System.out.println("ls " + arguments);
                break;

            case "cd":
                System.out.println("cd " + arguments);
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
}