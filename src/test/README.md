# ProgramTester
Hierbei handelt es sich um einen einfachen Programm-Tester, welcher
die Outputs deines Programmes auf Korrektheit überprüfen kann.
Um zu starten musst du den "test"-Ordner in deinen "src" Ordner deines
Projektes kopieren. Außerdem könnte es notwendig sein JUnit-Abhängigkeiten zu Importieren.
In IntelliJ sollte dafür ein Benachrichtigungs-Feld aufkommen, bei dem du aufgerufen
wirst, dies mit einem Klick zu tun.

## So schreibst du einen Test
Füge eine .txt-Datei in den "testFiles"-Ordner. Diese Datei sollte
einen sinnvollen Namen besitzen, der beschreibt, was getestet wird.
In diese Datei kannst du nun Inputs, Outputs und Argumente schreiben:

Um einen erwarteten Output deines Programmes anzuführen, beginne deine Zeile mit
"<" und füge danach (ohne Leerzeichen nach "<") den String hinzu, den du als Output von deinem Programm erwartest
Beachte, dass du hier RegEx verwenden kannst um die Ausgaben zu überprüfen!

Um deinem  Programm eine Input-Zeile zu geben, beginne deine Zeile mit ">" und schreibe genauso wie beim Output
den String mit dem Input dahinter.

Falls du einen Fehler erwartest, schreibe die Zeile mit "E<". Dann wird überprüft, ob die Ausgabe mit der
ERROR_START Konstante ("Error,") beginnt.

Um deinem Programm Argumente beim Aufrufen deines Programms zu überreichen,
starte für je ein Argument eine neue Zeile mit "$" und füge dahinter das Argument.

## Ein Beispiel:
    $hello
    $argument
    <Enter a number:
    >5 2 8
    <\d+

Hier wird in den ersten beiden Zeilen zunächst die Argumente "hello" und "argument" definiert: String[] args = {"hello", "argument"}
In der nächszen Zeile wird definiert, dass das Programm als erstes den Output "Enter a number:" geben sollte.
In der nächsten Zeile übergibt der Tester dem Program "5 2 8" als Input, als würde ein Mensch dein Programm bedienen und diese Zeile eingeben.
Die letzte Zeile beschreibt, dass dein Test (mit Hilfe der RegEx-Notation) eine Zahl als Output erwartet.

## Das Ausführen des Tests
Um den Test ausführen zu können, musst du in die "MainTest" Klasse im "test/java"-Ordner eine Funktion schreiben.
Diese sollte ungefähr so aus sehen:

    @Test
    public void testeZahl() {
        test("testeZahl.txt");
    }

Benenne die Funktion sinnvoll und gebe den Namen deiner Test-Datei an.
Um den Test ausführen zu können, muss deine Main-Klasse "Main.java" und die main-Methode "main(String[] args)" heißen.
Importiere deine Main-methode vom richtigen package in "MainTest".
Falls deine Main-Klasse / Methode anders heißt, suche die Zeile "Main.main(args);" in "MainTest" und ändere sie nach deinem Wunsch ab.

## Hinweise
IntelliJ löscht, solange nicht ausgeschalten, Leerzeichen am Ende einer Zeile beim Spiechern, also auch bei deiner test.txt Datei.
Pass deshalb auf, dass deine Testfälle nicht deshalb scheitern!