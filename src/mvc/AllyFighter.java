package mvc;

import javafx.scene.paint.Color;

public class AllyFighter extends Fighter {

    private int level;

    /**
     * Erstellt eine Klasse für Verbündete.
     * Diese enthält zusätzliche Parameter, die für die von Spielern
     * gesteuerten Kämpfer keine Verwendung haben.
     * Es wird empfohlen, den Konstruktor mit Parameter zu verwenden und damit
     * die Attribute zu erhalten.
     */
    public AllyFighter() {
        super();
        super.setColor(Color.MEDIUMBLUE);
    }

    public AllyFighter(Fighter fighter) {
        super(fighter);
        super.setColor(Color.MEDIUMBLUE);
    }

    public int getLevel() {
        return level;
    }

    /**
     * Der Level des Teilnehmers sollte zwischen 0 und 6 liegen.
     * Er wird zur Berechnung der Werte für zufällige AT verwendet.
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }
}
