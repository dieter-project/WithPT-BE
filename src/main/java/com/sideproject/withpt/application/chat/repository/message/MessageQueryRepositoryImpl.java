package com.sideproject.withpt.application.chat.repository.message;

import static com.sideproject.withpt.domain.chat.QMessage.message;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.chat.Message;
import com.sideproject.withpt.domain.chat.Room;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MessageQueryRepositoryImpl implements MessageQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Message> findAllMessageBy(Room room, Pageable pageable) {
        List<Message> messages = jpaQueryFactory
            .selectFrom(message)
            .where(message.room.eq(room))
            .orderBy(message.id.desc())
            .offset(pageable.getOffset()) // Pageable의 offset
            .limit(pageable.getPageSize() + 1) // 다음 페이지 여부 확인을 위해 +1
            .fetch();

        boolean hasNext = messages.size() > pageable.getPageSize();
        if (hasNext) {
            messages.remove(messages.size() - 1); // 초과된 데이터 제거
        }

        return new SliceImpl<>(messages, pageable, hasNext);
    }
}
