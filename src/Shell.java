import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Shell {
    private static final String ERROR = "Ошибка: ";
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
        try {
            List<String> lines = Files.readAllLines(scriptPath);

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                System.out.println(vfsName + ">" + line);

                if (execute(line)) {
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка прочтения скрипта");
        }
    }

    private boolean execute(String line) {
        try {
            List<String> parts = Parser.parse(line);
            if (parts.isEmpty()) {
                return false;
            }
            String command = parts.getFirst();
            List<String> arguments = parts.subList(1, parts.size());

            switch (command) {
                case "ls":
                    System.out.println(command + " " + arguments);
                    break;
                case "cd":
                    System.out.println(command + " " + arguments);
                    break;
                case "exit":
                    if (arguments.isEmpty()) {
                        return true;
                    }
                    System.out.println(ERROR + "exit не принимает аргументы");
                    break;
                default:
                    System.out.println(ERROR + "неизвестная команда");
            }
            return false;
        } catch (IllegalArgumentException exception) {
            System.out.println(ERROR + exception.getMessage());
            return false;
        }
    }
}



