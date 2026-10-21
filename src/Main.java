
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String[] paths = parseArguments(args);

        if (paths == null) {
            return;
        }

        VirtualFileSystem vfs;
        try {
            vfs = loadVfs(paths[0]);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Ошибка загрузки VFS: " + e.getMessage());
            return;
        }

        printMotd(vfs.getMotd());

        String vfsName = paths[0] == null ? "default" : paths[0];
        Shell shell = new Shell(vfsName);

        if (paths[1] == null) {
            shell.run(new Scanner(System.in));
        } else {
            shell.runScript(Path.of(paths[1]));
        }
    }

    private static String[] parseArguments(String[] args) {
        String vfsPath = null;
        String scriptPath = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--vfs")) {
                if (i + 1 >= args.length) {
                    System.out.println("Ошибка: укажите путь после --vfs");
                    return null;
                }
                vfsPath = args[++i];
            } else if (args[i].equals("--script")) {
                if (i + 1 >= args.length) {
                    System.out.println("Ошибка: укажите путь после --script");
                    return null;
                }
                scriptPath = args[++i];
            } else {
                System.out.println("Ошибка: неизвестный параметр " + args[i]);
                return null;
            }
        }

        return new String[]{vfsPath, scriptPath};
    }

    private static VirtualFileSystem loadVfs(String vfsPath)
            throws IOException {
        if (vfsPath == null) {
            return VirtualFileSystem.createDefault();
        }
        return VirtualFileSystem.load(Path.of(vfsPath));
    }

    private static void printMotd(String motd) {
        if (motd == null) {
            return;
        }

        System.out.print(motd);
        if (!motd.endsWith("\n")) {
            System.out.println();
        }
    }
}
