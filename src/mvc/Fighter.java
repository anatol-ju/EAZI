package mvc;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;

import java.io.Serializable;

public class Fighter extends SimpleObjectProperty<Fighter> implements Serializable {

    private String name;
    private SimpleIntegerProperty ini;
    private int previousIni = -1;
    private int modAT;
    private int modPosition;
    private int modOrientieren;
    private int modLaden;
    private int modZiehen;
    private boolean isSelected;
    private javafx.scene.paint.Color color;

    public Fighter() {
        name = "(leer)";
        ini = new SimpleIntegerProperty(7);
        modAT = 0;
        modPosition = 0;
        modOrientieren = 0;
        modLaden = 0;
        modZiehen = 0;
        isSelected = false;
        color = Color.LIMEGREEN;
    }

    public Fighter(Fighter fighter) {
        if(fighter == null) {
            fighter = new Fighter();
        }
        this.name = fighter.getName();
        this.ini = new SimpleIntegerProperty(fighter.getIni());
        this.modAT = fighter.getModAT();
        this.modPosition = fighter.getModPosition();
        this.modOrientieren = fighter.getModOrientieren();
        this.modLaden = fighter.getModLaden();
        this.modZiehen = fighter.getModZiehen();
        this.previousIni = fighter.getPreviousIni();
        this.isSelected = fighter.isSelected();
        this.color = fighter.getColor();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIni() {
        return ini.get();
    }

    public ObservableIntegerValue iniProperty() {
        return ini;
    }

    public void setIni(int ini) {
        this.ini.set(ini);
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

    public int getModOrientieren() {
        return modOrientieren;
    }

    public void setModOrientieren(int modOrientieren) {
        this.modOrientieren = modOrientieren;
    }

    public int getModLaden() {
        return modLaden;
    }

    public void setModLaden(int modLaden) {
        this.modLaden = modLaden;
    }

    public int getModZiehen() {
        return modZiehen;
    }

    public void setModZiehen(int modZiehen) {
        this.modZiehen = modZiehen;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public javafx.scene.paint.Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getPreviousIni() {
        return previousIni;
    }

    public void setPreviousIni(int previousIni) {
        this.previousIni = previousIni;
    }

    /**
     * Setzt die Attribute des Parameters und kopiert die Werte.
     * @param fighter
     */
    public void setFighter(Fighter fighter) {
        this.name = fighter.getName();
        this.ini.set(fighter.getIni());
        this.modAT = fighter.getModAT();
        this.modPosition = fighter.getModPosition();
        this.modOrientieren = fighter.getModOrientieren();
        this.modLaden = fighter.getModLaden();
        this.modZiehen = fighter.getModZiehen();
        this.color = fighter.getColor();
    }

    @Override
    public String toString() {
        return (this.getName() + " " + this.getIni());
    }

}
