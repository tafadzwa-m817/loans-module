package zw.co.afrosoft.zdf.util;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import zw.co.afrosoft.zdf.dto.LoanRequestDto;
import zw.co.afrosoft.zdf.enums.LoanType;
import zw.co.afrosoft.zdf.enums.Prefixes;
import zw.co.afrosoft.zdf.loans.Loan;
import zw.co.afrosoft.zdf.loans.LoanRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.time.LocalDateTime.now;
import static zw.co.afrosoft.zdf.enums.LoanStatus.WAIT_APPROVAL;



@RequiredArgsConstructor
public class LoanUtil {
    private final LoanRepository loanRepository;

    public void processCSVFile(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<Loan> loans = reader.lines().skip(1).map(line -> {
                String[] data = line.split(",");
//                LoanRequestDto dto = new LoanRequestDto(data[0], Double.parseDouble(data[1]),
//                        Integer.parseInt(data[2]), Prefixes.valueOf(data[3]), data[4]);
                return new Loan(
                );
            }).collect(Collectors.toList());
            loanRepository.saveAll(loans);
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file", e);
        }
    }

    public void processExcelFile(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            List<Loan> loans = new ArrayList<>();

            if (rowIterator.hasNext()) rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                LoanRequestDto dto = new LoanRequestDto();
                row.getCell(0).getStringCellValue();
                row.getCell(1).getNumericCellValue();
                row.getCell(2).getNumericCellValue();
                Prefixes.valueOf(row.getCell(3).getStringCellValue());
                row.getCell(4).getStringCellValue();

                loans.add(new Loan(
                ));
            }
            loanRepository.saveAll(loans);
        } catch (Exception e) {
            throw new RuntimeException("Error processing Excel file", e);
        }
    }

}