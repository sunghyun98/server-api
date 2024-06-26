package gong.server_api.domain.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HospitalMessageDto {

    private String rnum; // 일련번호
    private String dutyAddr; // 기관주소
    private String dutyName; // 기관명
    private String emcOrgCod; // 기관코드
    private String hpid; // 기관 id
    private String symBlkMsg; // 전달메시지
    private String symBlkMsgTyp; // 메시지구분 A: 응급 / B: 중증
    private String symTypCod; // 중증질환구분
    private String symTypCodMag; // 중증질환명
    private String symOutDspYon; // 중증질환 표출구분
    private String symOutDspMth; // 표출 차단구분
    private String symBlkSttDtm; // 차단시작
    private String symBlkEndDtm; // 차단종료
}
