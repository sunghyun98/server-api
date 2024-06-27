package gong.server_api.domain.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "hospital_ai")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HospitalAi {

    @Id
    private Long id;

    @Column(name = "duty_name")
    private String dutyName;

    @Column(name = "dgid_id_name")
    private String dgidIdName;

    @Column(name = "wgs84Lat")
    private String wgs84Lat;

    @Column(name = "wgs84Lon")
    private String wgs84Lon;


    @Column(name = "hpid")
    private String hpid;
}
