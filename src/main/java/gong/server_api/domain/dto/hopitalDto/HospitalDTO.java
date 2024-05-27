package gong.server_api.domain.dto.hopitalDto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDTO {

    private String SIGUN_NM; //시군명
    private String BIZPLC_NM; //사업장 명
    private String SICKBD_CNT; //병상 수
    private String REFINE_WGS84_LAT; //위도
    private String REFINE_WGS84_LOGT; //경도
}
