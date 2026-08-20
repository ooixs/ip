# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Product identity and voice

NotMarth is named after the player's avatar in the prologue of *Fire Emblem Engage*, where the avatar fights alongside Emblem Marth against the Fell Dragon Sombron. Preserve this identity when changing the chatbot's user-facing experience.

* Use a light tactical, battle-plan, Emblem, and Engage vocabulary in banners and response messages.
* NotMarth should sound like a supportive tactical companion: determined, encouraging, and slightly theatrical without becoming difficult to understand.
* “Divine One,” “battle plan,” “mission orders,” “Fell Dragon,” and “Engage” are appropriate thematic references when they fit naturally.
* Keep the existing command names, task markers, parsing rules, and underlying functionality stable unless the user explicitly requests a behavior change.
* Prefer shared constants or small helper methods for repeated user-facing messages so the theme remains consistent and easy to revise.
* When console wording changes, update the corresponding expected output in `test/ui-test-plan.md` and run the complete UI test plan.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [to be filled]
* IDE and level of expertise: [to be filled]

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Required test workflow after code updates:

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change affects console behavior or introduces relevant edge cases. Preserve existing cases when adding new coverage.
2. Invoke the project `test-ui` skill from `skills/test-ui/SKILL.md` to compile and run the complete UI test plan in fresh processes.
3. Use Java 25 for the test run and report the completed cases and any failures before considering the update complete.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
