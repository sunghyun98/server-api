package gong.server_api.domain.dto.searchList;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmerHospitalDto {
    private String hpid; // 기관ID
    private String wgs84Lon; // 병원경도
    private String wgs84Lat; // 병원위도
    private String dgidIdName; // 진료과목
    private String dutyName; // 기관명
    /*
    private String postCdn1; // 우편번호1
    private String postCdn2; // 우편번호2
    private String dutyAddr; // 주소
    private String dutyTel1; // 대표전화1
    private String dutyTel3; // 응급실전화
    private String hvec; // 응급실
    private String hvoc; // 수술실
    private String hvcc; // 신경중환자
    private String hvncc; // 신생중환자
    private String hvccc; // 흉부중환자
    private String hvicc; // 일반중환자
    private String hvgc; // 입원실
    private String dutyHayn; // 입원실가용여부(1/2)
    private String dutyHano; // 병상수
    private String dutyInf; // 기관설명상세
    private String dutyMapimg; // 간이약도
    private String dutyEryn; // 응급실운영여부(1/2)
    private String MKioskTy25; // 응급실(Emergency gate keeper)
    private String MKioskTy1; // 뇌출혈수술
    private String MKioskTy2; // 뇌경색의재관류
    private String MKioskTy3; // 심근경색의재관류
    private String MKioskTy4; // 복부손상의수술
    private String MKioskTy5; // 사지접합의수술
    private String MKioskTy6; // 응급내시경
    private String MKioskTy7; // 응급투석
    private String MKioskTy8; // 조산산모
    private String MKioskTy9; // 정신질환자
    private String MKioskTy10; // 신생아
    private String MKioskTy11; // 중증화상
    private String hpbdn; // 병상수
    private String hpccuyn; // 흉부중환자실
    private String hpcuyn; // 신경중환자실
    private String hperyn; // 응급실
    private String hpgryn; // 입원실
    private String hpicuyn; // 일반중환자실
    private String hpnicuyn; // 신생아중환자실
    private String hpopyn; // 수술실
    */
}