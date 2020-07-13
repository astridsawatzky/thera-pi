import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

 class DataResult {

    private final List<String> columnNames ;
    private final List<List<Object>> data ;

    public DataResult(List<String> columnNames, List<List<Object>> data) {
        this.columnNames = columnNames ;
        this.data = data ;
    }

    public int getNumColumns() {
        return columnNames.size();
    }

    public String getColumnName(int index) {
        return columnNames.get(index);
    }

    public int getNumRows() {
        return data.size();
    }

    public Object getData(int column, int row) {
        return data.get(row).get(column);
    }

    public List<List<Object>> getData() {
        return data ;
    }
}
 class DAO {

    private Connection conn ;


    public DAO() {
        // initialize connection...
    }

    public DataResult getAllData() throws SQLException {

        List<List<Object>> data = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("select * from some_table")) {


            int columnCount = rs.getMetaData().getColumnCount();

            for (int i = 1 ; i <= columnCount ; i++) {
                columnNames.add(rs.getMetaData().getColumnName(i));
            }

            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1 ; i <= columnCount ; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }
        }

        return new DataResult(columnNames, data);
    }


    private void showmethodingui() throws SQLException {
        TableView<List<Object>> table = new TableView<>();
        DAO dao = new DAO();
        DataResult data = dao.getAllData();

        for (int i = 0 ; i < data.getNumColumns() ; i++) {
            TableColumn<List<Object>, Object> column = new TableColumn<>(data.getColumnName(i));
            int columnIndex = i ;
            column.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().get(columnIndex)));
            table.getColumns().add(column);
        }

        table.getItems().setAll(data.getData());

    }
}
