import java.util.regex.Matcher;
import java.util.regex.Pattern;
class TemplateFiller {
    public String fillTemplate(String template, String[] names, String[] values) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = Pattern.compile("\\{(\\w+)\\}").matcher(template);
        int last = 0;
        while (matcher.find()) {
            result.append(template, last, matcher.start());
            String key = matcher.group(1), value = matcher.group();
            for (int i = 0; i < names.length; i++) if (names[i].equals(key)) { value = values[i]; break; }
            result.append(value);
            last = matcher.end();
        }
        result.append(template.substring(last));
        return result.toString();
    }
}
