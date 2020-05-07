# Description

Users often use files to save their progress and restore it the next time they run the program. It's tedious to print the actions manually. Sometimes you can just forget to do it! So let's add run arguments that define which file to read at the start and which file to save at the exit.

To read an initial cards set from an external file, you should pass the argument ***-import*** and follow it with the file name. If the argument is present, the first line of your program output should be ***10 cards have been loaded.*** (hereinafter, replace 10 with the number of cards). If such argument is not set, the set of cards should be initially empty.

If the ***-export*** argument is set and it is followed by the file name, you should write all the cards that are in the program memory into this file after the user has entered exit, and the last line of your program should be ***10 cards have been saved.***.

For additional information [please visit](https://hyperskill.org/projects/44/stages/238/implement).
