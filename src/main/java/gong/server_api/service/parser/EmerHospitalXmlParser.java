package gong.server_api.service.parser;

import javax.xml.parsers.DocumentBuilderFactory;

import gong.server_api.domain.dto.searchList.EmerHospitalDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EmerHospitalXmlParser {

    public List<EmerHospitalDto> parse(String xmlResponse) {
        List<EmerHospitalDto> hospitals = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            NodeList itemList = doc.getElementsByTagName("item");

            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                EmerHospitalDto hospital = new EmerHospitalDto();

                //hospital.setResultCode(XmlUtil.getTagValue("resultCode", item));
               // hospital.setResultMsg(XmlUtil.getTagValue("resultMsg", item));
                hospital.setHpid(XmlUtil.getTagValue("hpid", item));
                hospital.setWgs84Lon(XmlUtil.getTagValue("wgs84Lon", item));
                hospital.setWgs84Lat(XmlUtil.getTagValue("wgs84Lat", item));
                hospital.setDgidIdName(XmlUtil.getTagValue("dgidIdName", item));
                hospital.setDutyName(XmlUtil.getTagValue("dutyName", item)); //잠시 추가
  /*
                hospital.setDutyName(XmlUtil.getTagValue("dutyName", item));
                hospital.setPostCdn1(XmlUtil.getTagValue("postCdn1", item));
                hospital.setPostCdn2(XmlUtil.getTagValue("postCdn2", item));
                hospital.setDutyAddr(XmlUtil.getTagValue("dutyAddr", item));
                hospital.setDutyTel1(XmlUtil.getTagValue("dutyTel1", item));
                hospital.setDutyTel3(XmlUtil.getTagValue("dutyTel3", item));
                hospital.setHvec(XmlUtil.getTagValue("hvec", item));
                hospital.setHvoc(XmlUtil.getTagValue("hvoc", item));
                hospital.setHvcc(XmlUtil.getTagValue("hvcc", item));
                hospital.setHvncc(XmlUtil.getTagValue("hvncc", item));
                hospital.setHvccc(XmlUtil.getTagValue("hvccc", item));
                hospital.setHvicc(XmlUtil.getTagValue("hvicc", item));
                hospital.setHvgc(XmlUtil.getTagValue("hvgc", item));
                hospital.setDutyHayn(XmlUtil.getTagValue("dutyHayn", item));
                hospital.setDutyHano(XmlUtil.getTagValue("dutyHano", item));
                hospital.setDutyInf(XmlUtil.getTagValue("dutyInf", item));
                hospital.setDutyMapimg(XmlUtil.getTagValue("dutyMapimg", item));
                hospital.setDutyEryn(XmlUtil.getTagValue("dutyEryn", item));
                hospital.setMKioskTy25(XmlUtil.getTagValue("MKioskTy25", item).trim());
                hospital.setMKioskTy1(XmlUtil.getTagValue("MKioskTy1", item).trim());
                hospital.setMKioskTy2(XmlUtil.getTagValue("MKioskTy2", item).trim());
                hospital.setMKioskTy3(XmlUtil.getTagValue("MKioskTy3", item).trim());
                hospital.setMKioskTy4(XmlUtil.getTagValue("MKioskTy4", item).trim());
                hospital.setMKioskTy5(XmlUtil.getTagValue("MKioskTy5", item).trim());
                hospital.setMKioskTy6(XmlUtil.getTagValue("MKioskTy6", item).trim());
                hospital.setMKioskTy7(XmlUtil.getTagValue("MKioskTy7", item).trim());
                hospital.setMKioskTy8(XmlUtil.getTagValue("MKioskTy8", item).trim());
                hospital.setMKioskTy9(XmlUtil.getTagValue("MKioskTy9", item).trim());
                hospital.setMKioskTy10(XmlUtil.getTagValue("MKioskTy10", item).trim());
                hospital.setMKioskTy11(XmlUtil.getTagValue("MKioskTy11", item).trim());
                hospital.setHpbdn(XmlUtil.getTagValue("hpbdn", item));
                hospital.setHpccuyn(XmlUtil.getTagValue("hpccuyn", item));
                hospital.setHpcuyn(XmlUtil.getTagValue("hpcuyn", item));
                hospital.setHperyn(XmlUtil.getTagValue("hperyn", item));
                hospital.setHpgryn(XmlUtil.getTagValue("hpgryn", item));
                hospital.setHpicuyn(XmlUtil.getTagValue("hpicuyn", item));
                hospital.setHpnicuyn(XmlUtil.getTagValue("hpnicuyn", item));
                hospital.setHpopyn(XmlUtil.getTagValue("hpopyn", item));
*/
                hospitals.add(hospital);
            }
        } catch (Exception e) {
            log.error("Error parsing XML response: {}", e.getMessage());
        }
        return hospitals;
    }

}