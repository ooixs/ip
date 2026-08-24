package notmarth.command;

import java.time.LocalDate;

import notmarth.model.Deadline;
import notmarth.model.Event;
import notmarth.model.Task;
import notmarth.model.TaskList;
import notmarth.parser.DateTimeParser;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes a command that displays deadlines and events for a date. */
public final class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a date-query command.
     *
     * @param date the date to inspect
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays deadlines and events that match the command's requested date.
     *
     * @param tasks the task list to search
     * @param ui the console interaction handler
     * @param storage the task archive handler, which is unused
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String displayDate = DateTimeParser.format(date.atStartOfDay(), false);
        boolean foundMatch = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean matches = task instanceof Deadline deadline && deadline.isDueOn(date)
                    || task instanceof Event event && event.occursOn(date);
            if (matches) {
                if (!foundMatch) {
                    ui.showDateTasksHeader(displayDate);
                    foundMatch = true;
                }
                ui.showNumberedTask(i + 1, task);
            }
        }
        if (!foundMatch) {
            ui.showNoDateTasks(displayDate);
        }
    }
}
