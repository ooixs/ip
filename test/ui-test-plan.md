# Console UI test plan

## Execution information

- Program: `NotMarth`
- Source directory: `src/main/java`
- Runtime: Java 25
- Build command: `javac -d <temporary-output-directory> src/main/java/*.java`
- Run command: `java -cp <temporary-output-directory> NotMarth`
- Each test case runs in a fresh process.
- The test runner records the complete console input and output for each case.

## Test case 1: Add a ToDo

### Aim

Verify that a task without a date or time is stored and displayed as a ToDo.

### Comparison

`contains`

### Input

```text
todo borrow book
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
```

## Test case 2: Add a Deadline

### Aim

Verify that a deadline keeps its date/time text and displays the deadline marker and `(by: ...)` field.

### Comparison

`contains`

### Input

```text
deadline return book /by Sunday
bye
```

### Expected output

```text
Got it. I've added this task:
[D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
```

## Test case 3: Add an Event

### Aim

Verify that an event keeps its start and end text and displays the event marker and range.

### Comparison

`contains`

### Input

```text
event project meeting /from Mon 2pm /to 4pm
bye
```

### Expected output

```text
Got it. I've added this task:
[E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 tasks in the list.
```

## Test case 4: List all task types polymorphically

### Aim

Verify that ToDos, Deadlines, and Events can be stored together and listed through the common `Task` type.

### Comparison

`contains`

### Input

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```
