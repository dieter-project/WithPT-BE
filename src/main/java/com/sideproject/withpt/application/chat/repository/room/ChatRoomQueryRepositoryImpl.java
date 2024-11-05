package com.sideproject.withpt.application.chat.repository.room;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.chat.service.response.MessageResponse;
import com.sideproject.withpt.application.chat.service.response.QRoomInfoResponse;
import com.sideproject.withpt.application.chat.service.response.RoomInfoResponse;
import com.sideproject.withpt.application.user.response.QUserResponse;
import com.sideproject.withpt.domain.chat.QParticipant;
import com.sideproject.withpt.domain.user.QUser;
import com.sideproject.withpt.domain.user.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<RoomInfoResponse> findAllRoomInfoBy(User user) {

        QParticipant p1 = QParticipant.participant;
        QParticipant p2 = new QParticipant("p2");
        QUser qUser = QUser.user;

        return jpaQueryFactory
            .select(new QRoomInfoResponse(
                p1.room.id,
                p1.room.identifier,
                p1.room.type,
                p1.roomName,
                new QUserResponse(
                    qUser.id, qUser.name, qUser.email, qUser.role, qUser.imageUrl, qUser.birth, qUser.sex
                ), // 상대방 User 정보 매핑
                p1.unreadMessageCount,
                p1.lastReadMessageId,
                p1.room.lastChat,
                p1.createdDate,
                p1.lastModifiedDate
            ))
            .from(p1)
            .join(p2).on(p1.room.eq(p2.room)) // 동일한 room_id를 가진 다른 참가자와 조인
            .join(qUser).on(qUser.id.eq(p2.user.id)) // 상대방의 User 정보를 가져옴
            .where(p1.user.eq(user)
                .and(p2.user.ne(user))) // p2의 participant_id가 1이 아닌 경우만 필터링
            .fetch();
    }

    public List<MessageResponse> findAllChattingList(Long roomId, Long cursor) {

//        List<Message> result = jpaQueryFactory
//            .selectFrom(message1)
//            .where(
//                message1.room.id.eq(roomId),
//                message1.id.lt(cursor)
//            )
//            .orderBy(
//                message1.id.desc()
//            )
//            .limit(50)
//            .fetch();

//        return result.stream()
//            .sorted(Comparator.comparingLong(Message::getId))
//            .map(message -> MessageResponse.from(message, message.getRoom()))
//            .collect(toList());
        return null;
    }

//    private BooleanExpression trainerEq(Trainer trainer) {
//        return ObjectUtils.isEmpty(trainer) ? null : participant.trainer.eq(trainer);
//    }
//
//    private BooleanExpression memberEq(Member member) {
//        return ObjectUtils.isEmpty(member) ? null : participant.member.eq(member);
//    }
}
