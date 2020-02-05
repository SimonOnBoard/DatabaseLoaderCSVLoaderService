import java.awt.image.AreaAveragingScaleFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompareProductsFromClasses {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        BufferedReader br1 = new BufferedReader(new FileReader(args[1]));
        String line;
        List<String> productCodeMM = new ArrayList<>();
        List<String> productCodeNotMM = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] values = line.split(" ");
            productCodeMM.add(values[0]);
        }
        while ((line = br1.readLine()) != null) {
            String[] values = line.split(" ");
            productCodeNotMM.add(values[0]);
        }
        PrintWriter pw = new PrintWriter(new File(args[0]+args[1]));
        int i = 0;
        for (String str : productCodeMM) {
            if (productCodeNotMM.contains(str)) {
                pw.println(str);
                i++;
            }
        }
        pw.close();
        System.out.println(i);
    }
}
