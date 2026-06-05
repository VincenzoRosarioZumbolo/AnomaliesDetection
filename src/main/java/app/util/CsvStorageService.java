package app.util;

import app.model.DataRecord;
import app.model.FinancialIndicators;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Service utility layer responsible for handling persistent disk input/output operations.
 * <p>Manages saving, merging, and retrieving historical asset market pricing blocks and calculated
 * technical financial indicator metrics using local CSV storage files.</p>
 */
public class CsvStorageService {

    /**
     * Relative path directory location mapping where local asset pricing data records are stored.
     */
    private static final String DATA_PATH = "storage/data/";

    /**
     * Relative path directory location mapping where historical calculated technical indicator values are stored.
     */
    private static final String INDICATORS_PATH = "storage/indicators/";

    /**
     * Saves a list of historical pricing data records into a dedicated CSV file.
     * If the file already exists, it reads any existing lines, merges them chronologically
     * with the new arrivals, eliminates data overrides, and rewrites the complete collection.
     *
     * @param fileName   The destination name of the target CSV file.
     * @param newRecords The list containing new market data record rows to store.
     * @throws IOException If disk folders cannot be generated or file write streams fail.
     */
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

    /**
     * Reads and parses a financial dataset CSV file, filtering the records based on an
     * optional bounding start and end date criteria timeframe.
     *
     * @param fileName  The name of the target source database file.
     * @param startDate The lower timeline boundary filter context. If null, starts from the oldest entry.
     * @param endDate   The upper timeline boundary filter context. If null, parses up to maximum values.
     * @return A sorted {@link List} containing parsed domain {@link DataRecord} instances.
     * @throws IOException If the target resource experiences stream processing file extraction runtime errors.
     */
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

    /**
     * Logs calculated indicator parameters into a designated technical indicators tracking CSV file.
     * It tracks calculations using a combined unique reference tracking key composed of the timestamp
     * and the size of the underlying evaluation dataset.
     *
     * @param fileName            The destination filename targeting an indicator sheet.
     * @param timeStamp           The exact epoch instant associated with this indicator assessment entry.
     * @param dataSize            The size or record row count of the sample set used during computation.
     * @param financialIndicators The dataset containing computed values for RSI, MACD, ATR, and CMF.
     * @throws IOException        If folder initialization blocks occur or write handles are rejected by the OS.
     */
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

    /**
     * Searches an indicator log file to retrieve a map of calculated indicator values.
     * Matches logs using a compound lookup key of the target timestamp and context dataset row size.
     *
     * @param fileName      The name of the indicator log file to search.
     * @param timeStamp     The specific evaluation instant required for lookup matching.
     * @param dataSize      The volume size bounds of the database rows matching the calculation scope.
     * @return A {@link Map} pairing technical indicators names to their numeric values, or null if no record matches.
     * @throws IOException  If file streaming or read actions are interrupted.
     */
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