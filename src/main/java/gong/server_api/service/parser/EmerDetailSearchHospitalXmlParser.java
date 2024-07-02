package gong.server_api.service.parser;

import gong.server_api.domain.dto.EmerDetailSearchHospitalDto;
import gong.server_api.domain.entity.user.HospitalAi;
import gong.server_api.domain.entity.user.HospitalData;
import gong.server_api.repository.HospitalAiRepo;
import gong.server_api.repository.HospitalDataRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EmerDetailSearchHospitalXmlParser {

    private final HospitalAiRepo hospitalAiRepo;
    private final HospitalDataRepo hospitalDataRepo;

    public EmerDetailSearchHospitalXmlParser(HospitalAiRepo hospitalAiRepo, HospitalDataRepo hospitalDataRepo) {
        this.hospitalAiRepo = hospitalAiRepo;
        this.hospitalDataRepo = hospitalDataRepo;
    }

    public List<EmerDetailSearchHospitalDto> parse(String xmlResponse) {
        List<EmerDetailSearchHospitalDto> hospitals = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            NodeList itemList = doc.getElementsByTagName("item");

            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                EmerDetailSearchHospitalDto hospital = new EmerDetailSearchHospitalDto();

                String hpid = XmlUtil.getTagValue("hpid", item);
                hospital.setHpid(hpid);
                hospital.setDutyName(XmlUtil.getTagValue("dutyName", item));
                hospital.setWgs84Lon(XmlUtil.getTagValue("wgs84Lon", item));
                hospital.setWgs84Lat(XmlUtil.getTagValue("wgs84Lat", item));

                hospitals.add(hospital);

                // hpid를 통해 dgid_id_name 조회
                String dgidIdName = hospitalAiRepo.findDgidIdNameByHpid(hpid);

                // hospital_ai 테이블에 데이터 저장
                HospitalData hospitalDataEntity = new HospitalData();
                hospitalDataEntity.setHpid(hpid);
                hospitalDataEntity.setDutyName(hospital.getDutyName());
                hospitalDataEntity.setWgs84Lon(hospital.getWgs84Lon());
                hospitalDataEntity.setWgs84Lat(hospital.getWgs84Lat());
                hospitalDataEntity.setDgidIdName(dgidIdName);

                hospitalDataRepo.save(hospitalDataEntity);
                log.info("Hospital data saved for hpid: {}", hpid);
            }
        } catch (Exception e) {
            log.error("Error parsing XML response: {}", e.getMessage());
        }
        return hospitals;
    }
}