

import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ProductsAnalyzer {
    public static HashMap<String, HashMap<String, Transaction>> orders = new HashMap<>();
    public static double sum = 0;

    public static void main(String[] args) throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/DatasetFromCSV", "postgres", "hellodarkness");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT basket_id from w032008 where cust_price_sensitivity !='MM'");

        ResultSet resultSet = statement.executeQuery();
        int i = 0;
        while (resultSet.next()) {
            String name = resultSet.getString(1);
            loadBasket(name, connection, statement);
            i++;
        }
        System.out.println("Количество корзин: " + i);
        loadFinalSum();
        loadSumForProducts();
    }

    private static void loadSumForProducts() {
        Set<String> productsCode = new HashSet<>();
        Collection<HashMap<String, Transaction>> set = orders.values();
        for (HashMap<String, Transaction> map : set) {
            Set<String> transactions = map.keySet();
            productsCode.addAll(transactions);
        }
        System.out.println("Количество уникальных продуктов: " + productsCode.size());
        loadMapsOfParametrs(productsCode);
    }

    private static void loadMapsOfParametrs(Set<String> productsCode) {
        Map<String, Double> amount = new HashMap<>();
        Map<String, Double> checkIn = new HashMap<>();
        Collection<HashMap<String, Transaction>> keys = orders.values();
        Double spend = 0.0;
        int i, j;
        for (String name : productsCode) {
            spend = 0.0;
            i = 0;
            j = 0;
            for (HashMap<String, Transaction> map : keys) {
                if (map.get(name) != null) {
                    spend += map.get(name).spend;
                    i++;
                } else {
                    j++;
                }
            }
            amount.put(name, spend / sum);
            double a = i;
            double b = i + j;
            a = a / b;
            checkIn.put(name, a);
        }
        Map<String, Double> orderedAmount = sortByValue(amount);
        calculateProfitableProducts(orderedAmount, checkIn, productsCode);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private static void calculateProfitableProducts(Map<String, Double> amount, Map<String, Double> checker, Set<String> productsCode) {
        double mid = 0.5;
        double sum = 0.0;
        System.out.println("Количество уникальных продуктов: (для сверки) " + productsCode.size());
        Map<String,Double> resultProducts = new HashMap<>();
        for (Map.Entry<String, Double> entry : amount.entrySet()) {
            sum += entry.getValue();
            if(sum - mid > 0.00005){
                resultProducts.put(entry.getKey(),entry.getValue());
                break;
            }
            else{
                resultProducts.put(entry.getKey(),entry.getValue());
            }
        }
        System.out.println("Количество продуктов , которые формируют наибольшую прибыль" + resultProducts.size());
        Map<String, Double> orderedProducts = sortByValue(resultProducts);
//        for (Map.Entry<String, Double> entry : orderedProducts.entrySet()) {
//                System.out.println("ID =  " + entry.getKey() + " Доля выручки = " + entry.getValue());
//        }
        getPotentialProducts(orderedProducts, checker);
    }

    private static void getPotentialProducts(Map<String, Double> orderedProducts, Map<String, Double> checker)
    {
        Map<String,Double> koefficientMap = new HashMap<>();
        double sum = 0.0;
        for(Map.Entry<String,Double> entry: orderedProducts.entrySet()){
            koefficientMap.put(entry.getKey(), entry.getValue()*checker.get(entry.getKey()));
            sum += koefficientMap.get(entry.getKey());
            //System.out.println(koefficientMap.get(entry.getKey()) + " " + entry.getKey() + " " + entry.getValue() + " " checker.get(entry.getKey()));
        }
        System.out.println(sum);
        double middle = sum/koefficientMap.size();
        System.out.println("Среднее значние коэффициента : " + middle);
        System.out.println("Количество продуктов в итоговом списке коэффициентов : (для сверки) " + koefficientMap.size());
        Map<String,Double> finalProductsMap = new HashMap<>();
        for(Map.Entry<String,Double> entry: koefficientMap.entrySet()){
            //System.out.println(entry.getValue() - middle);
            if(Math.abs(entry.getValue() - middle) <= 0.000025){
                finalProductsMap.put(entry.getKey(),entry.getValue());
            }
        }
        System.out.println(finalProductsMap.size() + " Количество продуктов с потенциалом роста");
        writeStat(finalProductsMap);
    }

    private static void writeStat(Map<String, Double> finalProductsMap) {
        Random random = new Random();
        PrintWriter pw;
        try {
            pw = new PrintWriter(new File( "NOT_MM.txt"));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException();
        }
        for(Map.Entry<String,Double> entry: finalProductsMap.entrySet()){
            pw.println(entry.getKey() + " " + entry.getValue());
        }
        pw.close();
    }

    private static void loadFinalSum() {
        Collection<HashMap<String, Transaction>> set = orders.values();
        for (HashMap<String, Transaction> map : set) {
            Collection<Transaction> transactions = map.values();
            for (Transaction transaction : transactions) {
                sum += transaction.spend;
            }
        }
        System.out.println("Всего потрачено: " + sum);
    }

    private static void loadBasket(String name, Connection connection, PreparedStatement statement) throws SQLException {
        //language=sql
        statement = connection.prepareStatement("SELECT * FROM w032008 where basket_id = " + Long.parseLong(name));
        ResultSet resultSet = statement.executeQuery();
        Transaction transaction = null;
        HashMap<String, Transaction> map = null;
        while (resultSet.next()) {
            transaction = productRowMapper.mapRow(resultSet);
            map = orders.get(name);
            if (map == null) {
                map = new HashMap<>();
            }
            map.put(transaction.prodCode, transaction);
            orders.put(name, map);
        }
    }

    private static RowMapper<Transaction> productRowMapper = row -> {
        Long shopWeek = row.getLong("shop_week");
        Date shopDate = row.getDate("shop_date");
        Integer shopWeekDay = row.getInt("shop_weekday");
        Integer shopHour = row.getInt("shop_hour");
        Integer quantity = row.getInt("quantity");
        Double spend = row.getDouble("spend");
        String code = row.getString("prod_code");
        String code1 = row.getString("prod_code_10");
        String code2 = row.getString("prod_code_20");
        String code3 = row.getString("prod_code_30");
        String code4 = row.getString("prod_code_40");
        String custCode = row.getString("cust_code");
        String custPriceSensitivity = row.getString("cust_price_sensitivity");
        String custLifeStage = row.getString("cust_lifestage");
        Long basket_id = row.getLong("basket_id");
        String basket_size = row.getString("basket_size");
        String basketPriceSensitivity = row.getString("basket_price_sensetivity");
        String basketType = row.getString("basket_type");
        String basketDominantMission = row.getString("basket_dominant_mission");
        String store_code = row.getString("store_code");
        String store_format = row.getString("store_format");
        String store_region = row.getString("store_region");
        return new Transaction(shopWeek, shopDate, shopWeekDay, shopHour, quantity, spend, code, code1, code2, code3, code4, custCode, custPriceSensitivity,
                custLifeStage, basket_id, basket_size, basketPriceSensitivity, basketType, basketDominantMission, store_code, store_format, store_region);
    };

}
