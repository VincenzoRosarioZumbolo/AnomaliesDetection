package app.util;

import app.model.DataRecord;
import app.model.FinancialIndicators;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class CsvStorageService {

    private static final String DATA_PATH = "storage/data/";
    private static final String INDICATORS_PATH = "storage/indicators/";

    public static void saveDataRecordsToCsv(String fileName, List<DataRecord> newRecords) throws IOException {

        File dir = new File(DATA_PATH);
        if (!dir.exists())
            if (!dir.mkdirs())
                throw new IOException("Cannot create directory: " + DATA_PATH);

        File file = new File(DATA_PATH + fileName);

        Map<Instant, DataRecord> combinedMap = new TreeMap<>();

        if (file.exists()) {
            List<DataRecord> existingRecords = loadDataRecordsFromCsv(fileName, null, null);

            for (DataRecord record : existingRecords)
                combinedMap.put(record.getTimestamp(), record);
        }

        for (DataRecord record : newRecords)
            combinedMap.put(record.getTimestamp(), record);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("timestamp,open,high,low,close,volume");

            for (DataRecord record : combinedMap.values()) {
                writer.printf(Locale.US, "%d,%.4f,%.4f,%.4f,%.4f,%d%n",
                        record.getTimestamp().getEpochSecond(),
                        record.getOpen(),
                        record.getHigh(),
                        record.getLow(),
                        record.getClose(),
                        record.getVolume());
            }
        }
    }

    public static List<DataRecord> loadDataRecordsFromCsv(String fileName, LocalDateTime startDate, LocalDateTime endDate) throws IOException {

        List<DataRecord> records = new ArrayList<>();
        File file = new File(DATA_PATH + fileName);
        if (!file.exists()) return records;

        long start = (startDate != null) ? startDate.toEpochSecond(ZoneOffset.UTC) : 0L;
        long end = (endDate != null) ? endDate.toEpochSecond(ZoneOffset.UTC) : Long.MAX_VALUE;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {

            bufferedReader.readLine();

            String line;

            while ((line = bufferedReader.readLine()) != null) {

                String[] values = line.split(",");

                if(Long.parseLong(values[0]) < start)
                    continue;
                if(Long.parseLong(values[0]) > end)
                    break;

                records.add(DataRecord.builder()
                        .timestamp(Instant.ofEpochSecond(Long.parseLong(values[0])))
                        .open(Double.parseDouble(values[1]))
                        .high(Double.parseDouble(values[2]))
                        .low(Double.parseDouble(values[3]))
                        .close(Double.parseDouble(values[4]))
                        .volume(Long.parseLong(values[5]))
                        .build());
            }
        }
        return records;
    }

    public static void saveIndicatorsToCsv(String fileName, Instant timeStamp, int dataSize, FinancialIndicators financialIndicators) throws IOException {

        File dir = new File(INDICATORS_PATH);
        if (!dir.exists())
            if (!dir.mkdirs())
                throw new IOException("Cannot create directory: " + INDICATORS_PATH);

        File file = new File(dir, fileName);
        Map<String, String> indicatorMap = new TreeMap<>();

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                br.readLine();
                String line;

                while ((line = br.readLine()) != null) {

                    String[] v = line.split(",");
                    indicatorMap.put(v[0] + "_" + v[1], line);
                }
            }
        }

        String newRecord = String.format(Locale.US, "%d,%d,%.4f,14,%.4f,12,26,9,%.4f,14,%.4f,20",
                timeStamp.getEpochSecond(), dataSize, financialIndicators.getRSI(), financialIndicators.getMACD(),
                financialIndicators.getATR(), financialIndicators.getCMF());
        indicatorMap.put(timeStamp.getEpochSecond() + "_" + dataSize, newRecord);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("timestamp,data_size,rsi_val,rsi_p,macd_val,macd_f,macd_s,macd_sig,atr_val,atr_p,cmf_val,cmf_p");
            for (String row : indicatorMap.values()) {
                writer.println(row);
            }
        }
    }

    public static Map<String, Double> loadIndicatorsFromCsv(String fileName, Instant timeStamp, int dataSize) throws IOException {

        File file = new File(INDICATORS_PATH + fileName);
        if (!file.exists()) return null;

        long targetTimeStamp = timeStamp.getEpochSecond();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {

                String[] v = line.split(",");

                if (Long.parseLong(v[0]) == targetTimeStamp && Integer.parseInt(v[1]) == dataSize) {

                    Map<String, Double> values = new HashMap<>();
                    values.put("RSI", Double.parseDouble(v[2]));
                    values.put("MACD", Double.parseDouble(v[4]));
                    values.put("ATR", Double.parseDouble(v[8]));
                    values.put("CMF", Double.parseDouble(v[10]));
                    return values;
                }
            }
        }
        return null;
    }
}