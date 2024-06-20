package gong.server_api.repository;

import gong.server_api.domain.entity.user.Chat;
import gong.server_api.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PersonalChatRepository extends JpaRepository<Chat, Long> {
    // 특정 유저가 받은 모든 채팅 목록 조회
    @Query("SELECT c FROM Chat c WHERE c.receiverUser = :receiverUser")
    List<Chat> findByReceiverUser(@Param("receiverUser") User receiverUser);

    // 특정 유저가 보낸 모든 채팅 목록 조회
    @Query("SELECT c FROM Chat c WHERE c.senderUser = :senderUser")
    List<Chat> findBySenderUser(@Param("senderUser") User senderUser);
}
