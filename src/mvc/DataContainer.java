package mvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataContainer implements Serializable {

    // input data
    private List<List<Fighter>> list;
    private int fieldIndex;

    /**
     * Der Kontainer enthält die Daten, die für das Speichern und Laden
     * erforderlich sind. Das sind insbesondere die Teilnehmer und der Index
     * des Feldes auf dem sich der gerade handelnde Kämpfer befindet.
     * Das Bereitstellen bzw. Konvertieren der Daten übernimmt die Klasse
     * <code>Serializer</code>.
     */
    public DataContainer() {
    }

    public DataContainer(List<List<Fighter>> list, int fieldIndex) {
        this.list = list;
        this. fieldIndex = fieldIndex;
    }

    public List<List<Fighter>> getFighterList() {
        return list;
    }

    public void setFighterList(List<List<Fighter>> list) {
        this.list = list;
    }

    public int getFieldIndex() {
        return fieldIndex;
    }

    public void setFieldIndex(int fieldIndex) {
        this.fieldIndex = fieldIndex;
    }
}
