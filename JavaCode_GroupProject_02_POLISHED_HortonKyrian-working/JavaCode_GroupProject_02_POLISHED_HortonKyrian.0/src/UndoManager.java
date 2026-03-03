import java.util.ArrayDeque;
import java.util.Deque;

/** Simple undo/redo manager for UI actions (add/edit/delete/favourite). */
public class UndoManager {
    private final Deque<Command> undo = new ArrayDeque<>();
    private final Deque<Command> redo = new ArrayDeque<>();
    private final int maxSize;

    public UndoManager(int maxSize) {
        this.maxSize = Math.max(10, maxSize);
    }

    public void doCommand(Command c) {
        if (c == null) return;
        c.execute();
        undo.push(c);
        redo.clear();
        while (undo.size() > maxSize) {
            // drop oldest (bottom)
            Command last = null;
            for (Command cmd : undo) last = cmd;
            if (last != null) undo.removeLast();
            else break;
        }
    }

    public boolean canUndo() { return !undo.isEmpty(); }
    public boolean canRedo() { return !redo.isEmpty(); }

    public Command peekUndo() { return undo.peek(); }
    public Command peekRedo() { return redo.peek(); }

    public void undo() {
        if (undo.isEmpty()) return;
        Command c = undo.pop();
        c.undo();
        redo.push(c);
    }

    public void redo() {
        if (redo.isEmpty()) return;
        Command c = redo.pop();
        c.execute();
        undo.push(c);
    }

    public void clear() {
        undo.clear();
        redo.clear();
    }
}
