import java.util.regex.Pattern;

public class Validators {
    private static final Pattern HEIGHT = Pattern.compile("^\\d{1,2}-\\d{1,2}$");

    public static String requireNonBlank(String label, String v) {
        if (v == null || v.trim().isEmpty()) return label + " is required.";
        return null;
    }

    public static String intRange(String label, String raw, int min, int max) {
        if (raw == null || raw.trim().isEmpty()) return null; // treat blank as optional
        try {
            int x = Integer.parseInt(raw.trim());
            if (x < min || x > max) return label + " must be between " + min + " and " + max + ".";
            return null;
        } catch (NumberFormatException e) {
            return label + " must be a whole number.";
        }
    }

    public static String requireIntRange(String label, String raw, int min, int max) {
        String r = requireNonBlank(label, raw);
        if (r != null) return r;
        return intRange(label, raw, min, max);
    }

    public static String height(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        String v = raw.trim();
        if (!HEIGHT.matcher(v).matches()) return "Height should look like 6-2 (feet-inches).";
        return null;
    }

    public static String digits(String label, String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        if (!raw.trim().matches("\\d+")) return label + " must be numeric.";
        return null;
    }
}
