package mvc;

import javafx.scene.paint.Color;

public class EnemyFighter extends Fighter {

    private int level;

    /**
     * Erstellt eine Klasse für Gegner.
     * Diese enthält zusätzliche Parameter, die für die von Spielern
     * gesteuerten Kämpfer keine Verwendung haben.
     * Es wird empfohlen, den Konstruktor mit Parameter zu verwenden und damit
     * die Attribute zu erhalten.
     */
    public EnemyFighter() {
        super();
        super.setColor(Color.RED);
    }

    public EnemyFighter(Fighter fighter) {
        super(fighter);
        super.setColor(Color.RED);
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
