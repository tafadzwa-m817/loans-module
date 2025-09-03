package zw.co.afrosoft.zdf.feign.dto;


public record RankResponseDto(
        Long id,
        String name,//rank_name
        String rankAbbreviation
) {
}
