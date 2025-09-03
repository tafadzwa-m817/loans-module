package zw.co.afrosoft.zdf.components.excelhelper.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.components.excelhelper.ExcelDataExtractor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Author Terrance Nyamfukudza
 * Date: 3/27/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelHelper implements ExcelDataExtractor {
    @Override
    public String getStringValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null && cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue().trim();
            return value.isEmpty() ? null : value;
        }
        return null;
    }

    @Override
    public LocalDate getDateValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        return null;
    }

    @Override
    public Double getNumericValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        return null;
    }

    @Override
    public <T extends Enum<T>> T getEnumValue(Row row, int cellIndex, Class<T> enumType) {
        String value = getStringValue(row, cellIndex);
        if (value != null) {
            try {
                return Enum.valueOf(enumType, value.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid enum value '{}' for {}", value, enumType.getSimpleName());
            }
        }
        return null;
    }

    public void updateIfNotNull(Row row, int cellIndex, Consumer<String> setter) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null && cell.getCellType() == CellType.STRING && !cell.getStringCellValue().trim().isEmpty()) {
            setter.accept(cell.getStringCellValue().trim());
        }
    }

    public void updateIfNotNullDate(Row row, int cellIndex, Consumer<LocalDate> setter) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            LocalDateTime dateTime = cell.getLocalDateTimeCellValue();
            if (dateTime != null) {
                setter.accept(dateTime.toLocalDate());
            }
        }
    }

    public <T extends Enum<T>> void updateIfNotNullEnum(Row row, int cellIndex, Function<String, T> enumConverter, Consumer<T> setter) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null && cell.getCellType() == CellType.STRING && !cell.getStringCellValue().trim().isEmpty()) {
            try {
                setter.accept(enumConverter.apply(cell.getStringCellValue().trim()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid enum value '{}' at row {}", cell.getStringCellValue(), row.getRowNum() + 1);
            }
        }
    }

    public void updateIfNotNullNumeric(Row row, int cellIndex, Consumer<Double> setter) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            setter.accept(cell.getNumericCellValue());
        }
    }

    public String getPhoneNumberValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return formatPhoneNumber(cell.getNumericCellValue());
        }
        return null;
    }
    private String formatPhoneNumber(Double phoneNumber) {
        return new BigDecimal(phoneNumber).toPlainString();
    }

}
