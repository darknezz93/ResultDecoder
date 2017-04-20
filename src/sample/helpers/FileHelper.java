package sample.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class FileHelper {

    private Map<String, File> resourcesMap = new HashMap<>();

    private Map<String, Boolean> overallInfoValidation = new HashMap<>();


    public void processDirectory(File directory) {
        File[] insideDirectories = directory.listFiles();
        for(File file: insideDirectories) {
            resourcesMap.put(file.getName(), file.listFiles()[0]);
        }
    }

    public void saveCsvFile(File file) throws IOException {
        FileWriter fileWriter = null;
        fileWriter = new FileWriter(file);

        CSVUtils.writeLine(fileWriter, Arrays.asList("Imię", "Nazwisko", "Indeks", "Wynik"));
        for(Map.Entry<String, File> entry : resourcesMap.entrySet()) {
            String[] elements = entry.getKey().split("_");
            String name = elements[0];
            String surname = elements[1];
            String index = elements[2];
            String overallResult = parseOverallScore(entry.getValue());
            CSVUtils.writeLine(fileWriter, Arrays.asList(name, surname, index, overallResult));
        }
        fileWriter.flush();
        fileWriter.close();
    }

    private String parseOverallScore(File file) throws FileNotFoundException {
        List<String> lst = new ArrayList<>();
        Scanner sc = new Scanner(file);
        while(sc.hasNextLine()){
            lst.add(sc.nextLine());
        }

        String line = lst.get(lst.size() - 4);
        String overall = line.substring(line.lastIndexOf(" - ") + 3);

        return overall;
    }



}
