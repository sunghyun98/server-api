package gong.server_api.repository;

import gong.server_api.domain.entity.Hospital;
import gong.server_api.domain.entity.user.ChattingRoom;
import gong.server_api.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {
    Optional<ChattingRoom> findBySenderIdAndReceiverId(User senderId, User receiverId);
}
