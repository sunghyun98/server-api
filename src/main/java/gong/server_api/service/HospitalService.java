package gong.server_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import gong.server_api.domain.dto.hospitalDto.HospitalDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HospitalService {

    @Value("${hospital.api.url}")
    private String apiUrl;

    @Value("${hospital.api.key}")
    private String apiKey;

    public List<HospitalDTO> getHospitals(String stage1, String stage2) {
        log.info("getHospitals");

        try {
            // API 호출하여 XML 데이터 받아오기
            String xmlResponse = callApi(stage1, stage2);
            log.info("XML Response: {}", xmlResponse);

            // XML 파싱
            XmlMapper xmlMapper = new XmlMapper();
            log.info("xmlMapper: {}", xmlMapper);
            HospitalResponse hospitalResponse = xmlMapper.readValue(xmlResponse, HospitalResponse.class);
            log.info("Hospital Response: {}", hospitalResponse);

            // HospitalResponse를 JSON으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(hospitalResponse);
            log.info("JSON Response: {}", jsonResponse);

            // JSON 파싱
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            List<HospitalDTO> hospitals = new ArrayList<>();
            log.info("hospitals={}",hospitals);
            // 필요한 필드를 추출하여 HospitalDTO 객체로 만들기
            for (JsonNode node : jsonNode.path("body").path("items").path("item")) {
                HospitalDTO hospital = new HospitalDTO();
                hospital.setDutyName(node.path("dutyName").asText());
                hospital.setDutyTel3(node.path("dutyTel3").asText());
                log.info("hospital={}",hospital);
                hospitals.add(hospital);
            }

            return hospitals;
        } catch (Exception e) {
            log.error("Error fetching hospitals data: {}", e.getMessage());
            return null;
        }
    }

    private String callApi(String stage1, String stage2) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?")
                .append(URLEncoder.encode("serviceKey", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(apiKey, StandardCharsets.UTF_8))
                .append("&")
                .append(URLEncoder.encode("STAGE1", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(stage1, StandardCharsets.UTF_8))
                .append("&")
                .append(URLEncoder.encode("STAGE2", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(stage2, StandardCharsets.UTF_8))
                .append("&")
                .append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("1", StandardCharsets.UTF_8))
                .append("&")
                .append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("10", StandardCharsets.UTF_8));

        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        return result.toString();
    }
}