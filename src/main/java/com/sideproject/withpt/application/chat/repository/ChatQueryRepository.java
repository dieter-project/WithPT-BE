package com.sideproject.withpt.application.chat.repository;

import static com.sideproject.withpt.domain.chat.QMessage.message1;
import static com.sideproject.withpt.domain.chat.QParticipant.participant;
import static java.util.stream.Collectors.toList;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.chat.contoller.response.MessageResponse;
import com.sideproject.withpt.application.chat.contoller.response.QRoomListResponse_RoomInfo;
import com.sideproject.withpt.application.chat.contoller.response.RoomListResponse.RoomInfo;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.chat.Message;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<RoomInfo> findAllRoomInfo(Trainer trainer, Member member, Role role) {

        return jpaQueryFactory.select(
                new QRoomListResponse_RoomInfo(
                    participant.room.id,
                    participant.roomName,
                    participant.room.type,
                    participant.room.identifier,
                    ObjectUtils.isEmpty(member) ? participant.member.id : participant.trainer.id,
                    participant.room.lastChat,
                    participant.notReadChat,
                    participant.lastReadChatId,
                    participant.room.lastModifiedDate
                )
            )
            .from(participant)
            .join(participant.room)
            .where(
                trainerEq(trainer),
                memberEq(member),
                participant.role.eq(role)
            )
            .orderBy(
                participant.room.lastModifiedDate.desc()
            ).fetch();
    }

    public List<MessageResponse> findAllChattingList(Long roomId, Long cursor) {

        List<Message> result = jpaQueryFactory
            .selectFrom(message1)
            .where(
                message1.room.id.eq(roomId),
                message1.id.lt(cursor)
            )
            .orderBy(
                message1.id.desc()
            )
            .limit(50)
            .fetch();

        return result.stream()
            .sorted(Comparator.comparingLong(Message::getId))
            .map(message -> MessageResponse.from(message, message.getRoom()))
            .collect(toList());

    }

    private BooleanExpression trainerEq(Trainer trainer) {
        return ObjectUtils.isEmpty(trainer) ? null : participant.trainer.eq(trainer);
    }

    private BooleanExpression memberEq(Member member) {
        return ObjectUtils.isEmpty(member) ? null : participant.member.eq(member);
    }
}
