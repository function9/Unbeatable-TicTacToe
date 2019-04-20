# Unbeatable Tic-Tac-Toe Bot
Tic-Tac-Toe game vs. AI that will always win or draw.
# Software
```
Programmed in Java
```
```
Used the Swing library for the GUI of the game and the message displays.
```
```
Board state is kept track of as an int[3][3] array with methods checking for win conditions every turn.
```
# Gameplay
Tic-Tac-Toe is a solved game, meaning that if both players only make optimal moves then every game will result in a draw.
The bot will always use a winning strategy. It will always try to win first, if it can't win this turn it will try to block a potential player win, if there is nothing to block then it will move randomly (besides the first two turns which have set patterns it will always play).
If the first two turns are played correctly, it will be impossible for the player to win.
