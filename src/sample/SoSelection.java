package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SoSelection {

    int startRow = 1, endRow = 1;
    private double defStartRevers = 0;
    private String answer = "", answerRevers = "";

    public String find(double S01, double S02, double step, String n, File file) {
        String result;

        try {
            result = "";
            n = n.replace(',', '.');
            double degree = Double.parseDouble(n);

            BufferedReader reader = new BufferedReader(new FileReader(file));
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
//            System.out.println("Lines: " + l);

            for (double k = S01; k <= S02; k = k + step) {
                double elongationMax = 0;
                double x1 = 0, x2 = 0, x3 = 0, y0, y00, y1, y2, y02, y3, y03;
                double logStart = 0, logEnd = 0;
//                System.out.println("k:" + k);
                for (int i = 1; i < l; i = i + 10) {
                    double correlat = 1, degreeCalc = 0, numSum = 0, S1 = 0, S2 = 0, S3 = 0, S4 = 0, S5 = 0;
//                System.out.println("Line num:" + (i + 1));
                    String m1 = lines.get(i);
                    String[] split1 = m1.split("\t");
//                System.out.println(Arrays.deepToString(split1));
                    double defStart = Double.parseDouble(split1[0]); // Read Strain column
                    x1 = Double.parseDouble(split1[1]); // Read ln(e) column
//                    y00 = Double.parseDouble(split1[2]); // Read S column
//                    System.out.println("y "+ (y00 - k));

                    for (int j = i + 1; j < l; j = j + 10) {
                        String m2 = lines.get(j);
                        String[] split2 = m2.split("\t");
                        double defEnd = Double.parseDouble(split2[0]); // Read Strain column
                        x2 = Double.parseDouble(split2[1]); // Read ln(e) column
                        y02 = Double.parseDouble(split2[2]); // Read S column
                        y2 = Math.log(y02 - Double.parseDouble(String.valueOf(k)));
                        // Рассчет коэффициента линейной аппроксимации
                        S1 += Math.abs(x2);
                        S2 += Math.abs(y2);
                        S3 += Math.abs(x2) * Math.abs(y2);
                        S4 += Math.abs(x2) * Math.abs(x2);
                        S5 += Math.abs(y2) * Math.abs(y2);
                        numSum++;
                        double elongation = defEnd - defStart;
//                        System.out.println("S1:" + S1 + " S2:" + S2 + " S3:" + S3 + " S4:" + S4 + " j:" + j);
                        degreeCalc = Math.abs((numSum * S3 - S1 * S2) / (numSum * S4 - S1 * S1));  // определение коэффициента n
                        correlat = Math.abs((numSum * S3 - S1 * S2) / Math.sqrt((numSum * S4 - S1 * S1) * (numSum * S5 - S2 * S2))); // коэффициент корреляции Пирсона
                        if (elongation < 4 || degreeCalc >= degree * 0.95 && degreeCalc <= degree * 1.05 && correlat > 0.95) { // условие поиска стадии по максимальному диапазону
//                            System.out.println("degreeCalc:" + degreeCalc + " numSum: " + numSum + " elongation:" + elongation + " defStart:" + defStart);
//                            System.out.println("Degree calculation: " + degreeCalc + "\tCorrelat: " + correlat);
                            if (degreeCalc >= degree * 0.95 && degreeCalc <= degree * 1.05 && correlat > 0.95) {
//                                correlat = Math.abs((numSum * S3 - S1 * S2) / Math.sqrt((numSum * S4 - S1 * S1) * (numSum * S5 - S2 * S2))); // коэффициент корреляции Пирсона
//                                System.out.println("Degree calculation: " + degreeCalc + "\tCorrelat: " + correlat);
                                String initialDefEnd = df.format(defEnd);
                                String initialDefStart = df.format(defStart);
                                String initialElongation = df.format(elongation);
                                String initialResult = df.format(correlat);
                                if (elongation > elongationMax) {
                                    elongationMax = elongation;
                                    logStart = x1;
                                    logEnd = x2;
                                    if (elongationMax > 1) { // отбраковка стадий меньше 1 %
                                        answer = "\nS0=" + k + " " + "n=" + degree + "\nНачало стадии: " + initialDefStart + "\tОкончание стадии: " + initialDefEnd + "\n"
                                                + "Продолжительность: " + initialElongation + "\tОтклонение: R=" + initialResult + "\n" +
                                                "ln(e)_start=" + logStart + " " + "\tln(e)_end=" + logEnd + "\n";
                                        startRow = i;
                                        endRow = j;
                                        defStartRevers = defStart;
                                    }
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }

                // уточнение искомого интервала за счет обрезки кривой справа
                // решение проблемы отклонения кривой от линейности при больших массивах данных
                double elongationReversMax = 0;
                for (int q = endRow; q > startRow; q = q - 10) {  // Revers start
                    double degreeCalcRevers, correlatRevers;
                    double numSumRevers = 0;
                    double elongationRevers;
                    double S11 = 0, S21 = 0, S31 = 0, S41 = 0, S51 = 0;
                    String m2 = lines.get(q);
                    String[] split2 = m2.split("\t");
                    double startPoint = Double.parseDouble(split2[0]); // Read Strain column
//                System.out.println("startRow: " + startRow + " endRow: " + endRow);
                    for (int m = q; m < endRow; m = m + 10) {
//                    System.out.println("k: " + k + " endRow: " + endRow);
                        String m3 = lines.get(m);
                        String[] split3 = m3.split("\t");
                        double defEndRevers = Double.parseDouble(split3[0]); // Read Strain column
                        x3 = Double.parseDouble(split3[1]); // Read ln(e) column
                        y03 = Double.parseDouble(split3[2]); // Read S column
                        y3 = Math.log(y03 - Double.parseDouble(String.valueOf(k)));
//                    System.out.println(x2 + " " + y2);
                        S11 += Math.abs(x3);
                        S21 += Math.abs(y3);
                        S31 += Math.abs(x3) * Math.abs(y3);
                        S41 += Math.abs(x3) * Math.abs(x3);
                        S51 += Math.abs(y3) * Math.abs(y3);
                        numSumRevers++;
//                    System.out.println("numSumRevers " + numSumRevers);
//                                        System.out.println("S: " + S11 + " " + S21 + " " + S31 + " " + S41 + " j: " + numSumRevers);//
                        degreeCalcRevers = Math.abs((numSumRevers * S31 - S11 * S21) / (numSumRevers * S41 - S11 * S11));  // определение коэффициента n
                        correlatRevers = Math.abs((numSumRevers * S31 - S11 * S21) / Math.sqrt((numSumRevers * S41 - S11 * S11) * (numSumRevers * S51 - S21 * S21))); // коэффициент корреляции Пирсона
//                    System.out.println("Degree calculation: " + degreeCalcRevers + "\tCorrelat: " + correlatRevers);
                        elongationRevers = defEndRevers - startPoint;
                        if (degreeCalcRevers >= 0.95 * degree && degreeCalcRevers <= 1.05 * degree && correlatRevers > 0.95 || elongationRevers < 0.3) {
                            if (degreeCalcRevers >= 0.95 * degree && degreeCalcRevers <= 1.05 * degree && correlatRevers > 0.95) {
                                if (elongationRevers > elongationReversMax) {
                                    elongationReversMax = elongationRevers;
//                        System.out.println("Degree calculation: " + degreeCalcRevers + "\tCorrelat: " + correlatRevers + " j: " + numSumRevers);
//                                    System.out.println("elongationMaxRevers: " + elongationRevers + " defEndRevers:" + defEndRevers);
                                    double durationMax = defEndRevers - defStartRevers;
                                    String resultReversString = df.format(correlatRevers);
                                    String defStartString = df.format(defStartRevers);
                                    String elongReversString = df.format(defEndRevers);
                                    String elongationMaxReversString = df.format(durationMax);
                                    logEnd = x3;
                                    answerRevers = "\nS0=" + k + " " + "n=" + degree + "\nНачало стадии: " + defStartString + "\tОкончание стадии: " + elongReversString + "\n"
                                            + "Продолжительность: " + elongationMaxReversString + "\tОтклонение: R=" + resultReversString + "\n" +
                                            "Ln(e)_start=" + logStart + "\tLn(e)_end=" + logEnd + "\n";
//                        System.out.println("Начало стадии: " + defStartString + " Окончание стадии: " + initialDefEndRevers + " Продолжительность: " + initiaElongation + " Отклонение: R=" + initialResult);
//                        // Лучше использовать ЭТОТ уточненный интервал, утоняющий линейную аппроксимацию справа налево в исследуемом диапазоне деформаций
//                        System.out.println("Revers! Начало стадии: " + defStartString + " Окончание стадии: " + elongReversString + " Продолжительность: " + elongationMaxReversString + " Отклонение: R=" + resultReversString);
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }//Revers end

                System.out.println("Direct: \t" + answer);
                System.out.println("Revers: \t" + answerRevers);
                if (!answerRevers.equals("")) {
                    result += answerRevers + "\n";
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            result = "ERROR";
            return result;
        }
        return result;
    }
}
