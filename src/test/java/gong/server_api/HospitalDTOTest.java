package gong.server_api;
import gong.server_api.domain.dto.hopitalDto.HospitalDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HospitalDTOTest {

    @Test
    public void testHospitalDTO() {
        // Given
        HospitalDTO hospitalDTO = new HospitalDTO();
        hospitalDTO.setSIGUN_NM("서울시");
        hospitalDTO.setBIZPLC_NM("강남병원");
        hospitalDTO.setSICKBD_CNT(100);
        hospitalDTO.setREFINE_WGS84_LAT(37.1234);
        hospitalDTO.setREFINE_WGS84_LOGT(127.5678);

        // When
        String sigunName = hospitalDTO.getSIGUN_NM();
        String bizplcName = hospitalDTO.getBIZPLC_NM();
        int sickbedCount = hospitalDTO.getSICKBD_CNT();
        double latitude = hospitalDTO.getREFINE_WGS84_LAT();
        double longitude = hospitalDTO.getREFINE_WGS84_LOGT();

        // Then
        assertEquals("서울시", sigunName);
        assertEquals("강남병원", bizplcName);
        assertEquals(100, sickbedCount);
        assertEquals(37.1234, latitude, 0.0001); // delta 값은 0.0001로 설정하여 부동 소수점 오차를 허용합니다.
        assertEquals(127.5678, longitude, 0.0001);
    }
}