package mvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DataContainer implements Serializable {

    // input data
    private List<List<Fighter>> list;
    private int fieldIndex;
    private int maxIni;

    /**
     * Der Kontainer enthält die Daten, die für das Speichern und Laden
     * erforderlich sind. Das sind insbesondere die Teilnehmer und der Index
     * des Feldes auf dem sich der gerade handelnde Kämpfer befindet.
     * Das Bereitstellen bzw. Konvertieren der Daten übernimmt die Klasse
     * <code>Serializer</code>.
     */
    public DataContainer() {
    }

    public DataContainer(List<List<Fighter>> list, int fieldIndex, int maxIni) {
        this.list = list;
        this.fieldIndex = fieldIndex;
        this.maxIni = maxIni;
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

    public int getMaxIni() {
        return maxIni;
    }

    public void setMaxIni(int maxIni) {
        this.maxIni = maxIni;
    }
}
