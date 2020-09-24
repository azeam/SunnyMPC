# Sunny MPC

Very basic Java (Swing) MPD client with limited functionality. Written as a small class assignment (see below).

![Sunny MPC](https://www.bufonaturvard.se/images/sunnympc3.png)

## Features and limitations  

- Browse and play music files on local or external MPD servers.    
- Automatic album cover fetching.  
- Automatic MPD server detection (limited to the default MPD port 6600 and LAN).  
- Basic playlist management.  

In it's current early state it is rather ugly, does not support password protected or manually entered servers. There is no advanced handling of playlists. No search function. All album cover pictures are stored in the application root folder etc. It is working but will probably not be a good experience for daily usage as of now.

## Usage  

Install and set up MPD. This is a fairly straightforward process that I won't go in to details about, see [musicpd.org](https://www.musicpd.org/doc/html/index.html).

If no servers have previously been found (the first time you start the application) it will automatically scan for servers on your local network and add them to the dropdown list at the top left side of the window. This process takes 10-20 seconds so be patient. When the list has been populated with servers you can select one of them and the available music on that server will be displayed below. If no music is displayed, click the "Update MPD" button to build (or refresh) the music database on the server. This can take a long time if you have a large music collection, as of now there is no indication when the process has finished and you will need to reconnect to the server to display the updated database. If you add more servers you can refresh the server list by clicking the "Find servers" button.  

In the tree view on your left you will see the available artists, if you double click an artist or click the arrow next to it a list of their albums will expand. When you click an album it will be added to the playlist (right side). If you double click an item in the playlist the file will start playing on the active server and the playlist will continue descending. While the file is playing the album cover art will be downloaded (unless it already exists) and displayed below, together with some information about the file being played.  

The playlist can be sorted by clicking on the header of the playlist (Artist, Title etc.). This will not affect the order of the playlist, to show the order of the playlist again you can click on the "Reset order" button.

The other top right buttons are rather self-explanatory: click "Stop" to stop the music, "Next"/"Previous" to move to the next/previous track in the playlist. You can also clear the playlist.

## Slutprojekt Programmering i Java EVXJUH20

*Ni skall skapa ett program som lösa ett av de tre problemen som förklaras här under.*  
*Ert program ska vara körbart för en person som inte har några programmeringskunskaper, så ni behöver göra följande:*  
*1. Ni skall skapa en användar manual som beskriver hur ert program ska användas.*  
*2. Ert program ska ha någon form av interface.*  

*All kod ska vara väl kommenterat och följa Java Code Conventions väl.*  

*De tre problemen.*  

*_1. Ljudspelare: För detta problem så ska ni skapa ett program som ska spela flera olika ljud, när användarna vill det._*   
*2. Bild hanterare: För detta problem så ska ni skapa ett program som kan ta emot en bild ska kunna manipulera bilden på minst tre vis (ex storlek, färg osv)*  
*3. Data format: För detta problem så ska ni skapa ett program som kan ta emot ett kalkylark(sample.csv) full med information och en skapa om det till ett XML dokument som har all info från kalkylark ordnat in Xml format.*  

*Alla variabler, metoder och klasser skall vara namngivna logiskt och ni ska kommentera ert kod väl.*