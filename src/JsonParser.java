import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {
    private final String text;
    private int position;

    private JsonParser(String text) {
        this.text = text;
    }

    public static Object parse(String text) {
        JsonParser parser = new JsonParser(text);
        Object result = parser.readValue();
        parser.skipSpaces();

        if (parser.position != text.length()) {
            throw new IllegalArgumentException("лишние данные в JSON");
        }

        return result;
    }

    private Object readValue() {
        skipSpaces();

        if (position >= text.length()) {
            throw new IllegalArgumentException("неожиданный конец JSON");
        }

        char symbol = text.charAt(position);

        if (symbol == '{') {
            return readObject();
        }

        if (symbol == '[') {
            return readArray();
        }

        if (symbol == '"') {
            return readString();
        }

        throw new IllegalArgumentException(
                "неверное значение JSON в позиции " + position);
    }

    private Map<String, Object> readObject() {
        Map<String, Object> result = new LinkedHashMap<>();
        position++;
        skipSpaces();

        if (take('}')) {
            return result;
        }

        while (true) {
            skipSpaces();

            if (position >= text.length() || text.charAt(position) != '"') {
                throw new IllegalArgumentException("ожидалось имя поля JSON");
            }

            String key = readString();
            skipSpaces();

            if (!take(':')) {
                throw new IllegalArgumentException("ожидалось двоеточие");
            }

            Object value = readValue();
            result.put(key, value);
            skipSpaces();

            if (take('}')) {
                return result;
            }

            if (!take(',')) {
                throw new IllegalArgumentException("ожидалась запятая");
            }
        }
    }

    private List<Object> readArray() {
        List<Object> result = new ArrayList<>();
        position++;
        skipSpaces();

        if (take(']')) {
            return result;
        }

        while (true) {
            result.add(readValue());
            skipSpaces();

            if (take(']')) {
                return result;
            }

            if (!take(',')) {
                throw new IllegalArgumentException("ожидалась запятая");
            }
        }
    }

    private String readString() {
        position++;
        StringBuilder result = new StringBuilder();

        while (position < text.length()) {
            char symbol = text.charAt(position++);

            if (symbol == '"') {
                return result.toString();
            }

            if (symbol == '\\') {
                if (position >= text.length()) {
                    throw new IllegalArgumentException("незавершённая строка JSON");
                }

                char escaped = text.charAt(position++);

                switch (escaped) {
                    case '"': result.append('"'); break;
                    case '\\': result.append('\\'); break;
                    case '/': result.append('/'); break;
                    case 'b': result.append('\b'); break;
                    case 'f': result.append('\f'); break;
                    case 'n': result.append('\n'); break;
                    case 'r': result.append('\r'); break;
                    case 't': result.append('\t'); break;

                    case 'u':
                        if (position + 4 > text.length()) {
                            throw new IllegalArgumentException(
                                    "неверный символ Unicode в JSON");
                        }

                        String hex = text.substring(position, position + 4);

                        try {
                            result.append((char) Integer.parseInt(hex, 16));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(
                                    "неверный символ Unicode в JSON");
                        }

                        position += 4;
                        break;

                    default:
                        throw new IllegalArgumentException(
                                "неверная escape-последовательность JSON");
                }
            } else {
                result.append(symbol);
            }
        }

        throw new IllegalArgumentException("строка JSON не закрыта");
    }

    private void skipSpaces() {
        while (position < text.length()
                && Character.isWhitespace(text.charAt(position))) {
            position++;
        }
    }

    private boolean take(char expected) {
        if (position < text.length() && text.charAt(position) == expected) {
            position++;
            return true;
        }

        return false;
    }
}