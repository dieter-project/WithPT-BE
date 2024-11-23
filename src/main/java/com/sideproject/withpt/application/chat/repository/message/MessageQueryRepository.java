package com.sideproject.withpt.application.chat.repository.message;

import com.sideproject.withpt.domain.chat.Message;
import com.sideproject.withpt.domain.chat.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MessageQueryRepository {

    Slice<Message> findAllMessageBy(Room room, Pageable pageable);
}
