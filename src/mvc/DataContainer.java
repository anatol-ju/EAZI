package mvc;

import java.io.Serializable;
import java.util.List;

public class DataContainer implements Serializable {

    private List<List<Fighter>> list;
    private int fieldIndex;

    /**
     * Der Kontainer enthält die Daten, die für das Speichern und Laden
     * erforderlich sind. Das sind insbesondere die Teilnehmer und der Index
     * des Feldes auf dem sich der gerade handelnde Kämpfer befindet.
     * Das Bereitstellen bzw. Konvertieren der Daten übernimmt die Klasse
     * <code>Serializer</code>.
     * @param list
     * @param fieldIndex
     */
    public DataContainer(List<List<Fighter>> list, int fieldIndex) {
        this.list = list;
        this.fieldIndex = fieldIndex;
    }

    public List<List<Fighter>> getList() {
        return list;
    }

    public int getFieldIndex() {
        return fieldIndex;
    }
}
