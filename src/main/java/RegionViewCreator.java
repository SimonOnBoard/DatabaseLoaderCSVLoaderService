import java.sql.*;

public class RegionViewCreator {
    public static void main(String[] args) throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/DatasetFromCSV", "postgres", "hellodarkness");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT store_region from transactions");

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String name = resultSet.getString(1);
            System.out.println(name);
            createView(name,connection);
        }
    }

    private static void createView(String name, Connection connection) throws SQLException {
        //language=sql
        String req = "CREATE MATERIALIZED VIEW " + name.toUpperCase() + " AS SELECT * FROM transactions where store_region =  " + '\'' + name.toUpperCase() + '\'' + ";";
        PreparedStatement statement = connection.prepareStatement(req);
        statement.execute();
    }
}
