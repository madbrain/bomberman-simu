bomberman-simu
==============

BomberMan-Simu est un jeu de combat de programmes, le jeu se déroule dans l'arêne du fameux jeu `BomberMan`.

`bomberman-simu` est le programme permettant de simuler une partie entre plusieurs programmes «joueurs».
Pour participer, un programme communique avec le simulateur par socket TCP/IP,
les échanges entre les programmes et le simulateur sont régis par un protocole textuel basé lignes.

Le protocole
------------

Les étapes suivantes donnent la définition du protocole:

1. Le programme joueur se connecte au serveur sur le port TCP/IP 9000 et envoie son nom de joueur
suivi d'un retour chariot (`\n`, caractère ASCII 10): `Patator\n`

2. Dès la connexion le serveur envoie très régulièrement l'état du jeu:
<pre>\n
X E ... E M\n
M S ... E M\n
...\n
M S ... M M\n
i\n
x1 y1 ALIVE\n
x2 y2 KILLED\n
</pre>
  * l'ensemble du plateau de jeu est envoyé, le plateau de fait 13 cases de large sur 11 de haut, les valeurs possibles des cases sont:
    * `EM` (Empty) est une case vide
    * `BL` (Block) est un block indestructible
    * `WA` (Wall) est un mur destructible
    * `BO` (Bomb) est une bombe (qui va exploser)
    * `FI` (Fire) est une case en feu
    * `BB` (Bonus Bomb) est l'option Bomb
    * `BF` (Bonus Fire) est l'option Fire
    * `BR` (Bonus Roller) est l'option Roller
  * suit une série, une pour chaque joueur, de `i` position suivi de ALIVE ou KILLED indiquant l'état du joueur.
  * le joueur recevant le flux est toujours en premier dans la liste.
  
3. A tout moment le joueur peut envoyer des commandes pour se déplacer :
  * `UP` pour se deplacer vers le haut
  * `DOWN` vers le bas
  * `LEFT` vers la gauche
  * `RIGHT` vers la droite
  * `NOP` pour indiquer aucun mouvement
  
La commande de déplacement peut être accompagnée de la commande `BOMB` pour poser une bombe
(par exemple `LEFT BOMB\n` pour se déplacer à gauche et poser une bombe). Si aucune commande
n'est envoyée le joueur continue de se déplacer dans la direction précédente. Par contre la commande
`BOMB` est à effet instantané et unique.

Position de démarrage
---------------------

Au départ, chaque joueur démarre dans un des coin de la carte.

Déplacements
------------

Les joueurs peuvent se déplacer librement sur les cases vides. Si au moins la moitier d'un joueur
dépasse d'une case bloquante (Block ou Wall) il peut se déplacer perpendiculairement (ou péremptoire ça dépend)
et est automatique recentré sur la case vide (oui je sais, c'est pas clair).

Options
-------

  * l'option Bomb permet de poser une bombe de plus simultanemment
  * l'option Fire permet d'augmenter la taille des explosions
  * l'option Roller permet de se déplacer plus vite

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


