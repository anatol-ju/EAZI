package mvc;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import java.io.Serializable;

public class Fighter extends SimpleObjectProperty<Fighter> implements Serializable {

    private String name;
    private int ini;
    private int previousIni;
    private int modAT;
    private int modPosition;
    private int modOrientate;
    private int modLoadBow;
    private int modDrawWeapon;
    private int maxIni;     // for compatibility checks
    private boolean isSelected;
    private final Color color = Color.LIMEGREEN; // default color

    /**
     * Basisklasse für Teilnehmer. Damit werden die Kämpfer beschrieben, die
     * von den Spielern gesteuert werden.
     * Um Verbündete zu erstellen steht die Unterklasse
     * <code>AllyFighter</code> und für Gegner <code>EnemyFighter</code>
     * zur Verfügung.
     */
    public Fighter() {
        name = "(leer)";
        ini = 7;
        previousIni = -1;
        modAT = 0;
        modPosition = 0;
        modOrientate = 0;
        modLoadBow = 0;
        modDrawWeapon = 0;
        maxIni = Integer.parseInt(Configuration.get().getProperty("actionCircleFieldCount"));
        isSelected = false;
    }

    /**
     * Liefert eine Kopie des Kämpfers als neue Instanz.
     * @param fighter das zu kopierende Objekt.
     */
    public Fighter(Fighter fighter) {
        if(fighter == null) {
            fighter = new Fighter();
        }
        this.name = fighter.getName();
        this.ini = fighter.getIni();
        this.modAT = fighter.getModAT();
        this.modPosition = fighter.getModPosition();
        this.modOrientate = fighter.getModOrientate();
        this.modLoadBow = fighter.getModLoadBow();
        this.modDrawWeapon = fighter.getModDrawWeapon();
        this.maxIni = fighter.getMaxIni();
        this.previousIni = fighter.getPreviousIni();
        this.isSelected = fighter.isSelected();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIni() {
        return this.ini;
    }

    public void setIni(int ini) {
        this.ini = ini;
    }

    public int getModAT() {
        return modAT;
    }

    public void setModAT(int modAT) {
        this.modAT = modAT;
    }

    public int getModPosition() {
        return modPosition;
    }

    public void setModPosition(int modPosition) {
        this.modPosition = modPosition;
    }

    public int getModOrientate() {
        return modOrientate;
    }

    public void setModOrientate(int modOrientate) {
        this.modOrientate = modOrientate;
    }

    public int getModLoadBow() {
        return modLoadBow;
    }

    public void setModLoadBow(int modLoadBow) {
        this.modLoadBow = modLoadBow;
    }

    public int getModDrawWeapon() {
        return modDrawWeapon;
    }

    public void setModDrawWeapon(int modDrawWeapon) {
        this.modDrawWeapon = modDrawWeapon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Color getColor() {
        return this.color;
    }

    public int getPreviousIni() {
        return previousIni;
    }

    public void setPreviousIni(int previousIni) {
        this.previousIni = previousIni;
    }

    public int getMaxIni() {
        return maxIni;
    }

    public void setMaxIni(int maxIni) {
        this.maxIni = maxIni;
    }

    /**
     * Setzt die Attribute des Parameters und kopiert die Werte.
     * @param fighter Objekt dessen Werte übernommen werden sollen.
     */
    public void setFighter(Fighter fighter) {
        this.name = fighter.getName();
        this.ini = fighter.getIni();
        this.modAT = fighter.getModAT();
        this.modPosition = fighter.getModPosition();
        this.modOrientate = fighter.getModOrientate();
        this.modLoadBow = fighter.getModLoadBow();
        this.modDrawWeapon = fighter.getModDrawWeapon();
        this.maxIni = fighter.getMaxIni();
    }

    @Override
    public String toString() {
        return (this.getName() + " " + this.getIni());
    }

}
