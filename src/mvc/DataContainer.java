package mvc;

import java.util.List;

public class DataContainer {

    private List<List<Fighter>> list;
    private int fieldIndex;

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
