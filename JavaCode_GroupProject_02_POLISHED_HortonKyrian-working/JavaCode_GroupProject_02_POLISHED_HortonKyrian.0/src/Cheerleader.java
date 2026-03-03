public class Cheerleader {
    private final String name;
    private final String role;
    private final int    age;
    private final String height;
    private final int    weight;
    private final String college;
    private final int    experience;
    private final String status;
    private final String achievements;
    private final String info;
    private boolean favorite;

    public Cheerleader(String name, String role, int age, String height, int weight,
                       String college, int experience, String status,
                       String achievements, String info) {
        this.name         = name         != null ? name.trim()         : "";
        this.role         = role         != null ? role.trim()         : "";
        this.age          = age;
        this.height       = height       != null ? height.trim()       : "";
        this.weight       = weight;
        this.college      = college      != null ? college.trim()      : "";
        this.experience   = experience;
        this.status       = status       != null ? status.trim()       : "Active";
        this.achievements = achievements != null ? achievements.trim() : "";
        this.info         = info         != null ? info.trim()         : "";
        this.favorite     = false;
    }

    public String  getName()         { return name; }
    public String  getRole()         { return role; }
    public int     getAge()          { return age; }
    public String  getHeight()       { return height; }
    public int     getWeight()       { return weight; }
    public String  getCollege()      { return college; }
    public int     getExperience()   { return experience; }
    public String  getStatus()       { return status; }
    public String  getAchievements() { return achievements; }
    public String  getInfo()         { return info; }
    public boolean isFavorite()      { return favorite; }
    public boolean isActive()        { return "Active".equalsIgnoreCase(status); }

    public void setFavorite(boolean f) { this.favorite = f; }
    public void toggleFavorite()       { this.favorite = !this.favorite; }

    @Override public String toString() { return name; }
}
