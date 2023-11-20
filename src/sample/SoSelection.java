package sample;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SoSelection {

    double logStart = 0, logEnd = 0;
    List<String> lines;

    public String find(String S001, String S002, String step0, String n, File file) {
        int startRow = 1, endRow = 1;
        double defStartRevers = 0;
        String answer = "";
        String answerRevers = "";
        String result = "";
        S001 = S001.replace(',', '.');
        S002 = S002.replace(',', '.');
        step0 = step0.replace(',', '.');
        n = n.replace(',', '.');
        double degree = Double.parseDouble(n);
        DecimalFormat df = new DecimalFormat("##.###");
        String degreeString = df.format(degree);
        try {
            double S01 = Double.parseDouble(S001);
            double S02 = Double.parseDouble(S002);
            double step = Double.parseDouble(step0);
            lines = readFile(file);
            int l = lines.size();
//            System.out.println("Lines: " + l);
            if (S01 > S02) {
                result = "Ошибка! Недопустимое значение интервала!";
                return result;
            }
            for (double k = S01; k <= S02; k = k + step) {
                String SO = df.format(k);
                double elongationMax = 0;
                double x1, x2, x3, y0, y00, y1, y2, y02, y3, y03;
//                System.out.println("k:" + k);
                for (int i = 1; i < l; i = i + 10) {
                    double correlat, degreeCalc, numSum = 0, S1 = 0, S2 = 0, S3 = 0, S4 = 0, S5 = 0;
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
                        S1 += x2;
                        S2 += y2;
                        S3 += x2 * y2;
                        S4 += x2 * x2;
                        S5 += y2 * y2;
                        numSum++;
                        double elongation = defEnd - defStart;
//                        System.out.println("S1:" + S1 + " S2:" + S2 + " S3:" + S3 + " S4:" + S4 + " j:" + j);
                        degreeCalc = Math.abs((numSum * S3 - S1 * S2) / (numSum * S4 - S1 * S1));  // определение коэффициента n
                        correlat = Math.abs((numSum * S3 - S1 * S2) / Math.sqrt((numSum * S4 - S1 * S1) * (numSum * S5 - S2 * S2))); // коэффициент корреляции Пирсона
                        if (elongation < 1.25 || degreeCalc >= degree * 0.96 && degreeCalc <= degree * 1.04 && correlat > 0.96) { // условие поиска стадии по максимальному диапазону
//                            System.out.println("degreeCalc:" + degreeCalc + " numSum: " + numSum + " elongation:" + elongation + " defStart:" + defStart);
//                            System.out.println("Degree calculation: " + degreeCalc + "\tCorrelat: " + correlat);
                            if (degreeCalc >= degree * 0.96 && degreeCalc <= degree * 1.04 && correlat > 0.96) {
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
                                        answer = "\nS0=" + SO + " " + "n=" + degreeString + "\nНачало стадии: " + initialDefStart + "\tОкончание стадии: " + initialDefEnd + "\n"
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
                        S11 += x3;
                        S21 += y3;
                        S31 += x3 * y3;
                        S41 += x3 * x3;
                        S51 += y3 * y3;
                        numSumRevers++;
//                    System.out.println("numSumRevers " + numSumRevers);
//                                        System.out.println("S: " + S11 + " " + S21 + " " + S31 + " " + S41 + " j: " + numSumRevers);//
                        degreeCalcRevers = Math.abs((numSumRevers * S31 - S11 * S21) / (numSumRevers * S41 - S11 * S11));  // определение коэффициента n
                        correlatRevers = Math.abs((numSumRevers * S31 - S11 * S21) / Math.sqrt((numSumRevers * S41 - S11 * S11) * (numSumRevers * S51 - S21 * S21))); // коэффициент корреляции Пирсона
//                    System.out.println("Degree calculation: " + degreeCalcRevers + "\tCorrelat: " + correlatRevers);
                        elongationRevers = defEndRevers - startPoint;
                        if (degreeCalcRevers >= 0.90 * degree && degreeCalcRevers <= 1.10 * degree && correlatRevers > 0.90 || elongationRevers < 0.4) {
                            if (degreeCalcRevers >= 0.90 * degree && degreeCalcRevers <= 1.10 * degree && correlatRevers > 0.90) {
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
                                    answerRevers = "\nS0=" + SO + " " + "n=" + degreeString + "\nНачало стадии: " + defStartString + "\tОкончание стадии: " + elongReversString + "\n"
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
                } else {
                    result += "\nS0=" + SO + " " + "n=" + degreeString + "\nНет подходящего интервала" + "\n";
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            result = "Ошибка! Проверьте правильность вводимых данных!";
            return result;
        }
        return result;
    }

    private List<String> readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.replace(',', '.');
            if (!line.contains("--")) {
                lines.add(line);
                lines.removeIf(item -> item == null || "".equals(item));
            }
        }
        return lines;
    }

    public void graph(StackPane root, String So) {
        double logStartY = 0, logEndY = 0;
        double startGraphX = 0;
        double startGraphY = 0;
        double endGraphX;
        double endGraphY;
        boolean bool = false;
        double CordinateX = 0;
        double CordinateY;
        double CordinateYMax = 0;

        //Defining the x axis
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("ln(e)");

        //Defining the y axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("ln(S)");

        //Creating the line chart
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);

        //Prepare XYChart.Series objects by setting data
        XYChart.Series<Number, Number> data = new XYChart.Series<>();
        Platform.runLater(() ->
                data.getNode().setStyle("-fx-stroke: black;")
        );
        XYChart.Series<Number, Number> calculation = new XYChart.Series<>();
        Platform.runLater(() ->
                calculation.getNode().setStyle("-fx-stroke: red;")
        );

        for (String string : lines) {
            String[] split1 = string.split("\t");
            CordinateX = Double.parseDouble(split1[1]);// Read ln(e) column
            double S = Double.parseDouble(split1[2]); // Read S column
            double S0 = Double.parseDouble(So);
            if (S > S0) {
                CordinateY = Math.log(S - S0);
                XYChart.Data chartData;
                chartData = new XYChart.Data(CordinateX, CordinateY);
                chartData.setNode(new ShowCoordinatesNode(CordinateX, CordinateY));
                chartData.getNode().setStyle("-fx-padding: 2px;-fx-background-color: black;");
                data.getData().add(chartData);

//                data.setNode(new ShowCoordinatesNode(CordinateX, CordinateY));
//                data.getData().add(new XYChart.Data<>(CordinateX, CordinateY));
                if (!bool) {
                    startGraphX = CordinateX;
                    startGraphY = CordinateY;
                    bool = true;
                }
                if (CordinateX == logStart) {
                    logStartY = CordinateY;
                }
                if (CordinateX == logEnd) {
                    logEndY = CordinateY;
                }
                if (CordinateY > CordinateYMax) {
                    CordinateYMax = CordinateY;
                }
//                System.out.println(CordinateX + " " + CordinateY);
            }
        }
        calculation.getData().add(new XYChart.Data<>(logStart, logStartY));
        calculation.getData().add(new XYChart.Data<>(logEnd, logEndY));

        endGraphX = CordinateX;
        endGraphY = CordinateYMax;
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        xAxis.setLowerBound(startGraphX - Math.abs(startGraphX * 0.01));
        xAxis.setUpperBound(endGraphX + Math.abs(endGraphX * 0.01));
        yAxis.setLowerBound(startGraphY - Math.abs(startGraphY * 0.01));
        yAxis.setUpperBound(endGraphY + Math.abs(endGraphY * 0.01));

        //Setting the data to Line chart
        lineChart.getData().add(data);
        lineChart.getData().add(calculation);
        root.getChildren().add(lineChart);
    }
}
