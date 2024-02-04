package com.sideproject.withpt.application.chat.repository;

import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.domain.chat.Participant;
import com.sideproject.withpt.domain.chat.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Participant findByRoomAndRole(Room room, Role role);
}
