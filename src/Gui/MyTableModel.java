package Gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class MyTableModel extends AbstractTableModel {
    private ArrayList<ArrayList<String>> data;
    private ArrayList<String> columnNames;

    public MyTableModel(ArrayList<ArrayList<String>> data, ArrayList<String> columnNames) {
        this.data = data;
        this.columnNames = columnNames;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String dataCell = "";
        try {
            dataCell = data.get(rowIndex).get(columnIndex);
        } catch(IndexOutOfBoundsException e) {
            dataCell = "N/A";
        }
        return dataCell;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public void setData(ArrayList<ArrayList<String>> data) {
        this.data = data;
    }
}



