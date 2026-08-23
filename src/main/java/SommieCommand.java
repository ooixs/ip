/** Executes the hidden Sommie Easter egg command. */
public final class SommieCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showSommieMessage();
    }
}
