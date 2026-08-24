package notmarth.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

import notmarth.model.Deadline;
import notmarth.model.Event;
import notmarth.model.Task;
import notmarth.model.ToDo;

/**
 * Reads and writes the task list in a small, versioned text archive.
 *
 * <p>The default path is relative to the directory from which NotMarth is
 * launched, so the project can be moved to another computer or operating
 * system without changing any source code. A different path can be supplied
 * when creating the storage object.</p>
 */
public final class Storage {
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "notmarth.txt");
    private static final String HEADER = "# NotMarth battle plan v1";
    private final Path dataFile;

    /** Creates storage using the default NotMarth archive path. */
    public Storage() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates storage using a caller-supplied archive path.
     *
     * @param filePath the path of the task archive
     */
    public Storage(String filePath) {
        this(Path.of(filePath));
    }

    private Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Loads the saved tasks and reports whether startup had to recover from a
     * storage problem.
     *
     * @param maximumTasks the largest valid number of tasks
     * @return the loaded tasks and an optional startup warning
     */
    public LoadResult load(int maximumTasks) {
        if (!Files.exists(dataFile)) {
            return new LoadResult(new ArrayList<>(), null);
        }

        try {
            List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
            if (lines.isEmpty() || !HEADER.equals(lines.get(0))) {
                throw new CorruptTaskDataException();
            }

            ArrayList<Task> tasks = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                if (lines.get(i).isBlank()) {
                    continue;
                }
                if (tasks.size() == maximumTasks) {
                    throw new CorruptTaskDataException();
                }
                tasks.add(parseTask(lines.get(i)));
            }
            return new LoadResult(tasks, null);
        } catch (CorruptTaskDataException exception) {
            return new LoadResult(new ArrayList<>(),
                    "The saved battle plan is corrupted. Repair or remove the file before starting "
                            + "NotMarth again.");
        } catch (IOException exception) {
            return new LoadResult(new ArrayList<>(),
                    "I couldn't read the saved battle plan from disk. Fix the file before starting "
                            + "NotMarth again.");
        }
    }

    /**
     * Contains tasks loaded at startup and an optional warning for the user.
     */
    public static final class LoadResult {
        private final ArrayList<Task> tasks;
        private final String warning;

        private LoadResult(ArrayList<Task> tasks, String warning) {
            this.tasks = tasks;
            this.warning = warning;
        }

        /**
         * Returns the tasks recovered from disk.
         *
         * @return the loaded tasks, or an empty list after a storage problem
         */
        public ArrayList<Task> getTasks() {
            return tasks;
        }

        /**
         * Returns whether startup should show a storage warning.
         *
         * @return {@code true} when the archive could not be used
         */
        public boolean hasWarning() {
            return warning != null;
        }

        /**
         * Returns the warning that explains a storage problem.
         *
         * @return the warning text, or {@code null} when loading succeeded
         */
        public String getWarning() {
            return warning;
        }
    }

    /**
     * Saves the current task list, creating its parent directory if needed.
     * A temporary file is moved into place so an interrupted write is less
     * likely to destroy the previous valid archive.
     *
     * @param tasks the current task list
     * @throws IOException if the archive cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path parent = dataFile.getParent();
        if (parent == null) {
            parent = Path.of(".");
        }
        Files.createDirectories(parent);

        ArrayList<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Task task : tasks) {
            lines.add(serializeTask(task));
        }

        Path temporaryFile = Files.createTempFile(parent, "notmarth-", ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, dataFile,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException | UnsupportedOperationException exception) {
                Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Converts one saved record into a task object.
     *
     * @param line the saved record
     * @return the parsed task
     * @throws CorruptTaskDataException if the record is not valid
     */
    private static Task parseTask(String line) throws CorruptTaskDataException {
        List<String> fields = splitRecord(line);
        if (fields.size() < 3) {
            throw new CorruptTaskDataException();
        }

        String type = fields.get(0);
        boolean isDone = parseCompletion(fields.get(1));
        Task task;
        try {
            switch (type) {
            case "todo":
                requireFieldCount(fields, 3);
                task = new ToDo(requireText(fields.get(2)));
                break;
            case "deadline":
                requireFieldCount(fields, 4);
                task = new Deadline(requireText(fields.get(2)), requireText(fields.get(3)));
                break;
            case "event":
                requireFieldCount(fields, 5);
                task = new Event(
                        requireText(fields.get(2)), requireText(fields.get(3)), requireText(fields.get(4)));
                break;
            default:
                throw new CorruptTaskDataException();
            }
        } catch (DateTimeException | IllegalArgumentException exception) {
            throw new CorruptTaskDataException();
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Converts a task to the custom archive format.
     *
     * @param task the task to serialize
     * @return one archive record
     * @throws IllegalStateException if an unsupported task subtype is found
     */
    private static String serializeTask(Task task) {
        String status = task.isDone() ? "done" : "open";
        switch (task.getTaskType()) {
        case TODO:
            return String.join("|", "todo", status, escape(task.getDescription()));
        case DEADLINE:
            Deadline deadline = (Deadline) task;
            return String.join("|", "deadline", status, escape(task.getDescription()),
                    escape(deadline.getByForStorage()));
        case EVENT:
            Event event = (Event) task;
            return String.join("|", "event", status, escape(task.getDescription()),
                    escape(event.getFromForStorage()), escape(event.getToForStorage()));
        default:
            throw new IllegalStateException("Unsupported task type");
        }
    }

    /**
     * Splits a record while respecting escaped pipes and backslashes.
     *
     * @param line the record to split
     * @return decoded fields
     * @throws CorruptTaskDataException if an escape sequence is incomplete
     */
    private static List<String> splitRecord(String line) throws CorruptTaskDataException {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (escaping) {
                switch (character) {
                case '\\':
                    field.append('\\');
                    break;
                case '|':
                    field.append('|');
                    break;
                case 'n':
                    field.append('\n');
                    break;
                case 'r':
                    field.append('\r');
                    break;
                default:
                    throw new CorruptTaskDataException();
                }
                escaping = false;
            } else if (character == '\\') {
                escaping = true;
            } else if (character == '|') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (escaping) {
            throw new CorruptTaskDataException();
        }
        fields.add(field.toString());
        return fields;
    }

    private static boolean parseCompletion(String status) throws CorruptTaskDataException {
        if ("open".equals(status)) {
            return false;
        }
        if ("done".equals(status)) {
            return true;
        }
        throw new CorruptTaskDataException();
    }

    private static void requireFieldCount(List<String> fields, int expected)
            throws CorruptTaskDataException {
        if (fields.size() != expected) {
            throw new CorruptTaskDataException();
        }
    }

    private static String requireText(String value) throws CorruptTaskDataException {
        if (value.isEmpty()) {
            throw new CorruptTaskDataException();
        }
        return value;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /** Signals that a saved record does not follow the archive format. */
    private static final class CorruptTaskDataException extends Exception {
        private static final long serialVersionUID = 1L;
    }
}
