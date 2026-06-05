package app.service;

import app.exception.DataParsingException;
import app.model.DataRecord;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface DataSourceParser {

    List<DataRecord> parseData(JsonNode data) throws DataParsingException;
}
