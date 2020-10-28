package hellocucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.After;
import org.junit.Assert;

import java.sql.*;

public class DatabaseSchemaStepDefinitions {

    private static Connection conn;
    private StepHelper stepHelper = new StepHelper();

    @After
    public void CloseConnection() throws SQLException {
        conn.close();
    }

    @Given("there is a connection to the Dynamics database")
    public void there_is_a_connection_to_the_dynamics_database() {
        try {
            String serverName = "127.0.0.1:1433";
            String databaseName = "LRDatabase";
            String url = "jdbc:sqlserver://" + serverName + ";databaseName=" + databaseName + ";integratedSecurity=true;";

            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Given("the {string} table exists")
    public void the_table_exists(String name) throws SQLException {
        boolean exists = conn.getMetaData().getTables(null, null, name, null).next();

        Assert.assertTrue(String.format("Table '%s' was not found in database.", name), exists);
    }

    @Then("verify the schema of the {string} table")
    public void verify_the_schema_of_the_table(String name, DataTable dataTable) throws SQLException {
        String query = String.format("SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, IS_NULLABLE, CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '%s'", name);
        ResultSet resultSet = stepHelper.runQuery(query, conn);

        DataTable dt = stepHelper.convertResultSetToDataTable(resultSet);
        resultSet.close();
        dataTable.diff(dt);
    }

    @Then("verify the {string} record count is {int}")
    public void verify_the_address_record_count_is(String name, int expectedCount) throws SQLException {
        String query = String.format("select count(*) from %s", name);
        ResultSet resultSet = stepHelper.runQuery(query, conn);
        resultSet.next();
        int actualCount = resultSet.getInt(1);
        resultSet.close();

        Assert.assertEquals(String.format("'%s' table count - expected %s but was actually %s", name, expectedCount, actualCount), actualCount, expectedCount);
    }
}
