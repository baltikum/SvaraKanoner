Svåra Kanoner
=============

Svåra kanoner is a social game about how things can easily can corrupted when spread. It's recommended for 4-8 players.

How it's played
----

<ol>
<li>First every player picks a word except if there is an uneven amount of players then one player waits a turn, this applies
for all upcoming steps.</li>
<li>The player then draws his choosen word and passes that drawing to the next player. </li>
<li>Then the player guesses what word the drawing represents and passes the guess to the next player. </li>
<li>Steps 2 and 3 are repeated until the word has passed every player.</li>
<li>When the word has gone through all the players it's revealed how you did.</li>
<li>Steps 2 to 5 are repeated for how many rounds you choose in the beginning.</li>
</ol>

<strong>Hav fun!</strong>

Setup
-----
The program exists of two applications, one client and one server. </br>
The server needs to be running before the application and is watching on port 12345 so you need to port 
forward it and allow it through your firewall if not running on a local network. Inorder to change the ip address
the ipAddress line in settings.ini needs to be changed. If you don't see the settings.ini file first start the client and
then stop it again then it should appear. 


Technical mumbo jumbo
-----
The game is heavily divided into phases both on the server, and the client side. All the common data is stored in
the class Game on the client side and GameSession on the server side. The server supports multiple GameSessions to be
active at once. </br></br>

The respective classes also have a currentPhase field originating from the abstract class Phase that has a message method.
Any messages are then given to the currentPhase that is responsible for the appropriate messages it needs to send/receive.
Some messages are however treated outside the phase as they are not restricted to a certain phase, examples are GOTO, CHAT_MESSAGE,
 JOIN_GAME, CREATE_GAME. To change phase you either call Game.game.setCurrentPhase(...) or session.setCurrentPhase(...). </br></br>
S
All communication between the server and client uses the Message class. The most important field is the type filed. This
is the message type and should be used to decide to whom the message is meant and what it includes. All remaining data should
be but in the data map in the Message instance. 
