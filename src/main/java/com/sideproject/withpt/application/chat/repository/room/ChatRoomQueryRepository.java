package com.sideproject.withpt.application.chat.repository.room;

import com.sideproject.withpt.application.chat.service.response.RoomInfoResponse;
import com.sideproject.withpt.domain.user.User;
import java.util.List;

public interface ChatRoomQueryRepository {

    List<RoomInfoResponse> findAllRoomInfoBy(User user);

}
