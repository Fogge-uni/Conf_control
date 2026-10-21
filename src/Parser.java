import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static final char QUOTE = '"';

    public static List<String> parse (String line){
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char symbol = line.charAt(i);

            if (symbol == QUOTE) {
                inQuotes = !inQuotes;
            } else if (Character.isWhitespace(symbol) && !inQuotes) {
                if (!current.isEmpty()) {
                    result.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(symbol);
            }
        }

        if (inQuotes) {
            throw new IllegalArgumentException("кавычки не закрыты");
        }

        if (!current.isEmpty()) {
            result.add(current.toString());

        }
        return result;
    }
}

