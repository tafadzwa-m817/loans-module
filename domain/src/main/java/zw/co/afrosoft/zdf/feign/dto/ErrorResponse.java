package zw.co.afrosoft.zdf.feign.dto;

import lombok.Builder;


@Builder
public record ErrorResponse(
        int status,
        String error
) {
}
