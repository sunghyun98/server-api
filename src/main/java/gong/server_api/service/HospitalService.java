package gong.server_api.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import gong.server_api.domain.entity.HospitalMatch;
import gong.server_api.repository.HospitalMatchRepository;
import gong.server_api.service.response.PythonResponse;
import gong.server_api.domain.dto.EmerDetailSearchHospitalDto;
import gong.server_api.domain.dto.EmerHospitalDto;
import gong.server_api.domain.dto.HospitalMessageDto;
import gong.server_api.domain.dto.HospitalCombinedDto;
import gong.server_api.domain.dto.SearchHospitalDto;
import gong.server_api.service.API.EmerHospitalApi;
import gong.server_api.service.API.KakaoApi;
import gong.server_api.service.parser.EmerDetailSearchHospitalXmlParser;
import gong.server_api.service.parser.EmerHospitalXmlParser;
import gong.server_api.service.parser.HospitalMessageXmlParser;
import gong.server_api.service.response.SearchHospitalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HospitalService {

    private final EmerHospitalXmlParser emerHospitalXmlParser;
    private final EmerHospitalApi emerHospitalApi;
    private final EmerDetailSearchHospitalXmlParser emerDetailSearchHospitalXmlParser;
    private final HospitalMessageXmlParser hospitalMessageXmlParser;
    private final KakaoApi kakaoApi;
    private final HospitalMatchRepository hospitalMatchRepository;


    public EmerHospitalDto getEmerHospital(String hpid) {

        try {
            // API 호출하여 XML 데이터 받아오기
            String xmlResponse = emerHospitalApi.callApi(hpid);
            log.info("XML 응답: {}", xmlResponse);

            // XML 파싱 후 첫 번째 항목 반환
            List<EmerHospitalDto> hospitalList = emerHospitalXmlParser.parse(xmlResponse);
            if (!hospitalList.isEmpty()) {
                return hospitalList.get(0);
            } else {
                return null; // 또는 예외 처리를 해야 할 수도 있습니다.
            }
        } catch (Exception e) {
            log.error("병원 데이터 가져오는 중 오류 발생: ", e);
            return null;
        }
    }

    public List<EmerDetailSearchHospitalDto> getEmerDetailHospitals(String Q0, String Q1, String QD, String QN) {

        try {
            // API 호출하여 XML 데이터 받아오기
            String xmlResponse = emerHospitalApi.callApiDetail(Q0, Q1, QD, QN);
            log.info("XML 응답: {}", xmlResponse);

            // XML 파싱
            return emerDetailSearchHospitalXmlParser.parse(xmlResponse);
        } catch (Exception e) {
            log.error("병원 데이터 가져오는 중 오류 발생: ", e);
            return Collections.emptyList();
        }
    }

    public List<HospitalMessageDto> getHospitalMessageDto(String HPID, String QN, String Q0, String Q1) {
        try {
            // API 호출하여 XML 데이터 받아오기
            String xmlResponse = emerHospitalApi.callApiMessage(HPID, QN, Q0, Q1);
            log.info("XML 응답: {}", xmlResponse);

            // XML 파싱
            return hospitalMessageXmlParser.parse(xmlResponse);
        } catch (Exception e) {
            log.error("병원 데이터 가져오는 중 오류 발생: ", e);
            return Collections.emptyList();
        }
    }

    public List<SearchHospitalDto> searchHospitalDto(String stage1) {
        try {
            // API 호출하여 XML 데이터 받아오기
            String xmlResponse = emerHospitalApi.callApiRealTime(stage1);
            log.info("XML 응답: {}", xmlResponse);

            // XML 파싱
            XmlMapper xmlMapper = new XmlMapper();
            SearchHospitalResponse response = xmlMapper.readValue(xmlResponse, SearchHospitalResponse.class);
            log.info("Parsed Response: {}", response); // 파싱된 응답을 로그로 출력

            return response.getBody().getItems();
        } catch (Exception e) {
            log.error("병원 데이터 가져오는 중 오류 발생: ", e);
            return Collections.emptyList();
        }
    }

    public List<HospitalCombinedDto> getHospitalAddress(double latitude, double longitude) {
        try {
            String response = kakaoApi.callKakaoApi(String.valueOf(longitude), String.valueOf(latitude));
            // 응답 처리 및 region_1depth_name 추출
            JSONObject jsonObject = new JSONObject(response);
            String stage1 = jsonObject.getJSONArray("documents")
                    .getJSONObject(0)
                    .getJSONObject("address")
                    .getString("region_1depth_name");

            log.info("Region 1 Depth Name: {} ", stage1);
            // stage1 값을 변환
            switch (stage1) {
                case "경남":
                    stage1 = "경상남도";
                    break;
                case "경북":
                    stage1 = "경상북도";
                    break;
                case "충북":
                    stage1 = "충청북도";
                    break;
                case "충남":
                    stage1 = "충청남도";
                    break;
                // 필요시 다른 지역도 추가 가능
            }

            List<HospitalCombinedDto> hospitalCombinedDtoList = combineHospitalInfo(stage1, latitude, longitude);

            // 주어진 위치를 기준으로 병원들을 정렬하여 반환
            hospitalCombinedDtoList.sort(Comparator.comparingDouble(h -> calculateDistance(latitude, longitude, Double.parseDouble(h.getWgs84Lat()), Double.parseDouble(h.getWgs84Lon()))));

            return hospitalCombinedDtoList;

        } catch (Exception e) {
            log.error("Error while calling Kakao API", e);
            // 예외를 처리하고 빈 목록을 반환
            return new ArrayList<>();
        }

    }

    private List<HospitalCombinedDto> combineHospitalInfo(String stage1, double latitude, double longitude) {
        log.info("Fetching hospital messages for stage1: {}", stage1);

        // 서비스 호출하여 병원 메시지 목록 검색
        List<SearchHospitalDto> searchHospitalDto = searchHospitalDto(stage1);
        log.info("hospitalMessageDto: {}", searchHospitalDto);

        List<EmerHospitalDto> emerHospitalDto = new ArrayList<>();
        for (SearchHospitalDto aEmer : searchHospitalDto) {
            EmerHospitalDto bEmer = getEmerHospital(aEmer.getHpid());
            emerHospitalDto.add(bEmer);
        }

        // 정보 합치기
        List<HospitalCombinedDto> hospitalCombinedDtoList = new ArrayList<>();
        for (int i = 0; i < searchHospitalDto.size(); i++) {
            SearchHospitalDto aInfo = searchHospitalDto.get(i);
            EmerHospitalDto bInfo = emerHospitalDto.get(i);

            HospitalCombinedDto hospitalCombinedDto = new HospitalCombinedDto();
            hospitalCombinedDto.setHpid(aInfo.getHpid());
            hospitalCombinedDto.setDutyName(aInfo.getDutyName());
            hospitalCombinedDto.setDutyTel3(aInfo.getDutyTel3());
            hospitalCombinedDto.setDgidIdName(bInfo.getDgidIdName());
            hospitalCombinedDto.setHvidate(aInfo.getHvidate());
            hospitalCombinedDto.setHvec(aInfo.getHvec());
            hospitalCombinedDto.setHvoc(aInfo.getHvoc());
            hospitalCombinedDto.setHvicc(aInfo.getHvicc());
            hospitalCombinedDto.setHvgc(aInfo.getHvgc());
            hospitalCombinedDto.setHv1(aInfo.getHv1());
            hospitalCombinedDto.setWgs84Lon(bInfo.getWgs84Lon());
            hospitalCombinedDto.setWgs84Lat(bInfo.getWgs84Lat());
            // 거리 계산
            double distance = calculateDistance(latitude, longitude, Double.parseDouble(bInfo.getWgs84Lat()), Double.parseDouble(bInfo.getWgs84Lon()));
            hospitalCombinedDto.setDistance(distance);

            hospitalCombinedDtoList.add(hospitalCombinedDto);
        }
        return hospitalCombinedDtoList;
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 히버사인 공식(Haversine formula) 사용
        final int R = 6371; // 지구 반지름 (단위: km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    public List<HospitalCombinedDto> getHospitalAddress(double latitude, double longitude, PythonResponse pythonResponse, String userMail) {
        try {
            String response = kakaoApi.callKakaoApi(String.valueOf(longitude), String.valueOf(latitude));
            // 응답 처리 및 region_1depth_name 추출
            JSONObject jsonObject = new JSONObject(response);
            String stage1 = jsonObject.getJSONArray("documents")
                    .getJSONObject(0)
                    .getJSONObject("address")
                    .getString("region_1depth_name");

            log.info("Region 1 Depth Name: {} ", stage1);
            switch (stage1) {
                case "경남":
                    stage1 = "경상남도";
                    break;
                case "경북":
                    stage1 = "경상북도";
                    break;
                case "충북":
                    stage1 = "충청북도";
                    break;
                case "충남":
                    stage1 = "충청남도";
                    break;
                // 필요시 다른 지역도 추가 가능
            }
            List<String> pythonHpidList = pythonResponse.getHpid();

            List<HospitalCombinedDto> hospitalCombinedDtoList = combineHospitalInfo(stage1, latitude, longitude);

            // 필터링: Python 스크립트에서 반환된 HPID 목록에 포함된 병원만 남기기
            hospitalCombinedDtoList = hospitalCombinedDtoList.stream()
                    .filter(hospital -> pythonHpidList.contains(hospital.getHpid()))
                    .collect(Collectors.toList());

            // 주어진 위치를 기준으로 병원들을 정렬하여 반환
            hospitalCombinedDtoList.sort(Comparator.comparingDouble(h -> calculateDistance(latitude, longitude, Double.parseDouble(h.getWgs84Lat()), Double.parseDouble(h.getWgs84Lon()))));
            // HospitalMatch 엔티티 리스트로 변환
            saveHospitalMatches(hospitalCombinedDtoList, userMail);

            return hospitalCombinedDtoList;

        } catch (Exception e) {
            log.error("Error while calling Kakao API", e);
            // 예외를 처리하고 빈 목록을 반환
            return new ArrayList<>();
        }
    }
    private void saveHospitalMatches(List<HospitalCombinedDto> hospitalCombinedDtoList, String userMail) {
        List<HospitalMatch> hospitalMatches = hospitalCombinedDtoList.stream()
                .map(dto -> new HospitalMatch(null, userMail, dto.getHpid(), dto.getDutyName(), dto.getDutyTel3(), dto.getDgidIdName(),
                        dto.getHvidate(), dto.getHvec(), dto.getHvoc(), dto.getHvicc(), dto.getHvgc(),
                        dto.getHv1(), dto.getWgs84Lon(), dto.getWgs84Lat(), dto.getDistance()))
                .collect(Collectors.toList());

        hospitalMatchRepository.saveAll(hospitalMatches);
    }
    @Transactional
    public void deleteHospitalMatchesByUserHpid(String userMail) {
        try {
            // 해당 userHpid를 가지고 있는 HospitalMatch 엔티티들을 조회
            List<HospitalMatch> hospitalMatches = hospitalMatchRepository.findByUserMail(userMail);

            // 조회된 엔티티들을 삭제
            hospitalMatchRepository.deleteAll(hospitalMatches);

            log.info("Deleted {} HospitalMatch entities with userHpid: {}", hospitalMatches.size(), userMail);
        } catch (Exception e) {
            log.error("Error while deleting HospitalMatches for userHpid: {}", userMail, e);
            // 예외 처리, 로깅 등 필요한 작업 수행
        }
    }

}