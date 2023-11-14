package sample;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Logic {

    String fileName;
    String path;
    String fileNameCrop;
    FileWriter fileWriter;
    boolean existence;
    DecimalFormat df = new DecimalFormat("0.000000");
    ArrayList<Point> pointsMassive = new ArrayList<>();
    ArrayList<Point> pointsMassiveNext = new ArrayList<>();

    public String find(File file) throws IOException {

        fileName = file.getName();
        fileNameCrop = fileName.substring(0, fileName.lastIndexOf('x') + 1);
        path = String.valueOf(file);
        path = path.substring(0, path.lastIndexOf("\\") + 1);
        System.out.println("fileName = " + fileName);
        System.out.println("fileNameCrop = " + fileNameCrop);
        System.out.println("path = " + path);

        Matcher matcher = Pattern.compile("\\d+").matcher(fileName);
        matcher.find();
        int fileNum = Integer.parseInt(matcher.group());

        try {
            readLines(file, pointsMassive);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        for (int i = fileNum; i < 100; i++) {
            fileNum = fileNum + 1;
            fileName = path + fileNameCrop + fileNum + ".DAT";
            File fileNext = new File(fileName);
            if (fileNext.exists()) {
                System.out.println(fileName);
                readLines(fileNext, pointsMassiveNext);
                for (Point pointNext : pointsMassiveNext) {
                    existence = false;
                    for (Point point : pointsMassive) {
                        if (pointNext.equals(point)) {
                            point.Exx = pointNext.getExx() + pointNext.getExx();
                            existence = true;
                        }
                    }
                    if (!existence){
                        //добавить точку в расчет
                    }
                }
            }
        }
        fileWriter = new FileWriter(path + "summ.DAT");
        for (Point point : pointsMassive) {
            String dfX = df.format(point.getCoordinateX());
            String dfY = df.format(point.getCoordinateY());
            String dfExx = df.format(point.getExx());
            fileWriter.write(dfX + " " + dfY + " " + dfExx + "\n");
        }
        fileWriter.flush();
        return "Done!";
    }

    private void readLines(File file, ArrayList<Point> pointsMassive) throws IOException {
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
        for (String s : lines) {
            Point point = new Point(0, 0, 0);
            String[] split1 = s.split(" ");

            point.coordinateX = Double.parseDouble(split1[0]);
            point.coordinateY = Double.parseDouble(split1[1]);
            point.Exx = Double.parseDouble(split1[2]);
            pointsMassive.add(point);
        }
    }
}
