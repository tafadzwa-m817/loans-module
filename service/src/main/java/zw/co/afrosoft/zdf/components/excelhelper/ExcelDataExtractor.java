package zw.co.afrosoft.zdf.components.excelhelper;

import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDate;

/**
 * Author Terrance Nyamfukudza
 * Date: 3/27/25
 */
public interface ExcelDataExtractor {

    String getStringValue(Row row, int cellIndex);
    LocalDate getDateValue(Row row, int cellIndex);
    Double getNumericValue(Row row, int cellIndex);
    <T extends Enum<T>> T getEnumValue(Row row, int cellIndex, Class<T> enumType);
}
