package gong.server_api.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import gong.server_api.domain.dto.hospitalDto.HospitalDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class HospitalResponse {

    @JacksonXmlProperty(localName = "body")
    private Body body;

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JacksonXmlProperty(localName = "items")
        private Items items;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        @JacksonXmlProperty(localName = "item")
        private List<HospitalDTO> hospitals;
    }
}