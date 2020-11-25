package mvc;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

public class FightersList
        extends ArrayList<SimpleListProperty<Fighter>>
        implements ListChangeListener {

    private boolean isPausedListListener = false;

    // showing that no actions were done and the current max INI is not final
    private boolean isInitializing = true;

    // indicator for the currently used sublist and therefore the current INI
    // of the acting fighter
    private int subListIndex;

    // the number of sublists (not an index)
    private int maxIni;

    private Properties settings;

    public static final ObservableList<Fighter> sortedList =
            FXCollections.synchronizedObservableList(FXCollections.observableList(new LinkedList<>()));

    public FightersList() {

        super();

        this.settings = Serializer.readConfigFile();
        this.maxIni = Integer.parseInt(settings.getProperty("actionCircleFieldCount"));
        this.subListIndex = maxIni - 1;

        // create a sublist for every INI value
        for (int index = 0; index < maxIni; index++) {
            LinkedList<Fighter> baseList = new LinkedList<>();
            ObservableList<Fighter> subList = FXCollections.synchronizedObservableList(
                    FXCollections.observableList(baseList));
            subList.addListener(this);
            this.add(new SimpleListProperty<>(subList));
        }
    }

    public FightersList(int size) {

        super();

        this.settings = Serializer.readConfigFile();
        this.maxIni = size;
        this.subListIndex = maxIni - 1;

        // create a sublist for every INI value
        for (int index = 0; index < maxIni; index++) {
            LinkedList<Fighter> baseList = new LinkedList<>();
            ObservableList<Fighter> subList = FXCollections.synchronizedObservableList(
                    FXCollections.observableList(baseList));
            subList.addListener(this);
            this.add(new SimpleListProperty<>(subList));
        }
    }

    /**
     * Make a new instance using another FightersList object as base.
     * @param fightersList A FightersList object containing the base values.
     */
    public FightersList(FightersList fightersList) {

        if (fightersList == null) {
            return;
        }

        this.settings = Serializer.readConfigFile();
        this.maxIni = Integer.parseInt(settings.getProperty("actionCircleFieldCount"));
        this.subListIndex = fightersList.getSubListIndex();

        for (int index = 0; index < maxIni; index++) {
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
    }

    @Override
    public void onChanged(Change c) {
        if (!isPausedListListener) {
            updateSortedList();
            updateFieldIndex();
        }
    }

    /**
     * Includes a new participant. It's position in the list is
     * calculated earlier without the INI being adjusted.
     * @param fighter The Fighter object to include in the list.
     * @return true if object was added, false if not.
     */
    public boolean addFighter(Fighter fighter) {

        if (fighter == null) {
            return false;
        }

        synchronized (this) {

            // get the appropriate list based on fighter's INI value
            int ini = evaluateIndex(fighter.getIni() - 1);
            SimpleListProperty<Fighter> subList = this.get(ini);

            // insert into list
            int listIndex;
            if (subList.size() > 0) {         // sublist not empty
                listIndex = subList.size() - 1;
                // find position before the participant of the next round
                while (listIndex > 0 && fighter.getIni() < subList.get(listIndex).getIni()) {
                    listIndex = listIndex - 1;
                }
                subList.add(listIndex + 1, fighter);
            } else {                                // sublist is empty
                subList.add(fighter);
            }

            if (subList.contains(fighter)) {
                // update only when no actions done so far
                if (isInitializing) {
                    updateFieldIndex();
                }
                updateSortedList();
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Removes a given Fighter object from the list.
     * @param fighter The Object to remove.
     * @return The Fighter object that had been removed.
     */
    public Fighter removeFighter(Fighter fighter) {

        if (fighter == null) {
            return null;
        }

        Fighter removedFighter = null;

        synchronized (this) {

            int ini = evaluateIndex(fighter.getIni() - 1);
            this.get(ini).remove(fighter);

            if (!this.get(ini).contains(fighter)) {
                removedFighter = fighter;
            }
            updateSortedList();
            return removedFighter;
        }
    }

    /**
     * Updates sublists by moving a given Fighter object from one to another.
     * The INI value is being updated during this method's call.
     * @param fighter The object to be moved in between the lists.
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
                ini = ini + maxIni;
            }
            fighter.setIni(ini);    // Vor dem Einfügen, damit INI > 0

            this.get(currentIni).add(this.get(currentIni).size(), fighter);

        }

    }

    /**
     * This method is responsible to make actions work.
     * This is done by reducing the INI value of the given Fighter object
     * by a given number and saves old INI value. INI can get less than zero.
     * @param fighter The participant that acts.
     * @param range The value to 'move', also known as Action Points.
     */
    public void action(Fighter fighter, int range) {
        int ini = fighter.getIni();
        if (range == 0) {
            fighter.setPreviousIni(ini);
        } else if (range > 0) {
            fighter.setPreviousIni(ini);
            ini = ini - range;
            fighter.setIni(ini);
            updateSubLists(fighter);
            // if an action happened, the order in the list is not changed
            isInitializing = false;
            updateSortedList();
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

    /**
     * Updates the subListIndex field to adjust it to changes due to actions.
     * It represents the currently used sublist and therefore the current
     * fighter that is acting.
     */
    private void updateFieldIndex() {
        /*
        If no actions were done yet, the whole list must be traversed to
        include possible new entries with a higher INI than last ones.
        This is required to make sure that fighters who are added during
        the fight are last in the order.
        */
        if (isInitializing) {
            for (int ind = maxIni - 1; ind >= 0; ind--) {
                if (!this.get(ind).isEmpty()) {
                    subListIndex = ind;
                    return;
                }
            }
        } else {
            while (subListIndex >= 0 && this.get(subListIndex).isEmpty()) {
                subListIndex = subListIndex - 1;
            }

            if (subListIndex < 0) {
                subListIndex = maxIni - 1;
            }
        }
    }

    /**
     * Updates the sortedList field, required to make the ListView work.
     * This field is used as base to an ObservedList, without it the ListView
     * is not updated. Changes to sortedList do not affect the FightersList.
     * Call this method after any change in the FightersList instance.
     * Does not delete the list, since it has listeners, but clears it instead.
     */
    public synchronized void updateSortedList() {
        sortedList.clear();
        // start at current sublist index, make a full round
        for (int index = 0; index < maxIni; index++) {
            sortedList.addAll(this.get(evaluateIndex(subListIndex - index)));
        }
    }

    /**
     * Evaluates the given number so it is guaranteed to be in the interval.
     * @param ini The INI of the participant.
     * @return The new evaluated value for INI.
     */
    private int evaluateIndex(int ini) {
        if (ini < 0) {
            while (ini < 0) {
                ini = ini + maxIni;
            }
        } else {
            while (ini >= maxIni) {
                ini = ini - maxIni;
            }
        }
        return ini;
    }

    public List<Fighter> subListByIni(int ini) {
        return this.get(ini - 1);
    }

    public int getSubListIndex() {
        return this.subListIndex;
    }

    public void setSubListIndex(int subListIndex) {
        this.subListIndex = subListIndex;
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
