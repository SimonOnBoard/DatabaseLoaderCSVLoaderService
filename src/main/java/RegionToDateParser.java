import java.sql.*;

public class RegionToDateParser {
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
            createYearView(name,connection);
        }
    }

    private static void createYearView(String name, Connection connection) throws SQLException {
        //language=sql
        String req = "CREATE MATERIALIZED VIEW " + name.toUpperCase() + "2006" + " AS SELECT * FROM " + name + " where shop_date < '2007-01-01'" + ";";
        //language=sql
        String req1 = "CREATE MATERIALIZED VIEW " + name.toUpperCase() + "2007" + " AS SELECT * FROM " + name + " where shop_date < '2008-01-01' and shop_date >= '2007-01-01'" + ";";
        //language=sql
        String req2 = "CREATE MATERIALIZED VIEW " + name.toUpperCase() + "2008" + " AS SELECT * FROM " + name + " where shop_date >= '2008-01-01'" + ";";
        PreparedStatement statement = connection.prepareStatement(req);
        statement.execute();
        statement = connection.prepareStatement(req1);
        statement.execute();
        statement = connection.prepareStatement(req2);
        statement.execute();
    }
}
