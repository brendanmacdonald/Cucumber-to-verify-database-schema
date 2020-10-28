package hellocucumber;

import io.cucumber.datatable.DataTable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StepHelper {

    public ResultSet runQuery(String query, Connection conn) throws SQLException {
        Statement statement = conn.createStatement();

        return statement.executeQuery(query);
    }

    public DataTable convertResultSetToDataTable(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnCount = rsmd.getColumnCount();

        List<List<String>> resultSetList = new ArrayList<>();
        List<String> headers = new ArrayList<>();

        // Add column headers
        for (int i = 1; i <= columnCount; i++) {
            String headerValue = rsmd.getColumnName(i);
            headers.add(headerValue);
        }
        resultSetList.add(headers);

        // Add row data
        while (resultSet.next()) {
            List<String> rowList = new ArrayList<>();
            for (int j = 1; j <= columnCount; j++) {
                String columnValue = resultSet.getString(j);
                if (resultSet.wasNull()) {
                    columnValue = "NULL"; // Handle null values in resultSet
                }
                rowList.add(columnValue);
            }
            resultSetList.add(rowList);
        }

        return DataTable.create(resultSetList);
    }
}
