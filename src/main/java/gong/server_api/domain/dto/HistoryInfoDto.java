package gong.server_api.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HistoryInfoDto {
    private Integer id;
    private Integer historyId;
    private String title;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private LocalDate date;
}