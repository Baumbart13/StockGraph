# StockGraph

Programmablauf:

- API abfragen
- gelieferte Daten in MySQL einspielen (Pro Aktie ist eine eigene Tabelle am optimalsten)
- Sind Daten schon vorhanden in Tabellen, neue Daten Updaten (vielleicht wurde was korrigiert, neue Daten sind immer „korrekter“)
- 200 Tage Durschnitt berechnen, für alle abgespeicherte Aktien
- Chart zeichnen


Chartvorlage:
- siehe Lösung Laser Tobias gezeigt am 2021.01.20
  https://github.com/htlLaser/4ahwii_Programmieren/tree/master/Project%202%20-%20StockMarket


Tips zur Chartdarstellung

* [ ] X-Achse Datum, nicht zuviele Datumstrings, reichen ca. 10
* [x] Y-Achse nicht bei Null beginnen, reicht 10% unterm MIN-Wert und 10% über MAX-Wert (Xcharts macht das automatisch)
* [x] ist der letzte Close wert überm 200er, dann Chart-Hintergrund grün, sonst rot
* [x] keine „Knubbel“/Kreise in den Plots, nur eine Linie


Erweiterungen im 2ten Semester:

* [ ] Splitcorrection
* [ ] Beginn und Enddatumangabe vorm Zeichnen für die Charts
* [x] Charts als Bild abspeichern
* [x] Mehrere Aktien hintereinander von der API abrufen
* [x] Mehrere Aktien hintereinander zeichnen
* [x] Durchschnitte dynamisch in Datenbank hinterlegen und abrufen
* [ ] Mehrere Charts auf eine mini-Website anzeigen
