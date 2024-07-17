package gong.server_api.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@ToString
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer historyId;

    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private LocalDate date;

    @Column(name = "dgid_id_name")
    private String dgidIdName;

    @Column(name = "duty_name")
    private String dutyName;

    @Column(name = "hpid")
    private String hpid;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "usermail")
    private String usermail;

    @Column(name = "type")
    private String type;

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoryInfo> historyInfos = new ArrayList<>();

    // getters and setters
}