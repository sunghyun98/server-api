package gong.server_api.domain.dto.searchList;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HospitalCombined {
    private String hpid; // 기관ID
    private String dutyName;   // 기관명
    private String dutyTel3;  // 응급실 전화번호
    private String dgidIdName; // 진료과목
    private String hvidate;         // 입력일시
    private String hvec;            // 일반 병상
    private String hvoc;            // 수술실
    private String hvicc;           // 중환자실 (일반)
    private String hvgc;            // 입원실 (일반)
    private String hv1;             // 응급실 당직의 직통 연락처
    private String wgs84Lon; // 병원경도
    private String wgs84Lat; // 병원위도
    private double distance; // 거리 정보를 담을 필드
}
