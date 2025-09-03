package zw.co.afrosoft.zdf.report;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;


/**
 * Service interface for generating reports in various formats using JasperReports.
 */
public interface ReportService {

    /**
     * Generates a PDF report using the specified report template and parameters.
     *
     * @param reportName the name of the JasperReports template (without file extension)
     * @param params     a map of parameters to pass into the report
     * @return a byte array containing the generated PDF report
     * @throws IOException   if there is an issue reading the report file
     * @throws JRException   if there is an error compiling or filling the report
     * @throws SQLException  if a database error occurs during report generation
     */
    byte[] generateReport(String reportName, Map<String, Object> params)
            throws IOException, JRException, SQLException;

    /**
     * Generates a Word (DOCX) report using the specified report template and parameters.
     *
     * @param reportName the name of the JasperReports template (without file extension)
     * @param params     a map of parameters to pass into the report
     * @return a byte array containing the generated Word report
     * @throws JRException   if there is an error compiling or filling the report
     * @throws SQLException  if a database error occurs during report generation
     * @throws IOException   if there is an issue writing the report to Word format
     */
    byte[] generateToWord(String reportName, Map<String, Object> params)
            throws JRException, SQLException, IOException;

    /**
     * Generates an Excel (XLSX) report using the specified report template and parameters.
     *
     * @param reportName the name of the JasperReports template (without file extension)
     * @param params     a map of parameters to pass into the report
     * @return a byte array containing the generated Excel report
     * @throws JRException   if there is an error compiling or filling the report
     * @throws SQLException  if a database error occurs during report generation
     * @throws IOException   if there is an issue writing the report to Excel format
     */
    byte[] generateToExcel(String reportName, Map<String, Object> params)
            throws JRException, SQLException, IOException;
}

