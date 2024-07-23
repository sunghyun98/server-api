package gong.server_api.domain.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HospitalConnectionStatusDto {
    private Long id;

    private String userId;

    private boolean isConnected;

    private String organizationName;

    private LocalDateTime lastConnected;

    private String role;
}
