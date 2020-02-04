import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Date;
import java.util.List;

public class TimeParser {
    public static void main(String[] args) throws IOException, ParseException, SQLException {
        timeImporter("123");
    }
    public static void timeImporter(String fileName) throws IOException, ParseException, SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/DatasetFromCSV", "postgres", "hellodarkness");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("time.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date from;
        Date to;
        records.remove(records.get(0));
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO time (id, from_date, to_date) values (?,?,?)");
        for(List<String> row: records){
            from = new Date(format.parse(row.get(1)).getTime());
            to =  new Date(format.parse(row.get(2)).getTime());
            preparedStatement.setInt(1,Integer.parseInt(row.get(0)));
            preparedStatement.setObject(2,from);
            preparedStatement.setObject(3,to);
            int a = preparedStatement.executeUpdate();
            if(a == 0) throw new IllegalStateException("INSERT не прошел");
            System.out.println(row.get(0) + " " + from.toString() + " " + to.toString());
        }
    }
}
