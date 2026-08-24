package notmarth.command;

import java.util.Locale;

import notmarth.model.Task;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes a command that searches task descriptions for a keyword. */
public final class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command for a keyword.
     *
     * @param keyword the keyword to search for
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param tasks the task list to search
     * @param ui the console interaction handler
     * @param storage the task archive handler, which is unused
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        boolean foundMatch = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean matches = task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword);
            if (matches) {
                if (!foundMatch) {
                    ui.showFindTasksHeader();
                    foundMatch = true;
                }
                ui.showNumberedTask(i + 1, task);
            }
        }
        if (!foundMatch) {
            ui.showNoFindTasks(keyword);
        }
    }
}
