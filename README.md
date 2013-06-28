SurroundSound
=============

SurroundSound is a PD-Net application that shows facebook events of a list of organizers (given page name or page id).

1. Setup
--------

**websockets**
In *app/SurroundSoundController.java* set
	wsAddress
for the client-server communication websocket.

**Database**
In *conf/application.conf* set
	db.default.url
to the SQL server.

**Organizers**
Edit
	pageNamesArr
in *app/SurroundSoundController.java* for managing organizers' facebook pages. Please note that the names have to correspond to the official id page name, use the id number direclty otherwise.

E.g.:
*arteurbanalugano* is the id page name of https://www.facebook.com/arteurbanalugano,
but *Living-Room* isn't the id page name of https://www.facebook.com/pages/Living-Room/52722248335: use 52722248335 instead.

Tipp: always check the *pageid* with **https://www.facebook.com/***pageid*

2. Run
------
Once Play 2.0 is setup (see http://www.playframework.com/documentation/2.0/Home), the Play console can be used to run the application (http://www.playframework.com/documentation/2.0/PlayConsole)

3. Use
------
For using the application, just connect to the websocket (see *1. Setup*) with a web-browser.


PD-NET: http://pd-net.org/