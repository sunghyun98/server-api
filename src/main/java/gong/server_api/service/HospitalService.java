package gong.server_api.service;

import gong.server_api.domain.dto.hopitalDto.HospitalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Service
public class HospitalService {

    private final String HOSPITAL_API_URL = "https://openapi.gg.go.kr/Hospital";
    private final String API_KEY = "ba6170b672c340529ce25fc796ba0df2"; // 본인이 발급받은 API 키로 변경해주세요


}