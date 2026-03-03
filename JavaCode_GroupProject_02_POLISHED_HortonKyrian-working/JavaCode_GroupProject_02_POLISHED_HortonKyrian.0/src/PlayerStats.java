import java.util.*;

/**
 * PlayerStats – career totals (per-player) with a superset of common NFL stats.
 *
 * Storage:
 *  • data/player_stats.csv (keyed by player name)
 *
 * UI:
 *  • CowboysApp renders a position-specific subset.
 */
public class PlayerStats {

    private String playerName;

    // Core / universal
    public int games;
    public int gamesStarted;

    // QB / passing
    public int passAttempts;
    public int passCompletions;
    public int passYards;
    public int passTD;
    public int passINT;
    public int sacksTaken;

    // Rushing
    public int rushAttempts;
    public int rushYards;
    public int rushTD;

    // Receiving
    public int targets;
    public int receptions;
    public int recYards;
    public int recTD;

    // Defense
    public int tackles;
    public int tacklesForLoss;
    public int sacks;
    public int qbHits;
    public int interceptions;
    public int passesDefended;
    public int forcedFumbles;
    public int fumbleRecoveries;
    public int defensiveTD;

    // Ball security
    public int fumbles;
    public int fumblesLost;

    // OL
    public int penalties;
    public int sacksAllowed;

    // K/P
    public int fieldGoalsMade;
    public int fieldGoalsAttempted;
    public int extraPointsMade;
    public int extraPointsAttempted;
    public int punts;
    public int puntYards;
    public int puntsInside20;

    public PlayerStats(String playerName) {
        this.playerName = playerName == null ? "" : playerName;
    }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) { this.playerName = name == null ? "" : name; }

    // Derived
    public double completionPct() {
        if (passAttempts <= 0) return 0.0;
        return (100.0 * passCompletions) / passAttempts;
    }

    public double yardsPerAttempt() {
        if (passAttempts <= 0) return 0.0;
        return (1.0 * passYards) / passAttempts;
    }

    public double rushYardsPerCarry() {
        if (rushAttempts <= 0) return 0.0;
        return (1.0 * rushYards) / rushAttempts;
    }

    public double recYardsPerCatch() {
        if (receptions <= 0) return 0.0;
        return (1.0 * recYards) / receptions;
    }

    // CSV schema (superset)
    public static List<String> csvHeader() {
        return Arrays.asList(
            "PlayerName",
            "Games","GamesStarted",
            "PassAttempts","PassCompletions","PassYards","PassTD","PassINT","SacksTaken",
            "RushAttempts","RushYards","RushTD",
            "Targets","Receptions","RecYards","RecTD",
            "Tackles","TFL","Sacks","QBHits","Interceptions","PassesDefended","ForcedFumbles","FumbleRecoveries","DefensiveTD",
            "Fumbles","FumblesLost",
            "Penalties","SacksAllowed",
            "FGMade","FGAtt","XPMade","XPAtt","Punts","PuntYards","PuntsInside20"
        );
    }

    public List<String> toCsvRow() {
        return Arrays.asList(
            safe(playerName),
            String.valueOf(games), String.valueOf(gamesStarted),
            String.valueOf(passAttempts), String.valueOf(passCompletions), String.valueOf(passYards), String.valueOf(passTD), String.valueOf(passINT), String.valueOf(sacksTaken),
            String.valueOf(rushAttempts), String.valueOf(rushYards), String.valueOf(rushTD),
            String.valueOf(targets), String.valueOf(receptions), String.valueOf(recYards), String.valueOf(recTD),
            String.valueOf(tackles), String.valueOf(tacklesForLoss), String.valueOf(sacks), String.valueOf(qbHits), String.valueOf(interceptions), String.valueOf(passesDefended), String.valueOf(forcedFumbles), String.valueOf(fumbleRecoveries), String.valueOf(defensiveTD),
            String.valueOf(fumbles), String.valueOf(fumblesLost),
            String.valueOf(penalties), String.valueOf(sacksAllowed),
            String.valueOf(fieldGoalsMade), String.valueOf(fieldGoalsAttempted), String.valueOf(extraPointsMade), String.valueOf(extraPointsAttempted),
            String.valueOf(punts), String.valueOf(puntYards), String.valueOf(puntsInside20)
        );
    }

    public static PlayerStats fromCsvRow(String[] d) {
        PlayerStats s = new PlayerStats(d.length > 0 ? d[0].trim() : "");
        int i = 1;
        s.games               = intAt(d, i++);
        s.gamesStarted        = intAt(d, i++);

        s.passAttempts        = intAt(d, i++);
        s.passCompletions     = intAt(d, i++);
        s.passYards           = intAt(d, i++);
        s.passTD              = intAt(d, i++);
        s.passINT             = intAt(d, i++);
        s.sacksTaken          = intAt(d, i++);

        s.rushAttempts        = intAt(d, i++);
        s.rushYards           = intAt(d, i++);
        s.rushTD              = intAt(d, i++);

        s.targets             = intAt(d, i++);
        s.receptions          = intAt(d, i++);
        s.recYards            = intAt(d, i++);
        s.recTD               = intAt(d, i++);

        s.tackles             = intAt(d, i++);
        s.tacklesForLoss      = intAt(d, i++);
        s.sacks               = intAt(d, i++);
        s.qbHits              = intAt(d, i++);
        s.interceptions       = intAt(d, i++);
        s.passesDefended      = intAt(d, i++);
        s.forcedFumbles       = intAt(d, i++);
        s.fumbleRecoveries    = intAt(d, i++);
        s.defensiveTD         = intAt(d, i++);

        s.fumbles             = intAt(d, i++);
        s.fumblesLost         = intAt(d, i++);

        s.penalties           = intAt(d, i++);
        s.sacksAllowed        = intAt(d, i++);

        s.fieldGoalsMade      = intAt(d, i++);
        s.fieldGoalsAttempted = intAt(d, i++);
        s.extraPointsMade     = intAt(d, i++);
        s.extraPointsAttempted= intAt(d, i++);
        s.punts               = intAt(d, i++);
        s.puntYards           = intAt(d, i++);
        s.puntsInside20       = intAt(d, i++);
        return s;
    }

    private static int intAt(String[] d, int idx) {
        if (d == null || idx < 0 || idx >= d.length) return 0;
        try { return Integer.parseInt(d[idx].trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private static String safe(String v) { return v == null ? "" : v; }
}
