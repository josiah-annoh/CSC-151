import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CowboysDirectory – data access layer.
 *
 * Improvements over original:
 *  • RFC-4180-compliant CSV parsing (handles quoted fields that contain commas)
 *  • RFC-4180-compliant CSV writing  (wraps fields in quotes when needed)
 *  • Favourites persisted to data/favorites.txt
 *  • Added sortPlayers / sortCoaches helpers
 *  • Fixed hardcoded year – callers now pass currentYear
 */
public class CowboysDirectory {

    /** Same logic as CowboysApp.DATA_DIR – resolves data/ next to the .class files. */
    static final String DATA_DIR = resolveDataDir();

    private static String resolveDataDir() {
        try {
            String classPath = CowboysDirectory.class
                .getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            java.io.File classDir = new java.io.File(classPath);
            if (!classDir.isDirectory()) classDir = classDir.getParentFile();

            java.io.File dir = classDir;
            for (int i = 0; i <= 3; i++) {
                if (dir == null) break;
                java.io.File candidate = new java.io.File(dir, "data");
                if (candidate.exists() && candidate.isDirectory()) {
                    return candidate.getAbsolutePath() + java.io.File.separator;
                }
                dir = dir.getParentFile();
            }
            return "data" + java.io.File.separator;
        } catch (Exception e) {
            return "data" + java.io.File.separator;
        }
    }

    // CSV HELPERS – RFC-4180 compliant

    /** Parse one CSV line respecting double-quoted fields (which may contain commas). */
    static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb    = new StringBuilder();
        boolean inQuotes    = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');   // escaped quote ""
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(String[]::new);
    }

    /** Wrap a field in quotes if it contains commas, quotes, or newlines. */
    static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static int parseIntSafe(String value, int defaultValue) {
        if (value == null) return defaultValue;
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    public static List<Cheerleader> loadCheerleaders(String filePath) {
        List<Cheerleader> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim(); if (line.isEmpty()) continue;
                String[] d = parseCsvLine(line);
                if (d.length >= 9) {
                    list.add(new Cheerleader(
                        d[0].trim(), d[1].trim(),
                        parseIntSafe(d[2], 0), d[3].trim(), parseIntSafe(d[4], 0),
                        d[5].trim(), parseIntSafe(d[6], 0), d[7].trim(), d[8].trim(),
                        d.length > 9 ? d[9].trim() : ""
                    ));
                }
            }
        } catch (Exception e) { System.err.println("Error loading cheerleaders: " + e.getMessage()); }
        return list;
    }

    public static List<Staff> loadStaff(String filePath) {
        List<Staff> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim(); if (line.isEmpty()) continue;
                String[] d = parseCsvLine(line);
                if (d.length >= 9) {
                    list.add(new Staff(
                        d[0].trim(), d[1].trim(),
                        parseIntSafe(d[2], 0), d[3].trim(), parseIntSafe(d[4], 0),
                        d[5].trim(), parseIntSafe(d[6], 0), d[7].trim(), d[8].trim(),
                        d.length > 9 ? d[9].trim() : ""
                    ));
                }
            }
        } catch (Exception e) { System.err.println("Error loading staff: " + e.getMessage()); }
        return list;
    }

    // LOAD

    public static List<Player> loadPlayers(String filePath) {
        List<Player> players = new ArrayList<>();
        Set<String> favorites = loadFavorites(DATA_DIR + "favorites_players.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] d = parseCsvLine(line);
                if (d.length >= 14) {
                    Player p = new Player(
                        d[0].trim(),
                        d[1].trim(),
                        parseIntSafe(d[2], 0),
                        d[3].trim(),
                        d[4].trim(),
                        d[5].trim(),
                        d[6].trim(),
                        d[7].trim(),
                        parseIntSafe(d[8], 0),
                        parseIntSafe(d[9], 0),
                        parseIntSafe(d[10], 0),
                        parseIntSafe(d[11], 0),
                        d[12].trim(),
                        d[13].trim(),
                        d.length > 14 ? d[14].trim() : ""
                    );
                    if (favorites.contains(p.getName())) p.setFavorite(true);
                    players.add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading players: " + e.getMessage());
        }
        return players;
    }

    // PLAYER STATS (career totals)

    /**
     * Loads player stats keyed by PlayerName. If the file is missing, returns an empty map.
     * Callers should ensure missing players get default (zero) stats and then persist.
     */
    public static Map<String, PlayerStats> loadPlayerStats(String filePath) {
        Map<String, PlayerStats> map = new LinkedHashMap<>();
        File f = new File(filePath);
        if (!f.exists()) return map;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // header
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] d = parseCsvLine(line);
                if (d.length >= 1) {
                    PlayerStats s = PlayerStats.fromCsvRow(d);
                    if (!s.getPlayerName().trim().isEmpty()) {
                        map.put(s.getPlayerName().trim(), s);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading player stats: " + e.getMessage());
        }
        return map;
    }

    /** Saves stats as a CSV with a fixed superset schema. */
    public static boolean savePlayerStats(Map<String, PlayerStats> stats, String filePath) {
        try (PrintWriter w = new PrintWriter(new FileWriter(filePath))) {
            w.println(String.join(",", PlayerStats.csvHeader()));
            for (PlayerStats s : stats.values()) {
                List<String> row = s.toCsvRow();
                // Escape the name field (only one that realistically can contain commas)
                row.set(0, escapeCsv(row.get(0)));
                w.println(String.join(",", row));
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error saving player stats: " + e.getMessage());
            return false;
        }
    }

    public static List<Coach> loadCoaches(String filePath) {
        List<Coach> coaches = new ArrayList<>();
        Set<String> favorites = loadFavorites(DATA_DIR + "favorites_coaches.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] d = parseCsvLine(line);
                if (d.length >= 9) {
                    Coach c = new Coach(
                        d[0].trim(),
                        d[1].trim(),
                        parseIntSafe(d[2], 0),
                        d[3].trim(),
                        d[4].trim(),
                        d[5].trim(),
                        parseIntSafe(d[6], 0),
                        d[7].trim(),
                        d[8].trim(),
                        d.length > 9 ? d[9].trim() : ""
                    );
                    if (favorites.contains(c.getName())) c.setFavorite(true);
                    coaches.add(c);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading coaches: " + e.getMessage());
        }
        return coaches;
    }

    // SAVE

    public static boolean savePlayers(List<Player> players, String filePath) {
        try (PrintWriter w = new PrintWriter(new FileWriter(filePath))) {
            w.println("Name,Position,Joined,Height,Weight,College,Number,Status,"
                    + "DraftYear,DraftRound,DraftPick,ProBowls,Achievements,Info,ImageURL");
            for (Player p : players) {
                w.println(String.join(",",
                    escapeCsv(p.getName()),
                    escapeCsv(p.getPosition()),
                    String.valueOf(p.getJoined()),
                    escapeCsv(p.getHeight()),
                    escapeCsv(p.getWeight()),
                    escapeCsv(p.getCollege()),
                    escapeCsv(p.getNumber()),
                    escapeCsv(p.getStatus()),
                    String.valueOf(p.getDraftYear()),
                    String.valueOf(p.getDraftRound()),
                    String.valueOf(p.getDraftPick()),
                    String.valueOf(p.getProBowls()),
                    escapeCsv(p.getAchievements()),
                    escapeCsv(p.getInfo()),
                    escapeCsv(p.getImageURL())
                ));
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error saving players: " + e.getMessage());
            return false;
        }
    }

    public static boolean saveCoaches(List<Coach> coaches, String filePath) {
        try (PrintWriter w = new PrintWriter(new FileWriter(filePath))) {
            w.println("Name,Role,Joined,Experience,PreviousTeam,Status,Championships,Achievements,Info,ImageURL");
            for (Coach c : coaches) {
                w.println(String.join(",",
                    escapeCsv(c.getName()),
                    escapeCsv(c.getRole()),
                    String.valueOf(c.getJoined()),
                    escapeCsv(c.getExperience()),
                    escapeCsv(c.getPreviousTeam()),
                    escapeCsv(c.getStatus()),
                    String.valueOf(c.getChampionships()),
                    escapeCsv(c.getAchievements()),
                    escapeCsv(c.getInfo()),
                    escapeCsv(c.getImageURL())
                ));
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error saving coaches: " + e.getMessage());
            return false;
        }
    }

    // FAVORITES PERSISTENCE

    private static Set<String> loadFavorites(String filePath) {
        Set<String> favs = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) favs.add(line);
            }
        } catch (IOException ignored) {}   // file may not exist yet
        return favs;
    }

    public static void savePlayerFavorites(List<Player> players) {
        saveFavoritesToFile(
            players.stream().filter(Player::isFavorite).map(Player::getName).collect(Collectors.toList()),
            DATA_DIR + "favorites_players.txt"
        );
    }

    public static void saveCoachFavorites(List<Coach> coaches) {
        saveFavoritesToFile(
            coaches.stream().filter(Coach::isFavorite).map(Coach::getName).collect(Collectors.toList()),
            DATA_DIR + "favorites_coaches.txt"
        );
    }

    private static void saveFavoritesToFile(List<String> names, String filePath) {
        try (PrintWriter w = new PrintWriter(new FileWriter(filePath))) {
            names.forEach(w::println);
        } catch (IOException e) {
            System.err.println("Error saving favorites: " + e.getMessage());
        }
    }

    // BACKUP / RESTORE

    public static boolean backupData(String sourceFile, String backupFile) {
        try {
            Files.copy(Paths.get(sourceFile), Paths.get(backupFile),
                       StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            System.err.println("Error backing up: " + e.getMessage());
            return false;
        }
    }

    public static boolean restoreData(String backupFile, String targetFile) {
        return backupData(backupFile, targetFile);
    }

    // FILTER – PLAYERS

    public static List<Player> filterPlayersByStatus(List<Player> players, boolean activeOnly) {
        if (!activeOnly) return players;
        return players.stream().filter(Player::isActive).collect(Collectors.toList());
    }

    public static List<Player> filterPlayersByPosition(List<Player> players, String position) {
        if (position == null || position.equals("All Positions")) return players;
        return players.stream()
                .filter(p -> p.getPosition().equalsIgnoreCase(position))
                .collect(Collectors.toList());
    }

    public static List<Player> filterPlayersByYear(List<Player> players, int year) {
        return players.stream().filter(p -> p.getJoined() == year).collect(Collectors.toList());
    }

    public static List<Player> filterPlayersByYearRange(List<Player> players, int start, int end) {
        return players.stream()
                .filter(p -> p.getJoined() >= start && p.getJoined() <= end)
                .collect(Collectors.toList());
    }

    public static List<Player> filterPlayersByProBowls(List<Player> players, int min) {
        return players.stream().filter(p -> p.getProBowls() >= min).collect(Collectors.toList());
    }

    public static List<Player> filterPlayersFavorites(List<Player> players) {
        return players.stream().filter(Player::isFavorite).collect(Collectors.toList());
    }

    // FILTER – COACHES

    public static List<Coach> filterCoachesByStatus(List<Coach> coaches, boolean activeOnly) {
        if (!activeOnly) return coaches;
        return coaches.stream().filter(Coach::isActive).collect(Collectors.toList());
    }

    public static List<Coach> filterCoachesByYear(List<Coach> coaches, int year) {
        return coaches.stream().filter(c -> c.getJoined() == year).collect(Collectors.toList());
    }

    public static List<Coach> filterCoachesByYearRange(List<Coach> coaches, int start, int end) {
        return coaches.stream()
                .filter(c -> c.getJoined() >= start && c.getJoined() <= end)
                .collect(Collectors.toList());
    }

    public static List<Coach> filterCoachesByRole(List<Coach> coaches, String role) {
        if (role == null || role.isEmpty()) return coaches;
        String lower = role.toLowerCase();
        return coaches.stream()
                .filter(c -> c.getRole().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public static List<Coach> filterCoachesFavorites(List<Coach> coaches) {
        return coaches.stream().filter(Coach::isFavorite).collect(Collectors.toList());
    }

    // SEARCH

    public static List<Player> searchPlayers(List<Player> players, String term) {
        if (term == null || term.trim().isEmpty()) return players;
        String t = term.toLowerCase().trim();
        return players.stream()
                .filter(p -> p.getName().toLowerCase().contains(t)
                          || p.getPosition().toLowerCase().contains(t)
                          || p.getCollege().toLowerCase().contains(t)
                          // quick-access searches (e.g., "Hall of Fame") live in Achievements
                          || safeLower(p.getAchievements()).contains(t)
                          || safeLower(p.getStatus()).contains(t)
                          || safeLower(p.getNumber()).contains(t))
                .collect(Collectors.toList());
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    public static List<Coach> searchCoaches(List<Coach> coaches, String term) {
        if (term == null || term.trim().isEmpty()) return coaches;
        String t = term.toLowerCase().trim();
        return coaches.stream()
                .filter(c -> c.getName().toLowerCase().contains(t)
                          || c.getRole().toLowerCase().contains(t)
                          || c.getPreviousTeam().toLowerCase().contains(t))
                .collect(Collectors.toList());
    }

    // SORT

    public static List<Player> sortPlayers(List<Player> players, String column, boolean ascending) {
        Comparator<Player> cmp;
        switch (column) {
            case "Name":      cmp = Comparator.comparing(Player::getName,     String.CASE_INSENSITIVE_ORDER); break;
            case "Position":  cmp = Comparator.comparing(Player::getPosition, String.CASE_INSENSITIVE_ORDER); break;
            case "Joined":    cmp = Comparator.comparingInt(Player::getJoined); break;
            case "Number":    cmp = Comparator.comparingInt(p -> parseIntSafe(p.getNumber(), 999)); break;
            case "Pro Bowls": cmp = Comparator.comparingInt(Player::getProBowls); break;
            case "Status":    cmp = Comparator.comparing(Player::getStatus,   String.CASE_INSENSITIVE_ORDER); break;
            default:          cmp = Comparator.comparing(Player::getName,     String.CASE_INSENSITIVE_ORDER); break;
        }
        if (!ascending) cmp = cmp.reversed();
        return players.stream().sorted(cmp).collect(Collectors.toList());
    }

    public static List<Coach> sortCoaches(List<Coach> coaches, String column, boolean ascending) {
        Comparator<Coach> cmp;
        switch (column) {
            case "Name":           cmp = Comparator.comparing(Coach::getName,       String.CASE_INSENSITIVE_ORDER); break;
            case "Role":           cmp = Comparator.comparing(Coach::getRole,       String.CASE_INSENSITIVE_ORDER); break;
            case "Joined":         cmp = Comparator.comparingInt(Coach::getJoined); break;
            case "Championships":  cmp = Comparator.comparingInt(Coach::getChampionships); break;
            case "Status":         cmp = Comparator.comparing(Coach::getStatus,     String.CASE_INSENSITIVE_ORDER); break;
            default:               cmp = Comparator.comparing(Coach::getName,       String.CASE_INSENSITIVE_ORDER); break;
        }
        if (!ascending) cmp = cmp.reversed();
        return coaches.stream().sorted(cmp).collect(Collectors.toList());
    }

    // STATISTICS

    public static long countActivePlayers(List<Player> players) {
        return players.stream().filter(Player::isActive).count();
    }

    public static long countActiveCoaches(List<Coach> coaches) {
        return coaches.stream().filter(Coach::isActive).count();
    }

    public static int getTotalProBowls(List<Player> players) {
        return players.stream().mapToInt(Player::getProBowls).sum();
    }

    public static int getTotalChampionships(List<Coach> coaches) {
        return coaches.stream().mapToInt(Coach::getChampionships).sum();
    }

    public static List<String> getAllPositions(List<Player> players) {
        return players.stream()
                .map(Player::getPosition)
                .filter(pos -> !pos.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public static Map<String, Long> getPlayerCountByPosition(List<Player> players) {
        Map<String, Long> map = new TreeMap<>();
        players.stream()
               .collect(Collectors.groupingBy(Player::getPosition, Collectors.counting()))
               .forEach(map::put);
        return map;
    }

    public static Map<String, Long> getPlayerCountByDecade(List<Player> players) {
        Map<String, Long> map = new TreeMap<>();
        players.stream()
               .collect(Collectors.groupingBy(
                    p -> (p.getJoined() / 10 * 10) + "s",
                    Collectors.counting()))
               .forEach(map::put);
        return map;
    }

    public static Map<Integer, Long> getDraftRoundDistribution(List<Player> players) {
        return players.stream()
                .filter(p -> !p.isUndrafted())
                .collect(Collectors.groupingBy(Player::getDraftRound, TreeMap::new, Collectors.counting()));
    }

    public static List<Integer> getAllYears(List<Player> players, List<Coach> coaches) {
        Set<Integer> years = new TreeSet<>();
        players.forEach(p -> years.add(p.getJoined()));
        coaches.forEach(c -> years.add(c.getJoined()));
        return new ArrayList<>(years);
    }
}
