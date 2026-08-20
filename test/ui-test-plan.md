# Console UI test plan

## Execution information

- Program: `NotMarth`
- Source directory: `src/main/java`
- Runtime: Java 25
- Build command: `javac -d <temporary-output-directory> src/main/java/*.java`
- Run command: `java -cp <temporary-output-directory> NotMarth`
- Each test case runs in a fresh process.
- The test runner records the complete console input and output for each case.
- A nonzero exit status or unexpected standard-error output fails the current test case.

## Test case 1: Add a ToDo

### Aim

Verify that a task without a date or time is stored and displayed as a ToDo.

### Comparison

`contains`

### Input

```text
todo borrow book
```

### Expected output

```text
Order received. I've added it to the battle plan:
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
```

### Expected output

```text
Order received. I've added it to the battle plan:
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
```

### Expected output

```text
Order received. I've added it to the battle plan:
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
```

### Expected output

```text
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

## Test case 5: Reject an empty ToDo without changing state

### Aim

Verify that an empty ToDo is rejected and does not create an extra task between two valid ToDos.

### Comparison

`contains`

### Input

```text
todo buy milk
todo
todo read book
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: A todo needs a description. Try: todo <description>
Order received. I've added it to the battle plan:
1.[T][ ] buy milk
2.[T][ ] read book
```

## Test case 6: Reject incomplete Deadlines between valid Deadlines

### Aim

Verify that a Deadline without its description, `/by` marker, or due-time value is rejected and does not affect the numbering of later valid tasks.

### Comparison

`contains`

### Input

```text
deadline submit report /by Friday
deadline missing due date
deadline /by Sunday
deadline missing due value /by
deadline prepare presentation /by Monday
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: A deadline needs a description and a due time. Try: deadline <description> /by <date or time>
I couldn't process that, Divine One: A deadline needs a description and a due time. Try: deadline <description> /by <date or time>
I couldn't process that, Divine One: A deadline needs a description and a due time. Try: deadline <description> /by <date or time>
Order received. I've added it to the battle plan:
1.[D][ ] submit report (by: Friday)
2.[D][ ] prepare presentation (by: Monday)
```

## Test case 7: Reject incomplete Events without affecting later tasks

### Aim

Verify that Events missing a description, start time, or end time are rejected while valid tasks remain in the list.

### Comparison

`contains`

### Input

```text
event team meeting /from 10am /to 11am
event missing end time /from 2pm
event /from 2pm /to 3pm
event missing start time /from /to 3pm
todo pack presentation materials
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: An event needs a description, start time, and end time. Try: event <description> /from <start> /to <end>
I couldn't process that, Divine One: An event needs a description, start time, and end time. Try: event <description> /from <start> /to <end>
I couldn't process that, Divine One: An event needs a description, start time, and end time. Try: event <description> /from <start> /to <end>
Order received. I've added it to the battle plan:
1.[E][ ] team meeting (from: 10am to: 11am)
2.[T][ ] pack presentation materials
```

## Test case 8: Ignore an unknown command between valid tasks

### Aim

Verify that an unknown command is rejected rather than stored as a generic task, preserving the numbering of valid tasks entered afterward.

### Comparison

`contains`

### Input

```text
todo first task
blah
deadline second task /by tomorrow
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or delete.
Order received. I've added it to the battle plan:
1.[T][ ] first task
2.[D][ ] second task (by: tomorrow)
```

## Test case 9: Preserve completion state after invalid mark commands

### Aim

Verify that invalid mark and unmark commands do not change a task's completion state, while valid mark and unmark commands still work.

### Comparison

`contains`

### Input

```text
todo finish assignment
mark nope
mark 2
mark 1
unmark 0
unmark 1
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: Mark needs a task number, for example: mark 1
I couldn't process that, Divine One: That task number is not in your list. Use a number from 1 to 1.
Well fought! This order is complete:
Together, we can accomplish this. Engage!
[T][X] finish assignment
This order is back on the map:
[T][ ] finish assignment
1.[T][ ] finish assignment
```

## Test case 10: Reject blank input between valid tasks

### Aim

Verify that a blank line is rejected and does not create an empty task or change the numbering of valid tasks.

### Comparison

`contains`

### Input

```text
todo first task

todo second task
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: Please enter a command. Try todo, deadline, event, list, mark, unmark, or delete.
Order received. I've added it to the battle plan:
1.[T][ ] first task
2.[T][ ] second task
```

## Test case 11: Handle missing mark and unmark numbers

### Aim

Verify that mark and unmark commands without valid numeric arguments are rejected and leave the existing task unchanged.

### Comparison

`contains`

### Input

```text
todo keep task unchanged
mark
unmark abc
mark 999999999999999999999
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: Mark needs a task number, for example: mark 1
I couldn't process that, Divine One: Unmark needs a task number, for example: unmark 1
I couldn't process that, Divine One: Mark needs a task number, for example: mark 1
1.[T][ ] keep task unchanged
```

## Test case 12: Reject a task after reaching capacity

### Aim

Verify that the 101st task is rejected and that the first, middle, and last entries among the 100 stored tasks remain available.

### Comparison

`contains`

### Input

```text
todo task 1
todo task 2
todo task 3
todo task 4
todo task 5
todo task 6
todo task 7
todo task 8
todo task 9
todo task 10
todo task 11
todo task 12
todo task 13
todo task 14
todo task 15
todo task 16
todo task 17
todo task 18
todo task 19
todo task 20
todo task 21
todo task 22
todo task 23
todo task 24
todo task 25
todo task 26
todo task 27
todo task 28
todo task 29
todo task 30
todo task 31
todo task 32
todo task 33
todo task 34
todo task 35
todo task 36
todo task 37
todo task 38
todo task 39
todo task 40
todo task 41
todo task 42
todo task 43
todo task 44
todo task 45
todo task 46
todo task 47
todo task 48
todo task 49
todo task 50
todo task 51
todo task 52
todo task 53
todo task 54
todo task 55
todo task 56
todo task 57
todo task 58
todo task 59
todo task 60
todo task 61
todo task 62
todo task 63
todo task 64
todo task 65
todo task 66
todo task 67
todo task 68
todo task 69
todo task 70
todo task 71
todo task 72
todo task 73
todo task 74
todo task 75
todo task 76
todo task 77
todo task 78
todo task 79
todo task 80
todo task 81
todo task 82
todo task 83
todo task 84
todo task 85
todo task 86
todo task 87
todo task 88
todo task 89
todo task 90
todo task 91
todo task 92
todo task 93
todo task 94
todo task 95
todo task 96
todo task 97
todo task 98
todo task 99
todo task 100
todo task 101
list
```

### Expected output

```text
Now you have 100 tasks in the list.
I couldn't process that, Divine One: Your task list is full. Remove a task before adding another one.
1.[T][ ] task 1
50.[T][ ] task 50
100.[T][ ] task 100
```

## Test case 13: Delete a task and renumber the remaining tasks

### Aim

Verify that deleting a task removes it from the list and shifts later tasks so their numbering remains consecutive.

### Comparison

`contains`

### Input

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
todo borrow book
delete 3
list
```

### Expected output

```text
This order has been withdrawn:
[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 4 tasks in the list.
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)
3.[T][ ] join sports club
4.[T][ ] borrow book
```

## Test case 14: Reject invalid delete commands without changing state

### Aim

Verify that delete requires a task number and does not change the list when the number is missing or out of range.

### Comparison

`contains`

### Input

```text
todo keep this task
delete nope
delete 2
list
```

### Expected output

```text
I couldn't process that, Divine One: Delete needs a task number, for example: delete 1
I couldn't process that, Divine One: That task number is not in your list. Use a number from 1 to 1.
1.[T][ ] keep this task
```

## Test case 15: Reject task operations on an empty list

### Aim

Verify that mark, unmark, and delete commands report clear errors instead of failing when no tasks exist.

### Comparison

`contains`

### Input

```text
mark 1
unmark 1
delete 1
list
```

### Expected output

```text
I couldn't process that, Divine One: There are no tasks yet. Add a task before marking it.
I couldn't process that, Divine One: There are no tasks yet. Add a task before unmarking it.
I couldn't process that, Divine One: There are no tasks yet. Add a task before deleting it.
Here are your current mission orders:
```

## Test case 16: Exit the chatbot

### Aim

Verify that the `bye` command prints the farewell message and terminates normally.

### Comparison

`contains`

### Input

```text
bye
```

### Expected output

```text
Hello! I'm NotMarth, your not-quite-Emblem tactical assistant.
The Fell Dragon may be gone, but every battle still needs a plan.
What tactical command can I assist with?
Until we meet again. Stay strong, Divine One!
```

## Test case 17: Discover the Sommie Easter egg

### Aim

Verify that the hidden `sommie` command displays Sommie’s companion message without changing task functionality.

### Comparison

`contains`

### Input

```text
sommie
```

### Expected output

```text
Sommie appears with a cheerful wag. Your battle plan has a loyal companion!
```
