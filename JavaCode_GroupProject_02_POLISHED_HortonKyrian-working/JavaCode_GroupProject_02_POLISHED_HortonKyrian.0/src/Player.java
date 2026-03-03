public class Player {
    private String name;
    private String position;
    private int joined;
    private String height;
    private String weight;
    private String college;
    private String number;
    private String status;
    private int draftYear;
    private int draftRound;
    private int draftPick;
    private int proBowls;
    private String achievements;
    private String info;
    private String imageURL;
    private boolean favorite;

    public Player(String name, String position, int joined, String height,
                  String weight, String college, String number, String status,
                  int draftYear, int draftRound, int draftPick, int proBowls,
                  String achievements, String info, String imageURL) {
        this.name        = name        != null ? name.trim()        : "";
        this.position    = position    != null ? position.trim()    : "";
        this.joined      = joined;
        this.height      = height      != null ? height.trim()      : "";
        this.weight      = weight      != null ? weight.trim()      : "";
        this.college     = college     != null ? college.trim()     : "";
        this.number      = number      != null ? number.trim()      : "";
        this.status      = status      != null ? status.trim()      : "Active";
        this.draftYear   = draftYear;
        this.draftRound  = draftRound;
        this.draftPick   = draftPick;
        this.proBowls    = Math.max(0, proBowls);
        this.achievements = achievements != null ? achievements.trim() : "";
        this.info        = info        != null ? info.trim()        : "";
        this.imageURL    = imageURL    != null ? imageURL.trim()    : "";
        this.favorite    = false;
    }

    public String getName()         { return name; }
    public String getPosition()     { return position; }
    public int    getJoined()       { return joined; }
    public String getHeight()       { return height; }
    public String getWeight()       { return weight; }
    public String getCollege()      { return college; }
    public String getNumber()       { return number; }
    public String getStatus()       { return status; }
    public int    getDraftYear()    { return draftYear; }
    public int    getDraftRound()   { return draftRound; }
    public int    getDraftPick()    { return draftPick; }
    public int    getProBowls()     { return proBowls; }
    public String getAchievements() { return achievements; }
    public String getInfo()         { return info; }
    public String getImageURL()     { return imageURL; }
    public boolean isFavorite()     { return favorite; }

    public void setName(String name)               { this.name         = name        != null ? name.trim()        : ""; }
    public void setPosition(String position)       { this.position     = position    != null ? position.trim()    : ""; }
    public void setJoined(int joined)              { this.joined       = joined; }
    public void setHeight(String height)           { this.height       = height      != null ? height.trim()      : ""; }
    public void setWeight(String weight)           { this.weight       = weight      != null ? weight.trim()      : ""; }
    public void setCollege(String college)         { this.college      = college     != null ? college.trim()     : ""; }
    public void setNumber(String number)           { this.number       = number      != null ? number.trim()      : ""; }
    public void setStatus(String status)           { this.status       = status      != null ? status.trim()      : "Active"; }
    public void setDraftYear(int draftYear)        { this.draftYear    = draftYear; }
    public void setDraftRound(int draftRound)      { this.draftRound   = draftRound; }
    public void setDraftPick(int draftPick)        { this.draftPick    = draftPick; }
    public void setProBowls(int proBowls)          { this.proBowls     = Math.max(0, proBowls); }
    public void setAchievements(String a)          { this.achievements = a != null ? a.trim() : ""; }
    public void setInfo(String info)               { this.info         = info != null ? info.trim() : ""; }
    public void setImageURL(String imageURL)       { this.imageURL     = imageURL != null ? imageURL.trim() : ""; }
    public void setFavorite(boolean favorite)      { this.favorite     = favorite; }
    public void toggleFavorite()                   { this.favorite     = !this.favorite; }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }

    /** Returns how many seasons the player has been with Dallas (relative to current year). */
    public int yearsWithDallas(int currentYear) {
        return Math.max(0, currentYear - joined);
    }

    /** Undrafted if draftRound == 0. */
    public boolean isUndrafted() {
        return draftRound == 0;
    }

    /** Human-readable draft string. */
    public String getDraftDisplay() {
        if (isUndrafted()) return "Undrafted";
        return draftYear + " – Rd " + draftRound + ", Pick " + draftPick;
    }

    /** Snapshot copy used for undo/redo (including favourite flag). */
    public static Player copyOf(Player p) {
        if (p == null) return null;
        Player c = new Player(
            p.getName(), p.getPosition(), p.getJoined(), p.getHeight(),
            p.getWeight(), p.getCollege(), p.getNumber(), p.getStatus(),
            p.getDraftYear(), p.getDraftRound(), p.getDraftPick(), p.getProBowls(),
            p.getAchievements(), p.getInfo(), p.getImageURL()
        );
        c.setFavorite(p.isFavorite());
        return c;
    }

    @Override
    public String toString() { return name; }
}
