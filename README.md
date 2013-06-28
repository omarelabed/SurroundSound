SurroundSound
=============

SurroundSound is a PD-Net application that shows facebook events of a list of organizers (given page name or page id).

Anatomy
-------
The project is divided and organized in its usual Play Framework 2.0 subfolders, i.e.
- `app/controllers/` which contains the main *SurroundSoundController.java*;
- `app/models/` for the SQL database management: most important files for event management in SurroundSound specifically are `SurroundSoundFbPage.java`, `SurroundSoundFbEvent.java`;
- `app/views/` contains all client-side needed files, i.e. the GUI written in HTML/Javascript;
- `conf/` which contains the configuration files like `routes` for managing urls and `application.conf` for managing general application configurations;
- `lib/` contains additional libraries that are added to the project;
- `public/` contains mainly client-side resources like `images/`, `javascripts/` and `stylesheets/`.

A large part of the code (client side) is written in javascript and can be found in `app/views/surroundSound.scala.html`.

See http://www.playframework.com/documentation/2.0/Anatomy for further information on the Play 2.0 anatomy.

Setup
-----
### websockets
Edit `wsAddress` in `app/controllers/SurroundSoundController.java` for setting the client-server communication websocket.
### Database
Edit `db.default.url`
in `conf/application.conf` to set the SQL server.
### Organizers
Edit `pageNamesArr` in `app/controllers/SurroundSoundController.java` for managing the list of organizers' facebook pages.

Note that the names have to correspond to the official id page name, use the id number direclty otherwise.

E.g.:
*arteurbanalugano* is the id page name of https://www.facebook.com/arteurbanalugano,
but *Living-Room* isn't the id page name of https://www.facebook.com/pages/Living-Room/52722248335: use *52722248335* instead.

Tip: always check the *pageid* with https://www.facebook.com/
*pageid*

Run
---
Once Play 2.0 is setup (see http://www.playframework.com/documentation/2.0/Home), the Play console can be used to run the application (http://www.playframework.com/documentation/2.0/PlayConsole)

Use
---
For using the application, just connect to the websocket (see *Setup* section) with a web-browser.


PD-NET: http://pd-net.org/