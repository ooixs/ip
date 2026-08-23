package notmarth.command;

import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes the hidden Sommie Easter egg command. */
public final class SommieCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showSommieMessage();
    }
}
