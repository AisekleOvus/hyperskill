# Description
### Improve the application's interactivity. Ask the user for action and make it.

Support these actions:

* add a card: ***add***,
* remove a card: ***remove***,
* load cards from file: ***import***,
* save cards to file: ***export***,
* ask for a definition of some random cards: ***ask***,
* exit the program: ***exit***.

When entering the word *export*, the program should request a file name and write all currently available cards into this file.
When entering the word *import*, the program should request a file name and read all the cards written to this file.

You can use any format to save cards to the file. Tests do not check the content of the file, but they do check that all saved cards are loaded correctly.

In this stage, if you try to add a card with an existing term or an existing definition, the application must just reject it by printing an error message ([see example 1](https://hyperskill.org/projects/44/stages/236/implement)).

When you load cards from a file, you shouldn't erase the cards that aren't in the file. If the imported card already exists, it should update the old one (look at cards Japan and Moscow in the [example 2](https://hyperskill.org/projects/44/stages/236/implement)). It is guaranteed, that there won't be any conflicts with definitions in the tests.
