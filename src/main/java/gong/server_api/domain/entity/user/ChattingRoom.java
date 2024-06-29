package gong.server_api.domain.entity.user;

import gong.server_api.domain.entity.Hospital;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chatting_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "hpid", nullable = false)
    private User senderId;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "hpid", nullable = false)
    private User receiverId;

    @OneToMany(mappedBy = "chattingRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats;

    // Getter and Setter methods
}