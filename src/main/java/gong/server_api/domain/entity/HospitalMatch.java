package gong.server_api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor // Required for JPA
@Table(name = "hospital_match")
public class HospitalMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_hpid", length = 100)
    private String userHpid;

    @Column(name = "hpid", length = 255, nullable = false)
    private String hpid;

    @Column(name = "duty_name", length = 255, nullable = false)
    private String dutyName;

    @Column(name = "duty_tel3", length = 20)
    private String dutyTel3;

    @Column(name = "dgid_id_name", columnDefinition = "TEXT")
    private String dgidIdName;

    @Column(name = "hvidate", length = 20)
    private String hvidate;

    @Column(name = "hvec", length = 10)
    private String hvec;

    @Column(name = "hvoc", length = 10)
    private String hvoc;

    @Column(name = "hvicc", length = 10)
    private String hvicc;

    @Column(name = "hvgc", length = 10)
    private String hvgc;

    @Column(name = "hv1", length = 20)
    private String hv1;

    @Column(name = "wgs84_lon", length = 50)
    private String wgs84Lon;

    @Column(name = "wgs84_lat", length = 50)
    private String wgs84Lat;

    @Column(name = "distance")
    private double distance;
}