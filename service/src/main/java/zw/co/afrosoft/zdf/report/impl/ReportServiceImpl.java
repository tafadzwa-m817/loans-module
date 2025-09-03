package zw.co.afrosoft.zdf.report.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.report.ReportService;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;



@Data
@RequiredArgsConstructor
@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    private final DataSource dataSource;

    @Override
    public byte[] generateReport(String reportName, Map<String, Object> params)
            throws IOException, JRException, SQLException {

        log.info("Generating PDF report for: {}", reportName);

        try (
                InputStream jasperStream = getClass().getResourceAsStream("/templates/reports/" + reportName + ".jasper");
                Connection connection = dataSource.getConnection()
        ) {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }
    }


    @Override
    public byte[] generateToWord(String reportName, Map<String, Object> params)
            throws JRException, SQLException, IOException {

        log.info("Generating Word report for: {}", reportName);

        try (
                InputStream jasperStream = getClass().getResourceAsStream("/templates/reports/" + reportName + ".jasper");
                Connection connection = dataSource.getConnection();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);

            JRDocxExporter exporter = new JRDocxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

            SimpleDocxReportConfiguration config = new SimpleDocxReportConfiguration();
            exporter.setConfiguration(config);

            exporter.exportReport();

            return outputStream.toByteArray();
        }
    }

    @Override
    public byte[] generateToExcel(String reportName, Map<String, Object> params)
            throws JRException, SQLException, IOException {

        log.info("Generating Excel report for: {}", reportName);

        try (
                InputStream jasperStream = getClass().getResourceAsStream("/templates/reports/" + reportName + ".jasper");
                Connection connection = dataSource.getConnection();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);

            JRXlsxExporter exporter = getJrXlsxExporter(jasperPrint, outputStream);
            exporter.exportReport();

            return outputStream.toByteArray();
        }
    }

    private static JRXlsxExporter getJrXlsxExporter(JasperPrint jasperPrint, ByteArrayOutputStream outputStream) {
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);
        configuration.setIgnoreGraphics(false);
        configuration.setIgnoreTextFormatting(false);
        configuration.setOnePagePerSheet(false);

        exporter.setConfiguration(configuration);
        return exporter;
    }

}
