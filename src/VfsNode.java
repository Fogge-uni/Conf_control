import java.util.List;

public class VfsNode {
    private final String name;
    private final boolean directory;
    private final byte[] content;
    private final List<VfsNode> children;

    public VfsNode(String name, boolean directory,
                   byte[] content, List<VfsNode> children) {
        this.name = name;
        this.directory = directory;
        this.content = content;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return directory;
    }

    public byte[] getContent() {
        return content;
    }

    public List<VfsNode> getChildren() {
        return children;
    }
}
