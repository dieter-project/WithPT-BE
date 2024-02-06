package com.sideproject.withpt.application.chat.repository;

import com.sideproject.withpt.domain.chat.Message;
import com.sideproject.withpt.domain.chat.Room;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Modifying
    @Query("UPDATE Message c " +
        "SET c.notRead = c.notRead - 1 " +
        "WHERE c.room = :room " +
        "AND c.id > :startId " +
        "AND c.id <= :endId " +
        "AND c.notRead > 0")
    void decrementNotRead(
        @Param("room") Room room,
        @Param("startId") Long startId,
        @Param("endId") Long endId
    );
}
