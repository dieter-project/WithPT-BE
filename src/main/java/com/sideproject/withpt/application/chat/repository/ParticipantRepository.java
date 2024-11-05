package com.sideproject.withpt.application.chat.repository;

import com.sideproject.withpt.domain.chat.Participant;
import com.sideproject.withpt.domain.chat.Room;
import com.sideproject.withpt.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByRoomAndUser(Room room, User user);
}
