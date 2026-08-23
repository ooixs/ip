package notmarth.command;

import notmarth.exception.NotMarthException;
import notmarth.model.Task;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes a command that marks a task as complete. */
public final class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a mark command for a one-based task number.
     *
     * @param taskNumber the task number to complete
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NotMarthException {
        Task task = tasks.mark(taskNumber);
        saveTasks(tasks, ui, storage);
        ui.showTaskMarked(task);
    }
}
