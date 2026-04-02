## Collaborative editor

EQUIPE C2
Projet applicatiosn réseau L3-info/AMU.

Edition collaborative de documents texte
avec une architecture client/serveur

* Binôme 1: *EL BARAJ* *Anwar*
* Binôme 2: *KULE KYAKAKALA* *Saturnin*



Démarrer les Serveurs :

Ouvrez un terminal à la racine du projet et tapez :

.\\gradlew.bat runServers   (Lance le Dispatcher et les serveurs selon la configuration de peers.cfg)



Lancer un client (Interface Graphique) :

Ouvrez un nouveau terminal et tapez :

`.\\gradlew.bat run`

(Répétez cette commande dans plusieurs terminaux pour ouvrir plusieurs clients et tester la collaboration)



Lancer les tests de charge (Bots) :

`.\\gradlew.bat runBots`



nettoyage:

Nous avons programmé une tâche pour forcer la fermeture des serveurs fantômes en arrière-plan .

Pour nettoyer l'environnement manuellement, tapez :

`.\\gradlew.bat killOldServers`



Point clés du projet:

\-Sychronisation des modifications (`ADDL`, `MDFL`, `RMVL`)

\-Dispatcher: Redirection automatique des clients

\-Sauvegarde automatique des documents côté serveur dans le dossier `serveur\_sauvegardes`.



