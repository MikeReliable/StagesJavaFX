package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SoSelection {

    private String answer = null, answerRevers = null;
    private String result;

    public String find(int S01, int S02, int step, String n, File file) {
        try {
            result = null;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            n = n.replace(',', '.');
            double degree = Double.parseDouble(n);
            List<String> lines = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("##.###");
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace(',', '.');
                if (!line.contains("--")) {
                    lines.add(line);
                    lines.removeIf(item -> item == null || "".equals(item));
                }
            }
            int l = lines.size();
            System.out.println("Lines: " + l);

            for (int k = S01; k <= S02; k = k + step) {
                double elongationMax = 0;
                double x1 = 0, x2 = 0, x3 = 0, y0, y00, y1, y2, y02, y3;
                double logStart = 0, logEnd = 0;

                for (int i = 1; i < l; i = i + 10) {
                    double correlat = 1, degreeCalc = 0, numSum = 0, S1 = 0, S2 = 0, S3 = 0, S4 = 0, S5 = 0;
//                System.out.println("Line num:" + (i + 1));
                    String m1 = lines.get(i);
                    String[] split1 = m1.split("\t");
//                System.out.println(Arrays.deepToString(split1));
                    double defStart = Double.parseDouble(split1[0]); // Read Strain column
                    x1 = Double.parseDouble(split1[1]); // Read ln(e) column
                    y00 = Double.parseDouble(split1[2]); // Read S column
//                    System.out.println("y "+ (y00 - k));

                    for (int j = i + 1; j < l; j = j + 10) {
                        String m2 = lines.get(j);
                        String[] split2 = m2.split("\t");
                        double defEnd = Double.parseDouble(split2[0]); // Read Strain column
                        x2 = Double.parseDouble(split2[1]); // Read ln(e) column
                        y02 = Double.parseDouble(split2[2]); // Read S column
                        y2 = Math.log(y02 - k);
//                            System.out.println("log(S-S0) " + y2);
                        // Рассчет коэффициента линейной аппроксимации
                        S1 += x2;
                        S2 += y2;
                        S3 += x2 * y2;
                        S4 += x2 * x2;
                        S5 += y2 * y2;
                        numSum++;
//                    System.out.println("S: " + S1 + " " + S2 + " " + S3 + " " + S4 + " j: " + j);
                        if ((defEnd - defStart) < 5 || degreeCalc >= degree * 0.9 && degreeCalc <= degree * 1.1) { // условие поиска стадии по максимальному диапазону
                            degreeCalc = ((numSum * S3) - (S1 * S2)) / ((numSum * S4) - (S1 * S1));  // определение коэффициента n
                            if (degreeCalc >= degree * 0.99 && degreeCalc <= degree * 1.01) {
                                correlat = ((numSum * S3) - (S1 * S2)) / Math.sqrt((numSum * S4 - S1 * S1) * (numSum * S5 - S2 * S2)); // коэффициент корреляции Пирсона
//                                System.out.println("Degree calculation: " + degreeCalc + "\tCorrelat: " + correlat);
                                double elongation = defEnd - defStart;
                                String initialDefEnd = df.format(defEnd);
                                String initialDefStars = df.format(defStart);
                                String initiaElongation = df.format(elongation);
                                String initialResult = df.format(correlat);
                                if (elongation > elongationMax) {
                                    elongationMax = elongation;
                                    logStart = x1;
                                    logEnd = x2;
                                    if (elongationMax > 1) { // отбраковка стадий меньше 1 %
                                        answer = "\nS0=" + k + " " + "n=" + degree + "\nНачало стадии: " + initialDefStars + "\tОкончание стадии: " + initialDefEnd + "\n"
                                                + "Продолжительность: " + initiaElongation + "\tОтклонение: R=" + initialResult + "\n" +
                                                "Ln(e)_start=" + logStart + " " + "\tLn(e)_end=" + logEnd + "\n";
                                    }
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
                System.out.println("S0=" + k);
                System.out.println(answer);
                System.out.println("");
                result += answer;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "ERROR";
            return result;
        }
        return result;
    }
}
