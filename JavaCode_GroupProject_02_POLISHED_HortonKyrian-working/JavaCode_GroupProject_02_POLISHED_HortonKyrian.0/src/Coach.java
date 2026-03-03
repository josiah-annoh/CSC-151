public class Coach {
    private String name;
    private String role;
    private int    joined;
    private String experience;
    private String previousTeam;
    private String status;
    private int    championships;
    private String achievements;
    private String info;
    private String imageURL;
    private boolean favorite;

    public Coach(String name, String role, int joined, String experience,
                 String previousTeam, String status, int championships,
                 String achievements, String info, String imageURL) {
        this.name          = name          != null ? name.trim()          : "";
        this.role          = role          != null ? role.trim()          : "";
        this.joined        = joined;
        this.experience    = experience    != null ? experience.trim()    : "";
        this.previousTeam  = previousTeam  != null ? previousTeam.trim()  : "";
        this.status        = status        != null ? status.trim()        : "Active";
        this.championships = Math.max(0, championships);
        this.achievements  = achievements  != null ? achievements.trim()  : "";
        this.info          = info          != null ? info.trim()          : "";
        this.imageURL      = imageURL      != null ? imageURL.trim()      : "";
        this.favorite      = false;
    }

    //  Getters
    public String getName()            { return name; }
    public String getRole()            { return role; }
    public int    getJoined()          { return joined; }
    public String getExperience()      { return experience; }
    public String getPreviousTeam()    { return previousTeam; }
    public String getStatus()          { return status; }
    public int    getChampionships()   { return championships; }
    public String getAchievements()    { return achievements; }
    public String getInfo()            { return info; }
    public String getImageURL()        { return imageURL; }
    public boolean isFavorite()        { return favorite; }

    //  Setters
    public void setName(String name)                   { this.name          = name != null ? name.trim() : ""; }
    public void setRole(String role)                   { this.role          = role != null ? role.trim() : ""; }
    public void setJoined(int joined)                  { this.joined        = joined; }
    public void setExperience(String experience)       { this.experience    = experience != null ? experience.trim() : ""; }
    public void setPreviousTeam(String previousTeam)   { this.previousTeam  = previousTeam != null ? previousTeam.trim() : ""; }
    public void setStatus(String status)               { this.status        = status != null ? status.trim() : "Active"; }
    public void setChampionships(int championships)    { this.championships = Math.max(0, championships); }
    public void setAchievements(String achievements)   { this.achievements  = achievements != null ? achievements.trim() : ""; }
    public void setInfo(String info)                   { this.info          = info != null ? info.trim() : ""; }
    public void setImageURL(String imageURL)           { this.imageURL      = imageURL != null ? imageURL.trim() : ""; }
    public void setFavorite(boolean favorite)          { this.favorite      = favorite; }
    public void toggleFavorite()                       { this.favorite      = !this.favorite; }

    //  Derived helpers
    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }

    public int yearsWithDallas(int currentYear) {
        return Math.max(0, currentYear - joined);
    }

    /** Snapshot copy used for undo/redo (including favourite flag). */
    public static Coach copyOf(Coach c0) {
        if (c0 == null) return null;
        Coach c = new Coach(
            c0.getName(), c0.getRole(), c0.getJoined(), c0.getExperience(),
            c0.getPreviousTeam(), c0.getStatus(), c0.getChampionships(),
            c0.getAchievements(), c0.getInfo(), c0.getImageURL()
        );
        c.setFavorite(c0.isFavorite());
        return c;
    }

    @Override
    public String toString() { return name; }
}
