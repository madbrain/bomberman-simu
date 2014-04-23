bomberman-simu
==============

BomberMan-Simu est un jeu de combat de programmes, le jeu se déroule dans l'arêne du fameux jeu `BomberMan`.

`bomberman-simu` est le programme permettant de simuler une partie entre plusieurs programmes «joueurs».
Pour participer, un programme communique avec le simulateur par socket TCP/IP,
les échanges entre les programmes et le simulateur sont régis par un protocole textuel basé lignes.

Le protocole
------------

A écrire.

Position de démarrage
---------------------

Au départ, chaque joueur démarre dans un des coin de la carte.

Déplacements
------------

Les mienurs peuvent se déplacer librement sur les cases vides. Si au moins la moitier d'un joueur
dépasse dans le vide.

Gagnant de la partie
--------------------

Le gagant de la partie est celui qui le dernier survivant. Si tous les joueurs sont tués par la même
explosion, il y a match nul.

Lancement du simulateur
-----------------------

Pour lancer le simulateur exécuter la commande :
`bomberman-simu.bat server`

Il est possible de tester le simulateur avec un client controlable au clavier,
la commande suivante permet de lancer le client :
`bomberman-simu.bat client`


