package app.util;

import app.dto.DataRecord;

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
}