

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CsvLoader {
    public static void main(String[] args) throws SQLException, ParseException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/DatasetFromCSV", "postgres", "hellodarkness");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO transactions values(?,?," +
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        Scanner scan = new Scanner(System.in);
        File folder = new File(scan.nextLine());
        // loops over files available in the path except for hidden files.
        File[] listOfFiles = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isHidden();
            }
        });
        for (File file : listOfFiles) {
            loadData(file, preparedStatement);
            System.out.println("done for: " + file.getName());
        }
    }

    private static void loadData(File file, PreparedStatement preparedStatement) throws SQLException, ParseException {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        records.remove(records.get(0));
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        for (List<String> strings : records) {
            preparedStatement.setLong(1, Long.parseLong(strings.get(0)));
            preparedStatement.setObject(2, new Date(format.parse(strings.get(1)).getTime()));
            preparedStatement.setInt(3, Integer.parseInt(strings.get(2)));
            preparedStatement.setInt(4, Integer.parseInt(strings.get(3)));
            preparedStatement.setInt(5, Integer.parseInt(strings.get(4)));
            preparedStatement.setDouble(6, Double.parseDouble(strings.get(5)));
            preparedStatement.setString(7, strings.get(6));
            preparedStatement.setString(8, strings.get(7));
            preparedStatement.setString(9, strings.get(8));
            preparedStatement.setString(10, strings.get(9));
            preparedStatement.setString(11, strings.get(10));
            if (strings.get(12).equals("")) {
                preparedStatement.setString(12, "N");
                preparedStatement.setString(13, "N");
                preparedStatement.setString(14, "N");

            } else {
                preparedStatement.setString(12, strings.get(11));
                preparedStatement.setString(13, strings.get(12));
                preparedStatement.setString(14, strings.get(13));
            }
            preparedStatement.setLong(15, Long.parseLong(strings.get(14)));
            preparedStatement.setString(16, strings.get(15));
            preparedStatement.setString(17, strings.get(16));
            preparedStatement.setString(18, strings.get(17));
            preparedStatement.setString(19, strings.get(18));
            preparedStatement.setString(20, strings.get(19));
            preparedStatement.setString(21, strings.get(20));
            preparedStatement.setString(22, strings.get(21));
            int a = preparedStatement.executeUpdate();
            if(a == 0) throw new IllegalStateException("INSERT не прошел");
        }
    }
}
