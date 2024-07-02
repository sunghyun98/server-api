package gong.server_api.service.API;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class EmerHospitalApi {

    @Value("${hospitalId.api.url}")
    private String hospitalId;

    @Value("${hospitalDetail.api.url}")
    private String hospitalDetail;

    @Value("${hospitalRearTime.api.url}")
    private String hospitalRearTime;

    @Value("${hospitalMessage.api.url}")
    private String hospitalMessage;

    @Value("${hospitalStage.api.url}")
    private String hospitalStage;

    @Value("${hospital.api.key}")
    private String apiKey;

    @Value("${test.api.key}")
    private String testKey;

    @Value("${test.api.url}")
    private String testUrl;

    public String callApiTest(String hpid) throws Exception {
        String urlStr = testUrl + "?"
                + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(testKey, StandardCharsets.UTF_8)
                + "&"
                + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(String.valueOf(1), StandardCharsets.UTF_8)
                + "&"
                + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(String.valueOf(120), StandardCharsets.UTF_8);

        return HttpUtil.getResponseFromUrl(urlStr);
    }

    public String callApi(String hpid) throws Exception {
        String urlStr = hospitalId + "?"
                + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
                + "&"
                + URLEncoder.encode("HPID", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(hpid, StandardCharsets.UTF_8)
                + "&"
                + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(String.valueOf(1), StandardCharsets.UTF_8)
                + "&"
                + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(String.valueOf(100), StandardCharsets.UTF_8);

        return HttpUtil.getResponseFromUrl(urlStr);
    }

    public String callApiDetail(String Q0, String Q1, String QD, String QN) throws Exception {
        // 빈 문자열이 아닌 경우에만 URLEncoder.encode() 메서드를 사용하여 인코딩합니다.
        Q0 = (Q0 != null && !Q0.isEmpty()) ? URLEncoder.encode(Q0, StandardCharsets.UTF_8) : null;
        Q1 = (Q1 != null && !Q1.isEmpty()) ? URLEncoder.encode(Q1, StandardCharsets.UTF_8) : null;
        QD = (QD != null && !QD.isEmpty()) ? URLEncoder.encode(QD, StandardCharsets.UTF_8) : null;
        QN = (QN != null && !QN.isEmpty()) ? URLEncoder.encode(QN, StandardCharsets.UTF_8) : null;

        StringBuilder urlBuilder = new StringBuilder(hospitalDetail);
        urlBuilder.append("?");
        appendParam(urlBuilder, "serviceKey", apiKey);
        appendParam(urlBuilder, "Q0", Q0);
        appendParam(urlBuilder, "Q1", Q1);
        appendParam(urlBuilder, "QD", QD);
        appendParam(urlBuilder, "QN", QN);
        appendParam(urlBuilder, "pageNo", "1");
        appendParam(urlBuilder, "numOfRows", "600");

        String urlStr = urlBuilder.toString();
        return HttpUtil.getResponseFromUrl(urlStr);
    }
    public String callApiRealTime(String STAGE1) throws Exception {
        // 빈 문자열이 아닌 경우에만 URLEncoder.encode() 메서드를 사용하여 인코딩합니다.
        STAGE1 = (STAGE1 != null && !STAGE1.isEmpty()) ? URLEncoder.encode(STAGE1, StandardCharsets.UTF_8) : null;


        StringBuilder urlBuilder = new StringBuilder(hospitalStage);
        urlBuilder.append("?");
        appendParam(urlBuilder, "serviceKey", apiKey);
        appendParam(urlBuilder, "STAGE1", STAGE1);
        appendParam(urlBuilder, "pageNo", "1");
        appendParam(urlBuilder, "numOfRows", "100");

        String urlStr = urlBuilder.toString();
        return HttpUtil.getResponseFromUrl(urlStr);
    }

    public String callApiMessage(String HPID, String QN, String Q0, String Q1) throws Exception {
        // 빈 문자열이 아닌 경우에만 URLEncoder.encode() 메서드를 사용하여 인코딩합니다.
        HPID = (HPID != null && !HPID.isEmpty()) ? URLEncoder.encode(HPID, StandardCharsets.UTF_8) : null;
        QN = (QN != null && !QN.isEmpty()) ? URLEncoder.encode(QN, StandardCharsets.UTF_8) : null;
        Q0 = (Q0 != null && !Q0.isEmpty()) ? URLEncoder.encode(Q0, StandardCharsets.UTF_8) : null;
        Q1 = (Q1 != null && !QN.isEmpty()) ? URLEncoder.encode(QN, StandardCharsets.UTF_8) : null;

        StringBuilder urlBuilder = new StringBuilder(hospitalMessage);
        urlBuilder.append("?");
        appendParam(urlBuilder, "serviceKey", apiKey);
        appendParam(urlBuilder, "HPID", HPID);
        appendParam(urlBuilder, "QN", QN);
        appendParam(urlBuilder, "Q0", Q0);
        appendParam(urlBuilder, "Q1", Q1);
        appendParam(urlBuilder, "pageNo", "1");
        appendParam(urlBuilder, "numOfRows", "100");

        String urlStr = urlBuilder.toString();

        return HttpUtil.getResponseFromUrl(urlStr);
    }
    private void appendParam(StringBuilder urlBuilder, String paramName, String paramValue) {
        if (paramValue != null) {
            urlBuilder.append(URLEncoder.encode(paramName, StandardCharsets.UTF_8));
            urlBuilder.append("=");
            urlBuilder.append(paramValue);
            urlBuilder.append("&");
        }
    }
}