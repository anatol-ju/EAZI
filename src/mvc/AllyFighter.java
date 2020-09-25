package mvc;

import javafx.scene.paint.Color;

public class AllyFighter extends Fighter {

    private int level;
    private static Color color; // default color

    /**
     * Erstellt eine Klasse für Verbündete.
     * Diese enthält zusätzliche Parameter, die für die von Spielern
     * gesteuerten Kämpfer keine Verwendung haben.
     * Es wird empfohlen, den Konstruktor mit Parameter zu verwenden und damit
     * die Attribute zu erhalten.
     */
    public AllyFighter() {
        super();
        color = Color.MEDIUMBLUE;
    }

    public AllyFighter(Fighter fighter) {
        super(fighter);
        color = Color.MEDIUMBLUE;
    }

    public static Color getColor() {
        return color;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Der Level des Teilnehmers sollte zwischen 0 und 6 liegen.
     * Er wird zur Berechnung der Werte für zufällige AT verwendet.
     * @param level Numerische Darstellung der Stufe.
     */
    public void setLevel(int level) {
        this.level = level;
    }
}
