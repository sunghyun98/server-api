package gong.server_api.service;

import gong.server_api.domain.HospitalAi;
import gong.server_api.domain.dto.searchList.EmerHospitalDto;
import gong.server_api.service.API.EmerHospitalApi;
import gong.server_api.service.parser.EmerHospitalXmlParser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiService {
    private final EmerHospitalXmlParser emerHospitalXmlParser;
    private final EmerHospitalApi emerHospitalApi;

    @PersistenceContext
    private EntityManager entityManager;

    public AiService(EmerHospitalXmlParser emerHospitalXmlParser, EmerHospitalApi emerHospitalApi) {
        this.emerHospitalXmlParser = emerHospitalXmlParser;
        this.emerHospitalApi = emerHospitalApi;
    }

    @Transactional
    public void saveAllEmerHospitals(String hpid) {
        log.info("모든 응급 병원 데이터 저장 시작...");

        try {
            // API를 호출하여 XML 응답을 가져옵니다.
            String xmlResponse = emerHospitalApi.callApi(hpid);

            // XML 응답을 파싱하여 DTO 리스트를 가져옵니다.
            List<EmerHospitalDto> hospitals = emerHospitalXmlParser.parse(xmlResponse);

            // EmerHospitalDto를 HospitalAi 엔티티로 매핑하고 데이터베이스에 저장합니다.
            List<HospitalAi> hospitalEntities = hospitals.stream()
                    .map(dto -> {
                        HospitalAi entity = new HospitalAi();
                        entity.setHpid(dto.getHpid());
                        entity.setDutyName(dto.getDutyName());
                        entity.setDgidIdName(dto.getDgidIdName());
                        entity.setWgs84Lat(dto.getWgs84Lat());
                        entity.setWgs84Lon(dto.getWgs84Lon());
                        return entity;
                    })
                    .collect(Collectors.toList());

            // 대량 삽입을 위해 배치 크기를 설정합니다.
            int batchSize = 1000;
            for (int i = 0; i < hospitalEntities.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, hospitalEntities.size());
                List<HospitalAi> batch = hospitalEntities.subList(i, endIndex);

                // 엔티티 매니저를 사용하여 배치 단위로 데이터베이스에 저장합니다.
                batch.forEach(entityManager::persist);
                entityManager.flush();
                entityManager.clear();
            }

            log.info("대량 삽입이 완료되었습니다.");
        } catch (Exception e) {
            log.error("응급 병원 데이터 저장 중 오류 발생: ", e);
            throw new RuntimeException("응급 병원 데이터 저장 중 오류 발생");
        }
    }
}