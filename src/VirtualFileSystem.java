import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class VirtualFileSystem {
    private final VfsNode root;

    private VirtualFileSystem(VfsNode root) {
        this.root = root;
    }

    public static VirtualFileSystem load(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException("файл не найден: " + path);
        }

        String json = Files.readString(path, StandardCharsets.UTF_8);
        Object data = JsonParser.parse(json);

        if (!(data instanceof Map<?, ?>)) {
            throw new IllegalArgumentException("корень JSON должен быть объектом");
        }

        VfsNode root = parseNode((Map<?, ?>) data);

        if (!root.isDirectory() || !root.getName().equals("/")) {
            throw new IllegalArgumentException(
                    "корень VFS должен быть каталогом с именем /");
        }

        return new VirtualFileSystem(root);
    }

    public static VirtualFileSystem createDefault() {
        byte[] content = "VFS по умолчанию\n".getBytes(StandardCharsets.UTF_8);

        List<VfsNode> children = new ArrayList<>();
        children.add(new VfsNode("motd", false, content, new ArrayList<>()));

        VfsNode root = new VfsNode("/", true, new byte[0], children);
        return new VirtualFileSystem(root);
    }

    public String getMotd() {
        for (VfsNode node : root.getChildren()) {
            if (node.getName().equals("motd") && !node.isDirectory()) {
                return new String(node.getContent(), StandardCharsets.UTF_8);
            }
        }

        return null;
    }

    private static VfsNode parseNode(Map<?, ?> data) {
        String name = getString(data, "name");
        String type = getString(data, "type");

        if (type.equals("file")) {
            String encoded = getString(data, "content");
            byte[] content;

            try {
                content = Base64.getDecoder().decode(encoded);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "неверный Base64 в файле " + name);
            }

            return new VfsNode(name, false, content, new ArrayList<>());
        }

        if (type.equals("directory")) {
            Object value = data.get("children");

            if (!(value instanceof List<?>)) {
                throw new IllegalArgumentException(
                        "у каталога " + name + " отсутствует массив children");
            }

            List<VfsNode> children = new ArrayList<>();

            for (Object child : (List<?>) value) {
                if (!(child instanceof Map<?, ?>)) {
                    throw new IllegalArgumentException(
                            "элемент children должен быть объектом");
                }

                children.add(parseNode((Map<?, ?>) child));
            }

            return new VfsNode(name, true, new byte[0], children);
        }

        throw new IllegalArgumentException("неизвестный тип узла: " + type);
    }

    private static String getString(Map<?, ?> data, String key) {
        Object value = data.get(key);

        if (!(value instanceof String)) {
            throw new IllegalArgumentException(
                    "поле " + key + " должно быть строкой");
        }

        return (String) value;
    }
}