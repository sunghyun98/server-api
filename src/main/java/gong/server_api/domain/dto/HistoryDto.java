package gong.server_api.domain.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDto {
    private Integer historyId;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private LocalDate date;
    private String dgidIdName;
    private String dutyName;
    private String hpid;
    private String profileImage;
    private String usermail;
    private String type;
}