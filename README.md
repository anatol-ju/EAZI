# EAZI (german)
Echte Alternative zur Initiative (für DSA)

[The real alternative to the original initiative system (for The Black Eye)]

## Important Information

Work in progress!

This application is running and working. Some bugs may still be around and will be worked on when noticed.
The released version can be used to play and covers all of the game's rules, but it lacks a few features. These will be implemented one by one. Follow the changelog and this README to be up to date.

For now, I will focus on my C# skills and therefore do updates on this application more rarely.

## Update 28.10.

The release version successfully ran on an english Win10 with Java 8 JRE installed.

## Update 26.10.

Updated the release version (.jar file). Version stays the same since only manifest file was changed. This needs to be tested to run on different machines.

## Background

This application is my first project to implement Java backend with JavaFX frontend.
It started out as "learning-by-doing" with only a few buttons on the user interface and some logic to make things work.
Meanwhile it advanced to include features like:
- model-view-controler (MVC)
- usage of complex data types (as Lists, Maps, ...)
- Inheritance
- Serialization (to XML files)
- Localization (with resource bundles)
- user-defined settings (with properties)
- graphics (on Javas canvas)
- custom dialogues
- log window with random-generated text based on events

### Current TODOs

- apply settings options
- allow to change number of fields on the circle
- overwork the serialization process
- refactor classes
- include Spring for IoC
- make circle view interactive

## Description

The famous german pen-and-paper roleplaying system "The Black Eye" got an upgrade!
Since the combat system is very complex and time-consuming, I created my own combat system (not covered here).
This application allows you to manage a list of combat participants (heroes, allies and enemies) and their respective initiative.
This means, the application helps you keep track on which fighter's turn it is and what action can be used.
It implements all the game's rules in it's logic, so you only need to focus on the action on the battlefield!
Keep an overview with the list of participants or with the graphic representation.

Create your group, save it. Load the progress for the next games night.

## Usage

1. Copy the code into a folder or use the zip file structure.
2. Open the project in an IDE like IntelliJ IDEA or Eclipse.
3. Select the "Main.java" file and run it.
