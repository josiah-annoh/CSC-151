public interface Command {
    /** Execute the command. Should be idempotent only in the sense of being called once. */
    void execute();

    /** Undo the command. Must revert the effects of execute(). */
    void undo();

    /** A short label for UI (Undo <label>). */
    String label();
}
