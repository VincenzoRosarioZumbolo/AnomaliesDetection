package app.service;

import app.exception.ApiRequestException;
import app.exception.DataParsingException;
import app.exception.NetworkException;
import app.model.DataRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface DataSourceService {

    List<DataRecord> fetchData(String asset, String granularity, LocalDateTime startDate, LocalDateTime endDate)
            throws NetworkException, ApiRequestException, DataParsingException;
}