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

            try {
                List<String> parts = Parser.parse(scanner.nextLine());
                if (parts.isEmpty()) {
                    continue;
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
                            return;
                        }
                        System.out.println(ERROR + "exit не принимает аргументы");
                        break;
                    default:
                        System.out.println(ERROR + "неизвестная команда");
                }
            } catch (IllegalArgumentException exception) {
                System.out.println(ERROR + exception.getMessage());
            }
        }
    }
}



