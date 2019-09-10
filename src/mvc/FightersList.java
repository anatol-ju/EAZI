package mvc;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FightersList
        extends ArrayList<SimpleListProperty<Fighter>>
        implements ListChangeListener {

    private boolean isPausedListListener = false;
    private boolean isInitializing = true;
    private int fieldIndex = 11;

    private ObservableList<Fighter> sortedList;

    public FightersList() {

        super();

        // Eine Unterliste für jedes INI-Feld anlegen.
        for (int index = 0; index <= 11; index++) {
            LinkedList<Fighter> baseList = new LinkedList<>();
            ObservableList<Fighter> subList = FXCollections.synchronizedObservableList(
                    FXCollections.observableList(baseList));
            subList.addListener(this);
            this.add(new SimpleListProperty<>(subList));
        }

        sortedList = FXCollections.synchronizedObservableList(FXCollections.observableList(new LinkedList<>()));
    }

    public FightersList(FightersList fightersList) {

        if (fightersList == null) {
            return;
        }

        this.fieldIndex = fightersList.getFieldIndex();

        for (int index = 0; index <= 11; index++) {
            LinkedList<Fighter> baseList = new LinkedList<>();
            for (int ind = 0; ind < fightersList.get(index).size(); ind++) {
                if (!fightersList.get(index).isEmpty()) {
                    baseList.add(ind, new Fighter(fightersList.get(index).get(ind)));
                }
            }
            ObservableList<Fighter> subList = FXCollections.synchronizedObservableList(
                    FXCollections.observableList(baseList));
            subList.addListener(this);
            this.add(new SimpleListProperty<>(subList));
        }

        // Die sortedList sollte gleich bleiben mit gleicher Referenz.
        // Das vereinfacht das Aktualisieren durch die Listener.
        this.sortedList = fightersList.getSortedList();
    }

    @Override
    public void onChanged(Change c) {
        if (!isPausedListListener) {
            updateSortedList();
            updateFieldIndex();
        }
    }

    /**
     * Fügt einen neuen Teilnehmer ein. Die Position in der Liste wird vorher
     * umgerechnet ohne dass die INI angepasst wird.
     * @param fighter
     * @return
     */
    public boolean addFighter(Fighter fighter) {

        if (fighter == null) {
            System.out.println("Parameter is null.");
            return false;
        }

        synchronized (this) {

            // Passende Liste finden mit 0 < INI < 11.
            int ini = evaluateIndex(fighter.getIni() - 1);

            // Einfügen je nach INI
            int listIndex;
            if (this.get(ini).size() > 0) {         // Liste nicht leer
                listIndex = this.get(ini).size() - 1;
                // Position vor dem Teilnehmer der nächsten Runde suchen.
                while (listIndex > 0 && fighter.getIni() < this.get(ini).get(listIndex).getIni()) {
                    listIndex = listIndex - 1;
                }
                this.get(ini).add(listIndex + 1, fighter);
            } else {                                // Liste ist leer
                this.get(ini).add(fighter);
            }

            if (this.get(ini).contains(fighter)) {
                // Aktualisierung nur wenn noch keine Handlung durchgeführt wurden
                if (isInitializing) {
                    updateFieldIndex();
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public Fighter removeFighter(Fighter fighter) {

        if (fighter == null) {
            return null;
        }

        synchronized (this) {

            int ini = evaluateIndex(fighter.getIni() - 1);
            this.get(ini).remove(fighter);

            if (this.get(ini).contains(fighter)) {
                return null;
            } else {
                return fighter;
            }
        }
    }

    /**
     * Diese Funktion verschiebt den Teilnehmer von einer Unterliste in
     * eine andere. Der Index wird für einen Bereich 0 < INI < 11 umgerechnet.
     * @param fighter
     */
    public void updateSubLists(Fighter fighter) {

        if (fighter == null) {
            return;
        }

        synchronized (this) {

            int previousIni = evaluateIndex(fighter.getPreviousIni() - 1);
            this.get(previousIni).remove(fighter);

            int currentIni = evaluateIndex(fighter.getIni() - 1);

            int ini = fighter.getIni();
            if (ini <= 0) {
                ini = ini + 12;
            }
            fighter.setIni(ini);    // Vor dem Einfügen, damit INI > 0

            this.get(currentIni).add(this.get(currentIni).size(), fighter);

        }

    }

    /**
     * Diese Funktion stellt die eigentliche Handlung dar.
     * Die INI des Teilnehmers wird um den angegebenen Betrag gesenkt und kann
     * dabei auch kleiner als 0 werden. Wenn der Betrag 0 ist, wird nichts
     * verändert.
     * @param fighter
     * @param range
     */
    public void action(Fighter fighter, int range) {
        if (range > 0) {
            int ini = fighter.getIni();
            fighter.setPreviousIni(ini);
            ini = ini - range;
            fighter.setIni(ini);
            updateSubLists(fighter);
            // Nach einer Handlung wird Reihenfolge nicht mehr angepasst.
            isInitializing = false;
        }
    }

    public boolean contains(Fighter fighter) {

        for (SimpleListProperty<Fighter> list : this) {
            for (Fighter f : list) {
                if (f.equals(fighter)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean contains(String name) {

        for (SimpleListProperty<Fighter> list : this) {
            for (Fighter f : list) {
                if (f.getName().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    public int getIndex(Fighter fighter) {

        for (SimpleListProperty<Fighter> list : this) {
            for (int index = 0; index < list.size(); index++) {
                if (list.get(index).equals(fighter)) {
                    return index;
                }
            }
        }

        return -1;
    }

    public void updateSortedList() {

        sortedList.clear();

        synchronized(this) {

            for (int index = fieldIndex; index < this.size() + fieldIndex; index++) {
                if (index > 11) {
                    sortedList.addAll(this.get(index - this.size()));
                } else {
                    sortedList.addAll(this.get(index));
                }
            }

        }

    }

    private void updateFieldIndex() {

        while (fieldIndex >= 0 && this.get(fieldIndex).isEmpty()) {
            fieldIndex = fieldIndex - 1;
        }

        if (fieldIndex < 0) {
            fieldIndex = 11;
        }
    }

    /**
     * Diese Funktion passt den aktuellen Wert so an, dass er zwischen 0 und 11
     * liegt um ihn als Index für die Liste benutzen zu können.
     * @param ini
     * @return
     */
    private int evaluateIndex(int ini) {
        if (ini < 0) {
            ini = ini + 12;
        } else {
            while (ini > 11) {
                ini = ini - 12;
            }
        }
        return ini;
    }

    public List<Fighter> subListByIni(int ini) {
        return this.get(ini - 1);
    }

    public int getFieldIndex() {
        return this.fieldIndex;
    }

    public void setFieldIndex(int fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    public ObservableList<Fighter> getSortedList() {
        return this.sortedList;
    }

    /**
     * Erstellt eine Kopie der Liste.
     * Dabei werden alle Elemente in der Reihenfolge kopiert, also neue Objekte
     * dieser erstellt.
     * @return
     */
    public FightersList copy() {

        FightersList copy = new FightersList();
        for (int index = 0; index < this.size(); index++) {
            SimpleListProperty<Fighter> subList = new SimpleListProperty<>(FXCollections.observableList(new LinkedList<>()));
            subList.clear();
            for (Fighter fighter : this.get(index)) {
                subList.add(new Fighter(fighter));
            }
            copy.set(index, subList);
        }

        return copy;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int index = 0; index < this.size(); index++) {
            str.append(index).append(": ");
            for (Fighter fighter : this.get(index)) {
                str.append(fighter.getName()).append(" ");
            }
            str.append("\n");
        }
        return str.toString();
    }

}
