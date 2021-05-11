# Backtesting Software-Suite
Die Anwendung soll einen Börsenhandel simulieren. In dieser Börsenhandel-Simulation wird die 200er Linien-Strategie, die 200er +/- 3% (siehe weiter unten) und die Buy&Hold Strategie verglichen. Ziel ist die Strategien zu bewerten, welche Strategie die meiste Performance einbringt.

## Fertigstellung:
- Ende MAI 2021

## Grundsätzlicher Ablauf:
- gehe zu einem beliebigen Zeitpunkt in die Vergangenheit (z.B 2010.01.01)
- fülle dein Aktiendepot mit 100.000 USD
- wähle eine Aktie (z.B IBM)
- wenn die Aktie über seinen 200er Schnitt (an deinem gewähltem Datum) liegt, dann verwende all dein Kapital und kaufe die Aktie, sonst starte ab deinem Datum Richtung Gegenwart und suche einen Zeitpunkt wann die Aktie über den 200er Schnitt liegt und kauf dann erst.
- nach dem Kauf, suche Richtung Gegenwart den Zeitpunkt, wenn die Aktie unter dem 200er fällt und verkaufe dann die Aktie.
- danach wieder einen Zeitpunkt suche wo über dem 200er die Aktie liegt, dann kaufen, danach verkaufen, ...usw.
- das Ganze wird bis heute/Gegenwart gemacht, und am Ende wird alles verkauft.
- Dann wird analysiert ob man mehr oder weniger Geld im Konto hat und die prozentuale Veränderung wird ausgerechnet.

## Strategien
* [ ] 200er Strategie (wie im grundsätzlichen Ablauf beschrieben)
* [ ] 200er Strategie mit 3% Regel (erst dann kaufen/verkaufen, wenn der Kurs 3% über oder 3% unter dem Schnittpunkt mit der 200er Linie liegt, also jeweils ein wenig später wie bei der 200er Strategie)
* [ ] BuyAndHold Strategie (Startdatum kaufen, heute verkaufen, prozentuale Veränderung berechnen)
* [ ] 200er FALSCHE Strategie (Nur Schulerer und Niederhauser, kaufen unterhalb Schnittpunkt 200er, verkaufen über 200er Schnitt)

## Implementierungshinweise
Die einzelnen Trades, könnten in einer Tabelle mit folgenden Spalten gespeichert werden:

|date|ticker|flag|stücke|depotkonto|
|----|------|----|------|----------|
|wann fand dieser Trade statt|aktienticker (z.B. IBM)|buy/sell oder 1/0 als flag ob gekauft oder verkauft|wieviel Aktien habe ich|wie viel USD habe ich nach dem Handel/Trade

## HINWEISE / zum Überlegen:
- man kann nur ganze Aktien kaufen! man wird nicht das ganze Depotkonto investieren können
- was passiert wenn man eine Aktie im Depot hält, und ein Aktiensplit passiert (Danke Moritz!) -> Aktienanzahl muss im Depot verändert werden