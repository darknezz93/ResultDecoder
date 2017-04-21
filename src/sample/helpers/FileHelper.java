package sample.helpers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class FileHelper {

    private Map<String, File> resourcesMap = new HashMap<>();

    public void processDirectory(File directory) {
        File[] insideDirectories = directory.listFiles();
        for(File file: insideDirectories) {
            resourcesMap.put(file.getName(), file.listFiles()[0]);
        }
    }

    public void saveCsvResultFile(File file) throws IOException {
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

    public void saveCsvValidationFile(File file) throws IOException, NoSuchAlgorithmException {
        FileWriter fileWriter = null;
        fileWriter = new FileWriter(file);

        fileWriter = writeSerialNumberValidation(fileWriter);
        fileWriter = writeResultMd5Validation(fileWriter);

        fileWriter.flush();
        fileWriter.close();

    }

    private FileWriter writeResultMd5Validation(FileWriter fileWriter) throws IOException, NoSuchAlgorithmException {
        List<String> invalidMd5Users = retrieveInvalidMd5Users();

        if(invalidMd5Users.size() > 0) {
            CSVUtils.writeLine(fileWriter, Arrays.asList(""));
            CSVUtils.writeLine(fileWriter, Arrays.asList(""));
            CSVUtils.writeLine(fileWriter, Arrays.asList("Użytkownicy z niepoprawną sumą kontrolną treści logu:"));
            CSVUtils.writeLine(fileWriter, Arrays.asList("Imię", "Nazwisko", "Indeks"));
            for(String str: invalidMd5Users) {
                String[] elements = str.split("_");
                String name = elements[0];
                String surname = elements[1];
                String index = elements[2];
                CSVUtils.writeLine(fileWriter, Arrays.asList(name, surname, index));
            }
        }

        return fileWriter;
    }

    private List<String> retrieveInvalidMd5Users() throws FileNotFoundException, NoSuchAlgorithmException {
        List<String> invalidMd5Users = new ArrayList<>();
        for(Map.Entry<String, File> entry : resourcesMap.entrySet()) {
            String md5Checksum = getOverallInfoMd5Checksum(entry.getValue());
            String retrievedResult = getResultStringFromFile(entry.getValue());
            String retrievedMD5Result = MD5Hashing(retrievedResult);
            if(!md5Checksum.equals(retrievedMD5Result)) {
                invalidMd5Users.add(entry.getKey());
            }
        }
        return invalidMd5Users;
    }

    private String MD5Hashing(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    private String getResultStringFromFile(File file) throws FileNotFoundException {
        List<String> lst = new ArrayList<>();
        Scanner sc = new Scanner(file);
        while(sc.hasNextLine()){
            lst.add(sc.nextLine());
        }

        String overallInfo = "";
        for(int i = 1; i < lst.size() - 4; i++) {
            overallInfo += lst.get(i).substring(lst.get(i).lastIndexOf("]: ") + 3) + "\n";
        }

        return overallInfo;
    }

    private String getOverallInfoMd5Checksum(File file) throws FileNotFoundException {
        List<String> lst = new ArrayList<>();
        Scanner sc = new Scanner(file);
        while(sc.hasNextLine()){
            lst.add(sc.nextLine());
        }
        String line = lst.get(lst.size() - 2);
        String overallInfoMd5 = line.substring(line.lastIndexOf("info: ") + 6);

        return overallInfoMd5;
    }

    private FileWriter writeSerialNumberValidation(FileWriter fileWriter) throws IOException {

        Multimap<String, String> sameSerialNumberUsers = getSameSerialNumberUsers();

        if(sameSerialNumberUsers.keySet().size() > 0) {

            CSVUtils.writeLine(fileWriter, Arrays.asList("Użytkownicy z tymi samymi numerami dysków twardych: "));
            CSVUtils.writeLine(fileWriter, Arrays.asList("Imię", "Nazwisko", "Indeks", "MD5 numeru seryjnego"));

            for (Map.Entry<String, Collection<String>> entry : sameSerialNumberUsers.asMap().entrySet()) {
                String serialNumber = entry.getKey();
                Collection<String> allStrCollection = entry.getValue();

                if(allStrCollection.size() > 1) {
                    for(String str: allStrCollection) {
                        String[] elements = str.split("_");
                        String name = elements[0];
                        String surname = elements[1];
                        String index = elements[2];
                        CSVUtils.writeLine(fileWriter, Arrays.asList(name, surname, index, serialNumber));
                    }
                    CSVUtils.writeLine(fileWriter, Arrays.asList(""));
                }
            }
        }
        return fileWriter;
    }

    private Multimap<String, String> getSameSerialNumberUsers() throws IOException {
        Map<String, String> userSerialNumberMap = new HashMap<>();
        for(Map.Entry<String, File> entry : resourcesMap.entrySet()) {
            String serialNumber = retrieveSerialNumber(entry.getValue());
            userSerialNumberMap.put(entry.getKey(), serialNumber);
        }

        Multimap<String, String> multiMap = HashMultimap.create();
        for (Map.Entry<String, String> entry : userSerialNumberMap.entrySet()) {
            multiMap.put(entry.getValue(), entry.getKey());
        }

        return multiMap;
    }

    private String retrieveSerialNumber(File file) throws IOException {
        List<String> lst = new ArrayList<>();
        Scanner sc = new Scanner(file);
        while(sc.hasNextLine()){
            lst.add(sc.nextLine());
        }
        String line = lst.get(lst.size() - 3);
        String serialNumber = line.substring(line.lastIndexOf("SN: ") + 4);

        return serialNumber;
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
