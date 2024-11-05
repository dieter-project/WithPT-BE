package com.sideproject.withpt.application.chat.repository.room;

import com.sideproject.withpt.domain.chat.Room;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<Room, Long>, ChatRoomQueryRepository {

    Optional<Room> findByIdentifier(String identifier);
}
