StockGraph
============

### Programmablauf:

- API abfragen
- gelieferte Daten in MySQL einspielen (Pro Aktie ist eine eigene Tabelle am optimalsten)
- Sind Daten schon vorhanden in Tabellen, neue Daten Updaten (vielleicht wurde was korrigiert, neue Daten sind immer „korrekter“)
- 200 Tage Durschnitt berechnen, für alle abgespeicherte Aktien
- Chart zeichnen


### Chartvorlage:

- siehe [Lösung Laser Tobias](https://github.com/htlLaser/4ahwii_Programmieren/tree/master/Project%202%20-%20StockMarket) gezeigt am 2021.01.20


### Tipps zur Chartdarstellung

* [ ] X-Achse Datum, nicht zu viele Datumstrings, reichen ca. 10
* [x] Y-Achse nicht bei Null beginnen, reicht 10% unterm MIN-Wert und 10% über MAX-Wert (Xcharts macht das automatisch)
* [x] ist der letzte close-Wert überm 200er, dann Chart-Hintergrund grün, sonst rot
* [x] keine „Knubbel“/Kreise in den Plots, nur eine Linie


### Erweiterungen im 2ten Semester:

* [ ] Splitcorrection
* [ ] Beginn und Enddatumangabe vorm Zeichnen für die Charts
* [x] Charts als Bild abspeichern
* [x] Mehrere Aktien hintereinander von der API abrufen
* [x] Mehrere Aktien hintereinander zeichnen
* [ ] Mehrere Charts auf eine mini-Website anzeigen


### Statistik:

![Lines of Code](https://img.shields.io/tokei/lines/github/Baumbart13/StockGraph)
![Repo Size](https://img.shields.io/github/repo-size/Baumbart13/StockGraph)
![Last Commit](https://img.shields.io/github/last-commit/Baumbart13/StockGraph)
