# NotMarth User Guide

NotMarth is a tactical companion for managing tasks and tutor contacts offline.

// Product screenshot goes here

// Product intro goes here

## Adding a contact

Use `contact` to save a person's name, phone number, and address.

Example:

`contact Mrs Tan /phone 81234567 /address 12 Engage Road #04-05`

NotMarth confirms the new contact and keeps it in the shared archive.

```
Contact added to the battle plan:
Mrs Tan (phone: 81234567, address: 12 Engage Road #04-05)
Now you have 1 contacts in the list.
```

## Managing contacts

Use `listcontacts` to display all contacts, `findcontact <keyword>` to search
contact names, and `deletecontact <number>` to remove a contact by its number.
Contact numbers are separate from task numbers.

Examples:

```text
listcontacts
findcontact Tan
deletecontact 1
```

The existing task commands (`todo`, `deadline`, `event`, `list`, `find`, `on`,
`mark`, `unmark`, and `delete`) continue to work unchanged.
