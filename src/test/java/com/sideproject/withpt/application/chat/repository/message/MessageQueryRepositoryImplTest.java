package com.sideproject.withpt.application.chat.repository.message;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.chat.repository.participant.ParticipantRepository;
import com.sideproject.withpt.application.chat.repository.room.ChatRoomRepository;
import com.sideproject.withpt.application.user.UserRepository;
import com.sideproject.withpt.common.type.MessageType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.RoomType;
import com.sideproject.withpt.domain.chat.Message;
import com.sideproject.withpt.domain.chat.Participant;
import com.sideproject.withpt.domain.chat.Room;
import com.sideproject.withpt.domain.user.User;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class MessageQueryRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MessageRepository messageRepository;

    @DisplayName("현재 채팅방 메세지 리스트 조회")
    @Test
    void findAllMessageBy() {
        // given
        User sender = userRepository.save(createMember("회원"));
        User receiver = userRepository.save(createTrainer("트레이너"));
        Room room = chatRoomRepository.save(createRoom());

        participantRepository.saveAll(List.of(
            createParticipant(sender, room, receiver.getName()),
            createParticipant(receiver, room, sender.getName()))
        );

        List<Message> messages = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            messages.add(
                createMessage(room, sender, receiver, MessageType.TALK, "메세지" + i, 1)
            );
        }
        messageRepository.saveAll(messages);

        Pageable pageable = PageRequest.of(0, 5);
        // when
        Slice<Message> result = messageRepository.findAllMessageBy(room, pageable);

        // then
        assertThat(result.getContent())
            .extracting("message")
            .containsExactly(
                "메세지100",
                "메세지99",
                "메세지98",
                "메세지97",
                "메세지96"
            );
    }

    private Message createMessage(Room room, User sender, User receiver, MessageType messageType, String message, int notRead) {
        return Message.builder()
            .room(room)
            .sender(sender)
            .receiver(receiver)
            .type(messageType)
            .message(message)
            .notRead(notRead)
            .sentAt(LocalDateTime.now())
            .build();
    }

    public Participant createParticipant(User user, Room room, String roomName) {
        return Participant.builder()
            .user(user)
            .room(room)
            .roomName(roomName)
            .unreadMessageCount(0)
            .lastReadMessageId(null)
            .build();
    }

    public Room createRoom() {
        return Room.builder()
            .type(RoomType.INDIVIDUAL)
            .lastChat("")
            .build();
    }

    private Member createMember(String name) {
        return Member.builder()
            .email("test@test.com")
            .role(Role.MEMBER)
            .weight(80.0)
            .name(name)
            .build();
    }

    private Trainer createTrainer(String name) {
        return Trainer.signUpBuilder()
            .email("test@test.com")
            .role(Role.TRAINER)
            .name(name)
            .build();
    }
}