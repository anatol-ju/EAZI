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

    public DataContainer() {
    }

    /**
     * Create a container from the current <c>FightersList</c>
     * instance. The result is an object that can be serialized to save and
     * load the state of the application.
     * @param fightersList the current instance of the <c>FightersList</c>,
     *                     all data will be extracted from here.
     */
    public DataContainer(FightersList fightersList) {
        setData(fightersList);
    }

    public DataContainer(List<List<Fighter>> list, int fieldIndex, int maxIni) {
        this.list = list;
        this.fieldIndex = fieldIndex;
        this.maxIni = maxIni;
    }

    public void setData(FightersList fightersList) {
        List<List<Fighter>> list = new ArrayList<>(fightersList.size());

        for (int index = 0; index < fightersList.size(); index++) {
            List<Fighter> sublist = new ArrayList<>(fightersList.get(index).size());
            list.add(index, sublist);
            for (int ind = 0; ind < fightersList.get(index).size(); ind++) {
                sublist.add(ind, fightersList.get(index).get(ind));
            }
        }

        this.list = list;
        this.fieldIndex = fightersList.getSubListIndex();
        this.maxIni = fightersList.getMaxIni();
    }

    public FightersList getData() {
        FightersList fl = new FightersList(list.size());

        for (int index = 0; index < list.size(); index++) {
            for (int ind = 0; ind < list.get(index).size(); ind++) {
                fl.get(index).add(ind, list.get(index).get(ind));
            }
        }
        fl.setMaxIni(maxIni);
        fl.setSubListIndex(fieldIndex);
        return fl;
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
