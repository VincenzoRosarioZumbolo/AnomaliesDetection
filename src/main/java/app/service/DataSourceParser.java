package app.service;

import app.exception.DataParsingException;
import app.dto.DataRecord;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Service provider interface definition for mapping raw API JSON response structures
 * into application domain object models.
 */
public interface DataSourceParser {

    /**
     * Deserializes and maps a raw root hierarchical JSON tree node structure into a list
     * of structured domain data records.
     *
     * @param data The root Jackson {@link JsonNode} containing incoming raw database server blocks.
     * @return A mapped {@link List} containing structural domain {@link DataRecord} entries.
     * @throws DataParsingException If structural elements fail serialization criteria due to unexpected JSON schemas.
     */
    List<DataRecord> parseData(JsonNode data) throws DataParsingException;
}