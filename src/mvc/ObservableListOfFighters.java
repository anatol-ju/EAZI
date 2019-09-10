package mvc;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Diese Klasse verwaltet alle Teilnehmer und stellt Methoden zum Einfügen und
 * entfernen von Einträgen der Liste bereit. Außerdem kann sie von anderen
 * Klassen observiert werden.
 */
public class ObservableListOfFighters extends SimpleListProperty<Fighter> implements ChangeListener {
    private boolean changing = false;
    public ObservableListOfFighters() {

        // Diese Liste muss durch eine Observable gedeckt sein.
        super(FXCollections.synchronizedObservableList(FXCollections.observableList(new LinkedList<>())));

    }

    /**
     * Fügt einen neuen Teilnehmer zur Liste hinzu.
     * @param fighter
     */
    public void addFighter(Fighter fighter) {

        if(fighter == null || contains(fighter)) {
            return;
        }

        synchronized (this) {
            fighter.addListener(this);
            fighter.iniProperty().addListener(this);

            int ini = fighter.getIni();
            int currentIni = 0;
            int index = 0;

            for (index = 0; index < size(); index++) {
                currentIni = get(index).getIni();

                if (ini < currentIni) {
                    if (index == (size() - 1)) {
                        add(size()-1, fighter);
                        break;
                    } else {
                        continue;
                    }
                }

                if (ini == currentIni) {
                    if (index == (size() - 1)) {
                        add(size()-1, fighter);
                        break;
                    } else {
                        continue;
                    }
                }

                if (ini > currentIni) {
                    if (index == 0) {
                        add(0, fighter);
                        break;
                    } else {
                        add(index, fighter);
                        break;
                    }
                }
            }

            if(this.size() == 0) {
                add(0, fighter);
            }
        }

    }

    /**
     * Entfernt einen Teilnehmer aus der Liste.
     * @param fighter
     */
    public void removeFighter(Fighter fighter) {

        if(fighter == null) {
            return;
        }

        synchronized (this) {
            fighter.iniProperty().removeListener(this);

            Fighter toRemove = null;

            for (Fighter f: this) {
                if(f.getName().equals(fighter.getName())) {

                    toRemove = f;
                    break;
                }
            }

            if(toRemove != null) {
                toRemove.removeListener(this);

                remove(getIndex(toRemove));
            }
        }
    }

    /**
     * Prüft ob ein Teilnehmer mit dem gleichen Namen bereits in der Liste ist.
     * @param fighter
     * @return true wenn Teilnehmer bereits in der Liste ist, sonst false
     */
    public boolean contains(Fighter fighter) {
        if(fighter == null) {
            return true;
        }
        String name = fighter.getName();
        for (Fighter f : this) {
            if(f.getName().equals(fighter.getName())) {
                return true;
            }
        }
        return false;
    }

    public int getIndex(Fighter fighter) {
        for (int index = 0; index < this.size(); index++) {
            if(this.get(index).toString().equals(fighter.toString())) {
                return index;
            }
        }

        return -1;
    }

    /**
     * Ändert die INI des Teilnehmers in der Liste um den angegebenen Betrag.
     * Zusätzlich wird die Position in der Liste an die INI angepasst.
     * @param fighter
     * @param range
     */
    public void action(Fighter fighter, int range) {
        int ini = fighter.getIni();
        ini = ini - range;
        fighter.setIni(ini);

        updateEntries();
    }

    /**
     * Wenn der erste Teilnehmer in der Liste eine INI kleiner als 0 hat,
     * also wenn alle an der Reihe waren, wird die INI aller Teilnehmer
     * um 12 erhöht damit die Berechnung an den Aktionskreis angepasst wird.
     */
    private void updateEntries() {
        if(this.get(0).getIni() > 0) {
            return;
        }
        int ini;
        for (Fighter f : this) {
            ini = f.getIni() + 12;
            f.setIni(ini);
        }
    }

    private boolean isAllowedChange(Object oldValue, Object newValue) {

        boolean isAllowed = true;

        int previous = (int) oldValue;
        int current = (int) newValue;

        if(previous < 0 && current > 0) {
            isAllowed = false;
        }

        return isAllowed;
    }

    public List<Fighter> subListByIni(int ini) {

        List<Fighter> list = new LinkedList<>();

        for (Fighter fighter : this) {
            if(fighter.getIni() == ini) {
                list.add(fighter);
            }
        }

        return list;
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        if(observable.getClass() == Fighter.class) {
            Fighter f = (Fighter) observable;
            System.out.println(f.toString());
            removeFighter(f);
            addFighter(f);
        }

        if(observable.getClass() == SimpleIntegerProperty.class) {
            if(!changing && !isAllowedChange(oldValue, newValue)) {
                changing = true;
                Platform.runLater(() -> {
                    SimpleIntegerProperty ini = (SimpleIntegerProperty) observable;
                    ini.set((int) oldValue);
                    changing = false;
                });
            }
        }
    }

    @Override
    public String toString() {
        String str = "";
        for (Fighter fighter : this) {
            str = str + fighter.getName() + " ";
        }
        System.out.println(str);
        return str;
    }

}
