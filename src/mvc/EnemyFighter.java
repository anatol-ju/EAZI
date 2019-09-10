package mvc;

import javafx.scene.paint.Color;

public class EnemyFighter extends Fighter {

    private int level;

    public EnemyFighter() {
        super();
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
