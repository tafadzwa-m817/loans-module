package zw.co.afrosoft.zdf.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;



@Data
@Schema(description = "Generic paginated response wrapper")
public class PageResponse<T> {

    @Schema(
            description = "The actual list of records returned in this page"
    )
    private List<T> content;

    @Schema(
            description = "Total number of pages available",
            example = "5"
    )
    private int totalPages;

    @Schema(
            description = "Total number of elements across all pages",
            example = "100"
    )
    private long totalElements;

    @Schema(
            description = "Number of records per page",
            example = "20"
    )
    private int size;

    @Schema(
            description = "Current page number (0-based index)",
            example = "0"
    )
    private int number;
}
