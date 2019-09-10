package mvc;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;
import java.util.List;

/**
 * Diese Klasse verwaltet alle Teilnehmer und stellt Methoden zum Einfügen und
 * entfernen von Einträgen der Liste bereit. Außerdem kann sie von anderen
 * Klassen observiert werden.
 */
public class ListOfFighters extends LinkedList<Fighter> implements ChangeListener {

    private Fighter selectedFighter;
    private SimpleIntegerProperty observableSize;
    private ObservableList<Fighter> observableList;

    public ListOfFighters() {

        super();

        observableSize = new SimpleIntegerProperty(this.size());
        observableSize.addListener(this);

        observableList = FXCollections.synchronizedObservableList(FXCollections.observableList(this));

    }

    /**
     * Fügt einen neuen Teilnehmer zur Liste hinzu.
     * @param fighter
     */
    public void addFighter(Fighter fighter) {

        if(fighter == null || contains(fighter)) {
            return;
        }

        fighter.addListener(this);
        fighter.iniProperty().addListener(this);

        int ini = fighter.getIni();
        int currentIni = 0;
        int index = 0;

        for (index = 0; index < size(); index++) {
            currentIni = get(index).getIni();
            if (ini < currentIni) {
                if (index == (size() - 1)) {
                    this.addLast(fighter);
                    observableList.add(size()-1, fighter);
                    break;
                } else {
                    continue;
                }
            }

            if (ini == currentIni) {
                if (index == (size() - 1)) {
                    this.addLast(fighter);
                    observableList.add(size()-1, fighter);
                    break;
                } else {
                    continue;
                }
            }

            if (ini > currentIni) {
                if (index == 0) {
                    this.addFirst(fighter);
                    observableList.add(0, fighter);
                    break;
                } else {
                    this.add(index, fighter);
                    observableList.add(index, fighter);
                    break;
                }
            }
        }

        if(this.size() == 0) {
            this.addFirst(fighter);
            observableList.add(0, fighter);
        }

        observableSize.set(observableSize.get() + 1);
    }

    /**
     * Entfernt einen Teilnehmer aus der Liste.
     * @param fighter
     */
    public void removeFighter(Fighter fighter) {

        if(fighter == null) {
            return;
        }

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
            remove(toRemove);

            observableList.remove(getIndex(toRemove));
            observableSize.set(observableSize.get() - 1);
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

    public Fighter getSelectedFighter() {
        for (Fighter f : this) {
            if(f.isSelected()) {
                this.selectedFighter = f;
                break;
            }
        }
        return this.selectedFighter;
    }

    public void setSelectedFighter(Fighter selectedFighter, boolean state) {
        if(state) {
            this.selectedFighter = selectedFighter;
            this.selectedFighter.setSelected(state);
        }
        else {
            selectedFighter.setSelected(state);
            this.selectedFighter = null;
        }
    }

    public boolean isAnySelected() {
        for (Fighter f : this) {
            if(f.isSelected()) {
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

    private void makeListObservable(List<Fighter> fighterList) {
        observableList.setAll(fighterList);
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        if(observable.getClass() == Fighter.class) {
            Fighter f = (Fighter) observable;
            System.out.println(f.toString());
            removeFighter(f);
            addFighter(f);
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


    public int getObservableSize() {
        return observableSize.get();
    }

    public SimpleIntegerProperty observableSizeProperty() {
        return observableSize;
    }

    public void setObservableSize(int observableSize) {
        this.observableSize.set(observableSize);
    }

    public ObservableList<Fighter> getObservableList() {
        return observableList;
    }

    public ObservableList<Fighter> observableListProperty() {
        return observableList;
    }

    public void setObservableList(ObservableList<Fighter> observableList) {
        this.observableList = observableList;
    }
}
