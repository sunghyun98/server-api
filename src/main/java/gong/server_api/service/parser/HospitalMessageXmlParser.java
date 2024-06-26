package gong.server_api.service.parser;

import gong.server_api.domain.dto.HospitalMessageDto;
import lombok.extern.slf4j.Slf4j;
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
public class HospitalMessageXmlParser {

    public List<HospitalMessageDto> parse(String xmlResponse) {
        List<HospitalMessageDto> hospitals = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            NodeList itemList = doc.getElementsByTagName("item");

            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                HospitalMessageDto hospital = new HospitalMessageDto();

                hospital.setRnum(XmlUtil.getTagValue("rnum", item));
                hospital.setDutyAddr(XmlUtil.getTagValue("dutyAddr", item));
                hospital.setDutyName(XmlUtil.getTagValue("dutyName", item));
                hospital.setEmcOrgCod(XmlUtil.getTagValue("emcOrgCod", item));
                hospital.setHpid(XmlUtil.getTagValue("hpid", item));
                hospital.setSymBlkMsg(XmlUtil.getTagValue("symBlkMsg", item));
                hospital.setSymBlkMsgTyp(XmlUtil.getTagValue("symBlkMsgTyp", item));
                hospital.setSymTypCod(XmlUtil.getTagValue("symTypCod", item));
                hospital.setSymTypCodMag(XmlUtil.getTagValue("symTypCodMag", item));
                hospital.setSymOutDspYon(XmlUtil.getTagValue("symOutDspYon", item));
                hospital.setSymOutDspMth(XmlUtil.getTagValue("symOutDspMth", item));
                hospital.setSymBlkSttDtm(XmlUtil.getTagValue("symBlkSttDtm", item));
                hospital.setSymBlkEndDtm(XmlUtil.getTagValue("symBlkEndDtm", item));

                hospitals.add(hospital);
            }
        } catch (Exception e) {
            log.error("Error parsing XML response: {}", e.getMessage());
        }
        return hospitals;
    }
}