import java.util.Scanner;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        String vsfPath = null;
        String scriptPath = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--vfs")) {
                if (i + 1 >= args.length) {
                    System.out.println("Ошибка: отсутствие пути");
                    return;
                }
                vsfPath = args[++i];
            } else if (args[i].equals("--script")) {
                if (i + 1 >= args.length) {
                    System.out.println(
                            "Ошибка: отсутствие пути");
                    return;
                }
                scriptPath = args[++i];
            } else {
                System.out.println("Ошибка: неизвестный параметр" + args[i]);
                return;
            }
        }
        String vfsName = "default";

        if (vsfPath != null) {
            vfsName = vsfPath;
        }

        System.out.println("VFS: " + vsfPath);
        System.out.println("Script: " + scriptPath);

        Shell shell = new Shell(vfsName);
        if (scriptPath != null) {
            shell.runScript(Path.of(scriptPath));
        } else {
            shell.run(new Scanner(System.in));
        }
    }
}
