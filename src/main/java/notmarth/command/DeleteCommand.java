package notmarth.command;

import notmarth.exception.NotMarthException;
import notmarth.model.Task;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes a command that removes a task from the battle plan. */
public final class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a delete command for a one-based task number.
     *
     * @param taskNumber the task number to remove
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NotMarthException {
        Task deletedTask = tasks.delete(taskNumber);
        saveTasks(tasks, ui, storage);
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
