import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String vfsPath = null;
        String scriptPath = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--vfs")) {
                if (i + 1 >= args.length) {
                    System.out.println("Ошибка: укажите путь после --vfs");
                    return;
                }
                vfsPath = args[++i];
            } else if (args[i].equals("--script")) {
                if (i + 1 >= args.length) {
                    System.out.println("Ошибка: укажите путь после --script");
                    return;
                }
                scriptPath = args[++i];
            } else {
                System.out.println("Ошибка: неизвестный параметр " + args[i]);
                return;
            }
        }

        VirtualFileSystem vfs;

        try {
            if (vfsPath == null) {
                vfs = VirtualFileSystem.createDefault();
            } else {
                vfs = VirtualFileSystem.load(Path.of(vfsPath));
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Ошибка загрузки VFS: " + e.getMessage());
            return;
        }

        String motd = vfs.getMotd();

        if (motd != null) {
            System.out.print(motd);
            if (!motd.endsWith("\n")) {
                System.out.println();
            }
        }

        String vfsName = vfsPath == null ? "default" : vfsPath;
        Shell shell = new Shell(vfsName);

        if (scriptPath == null) {
            shell.run(new Scanner(System.in));
        } else {
            shell.runScript(Path.of(scriptPath));
        }
    }
}