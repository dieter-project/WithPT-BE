package com.sideproject.withpt.application.chat.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.chat.contoller.request.CreateRoomRequest;
import com.sideproject.withpt.application.chat.contoller.request.ReadMessageRequest;
import com.sideproject.withpt.application.chat.exception.ChatException;
import com.sideproject.withpt.application.chat.facade.request.MessageDto;
import com.sideproject.withpt.application.chat.repository.message.MessageRepository;
import com.sideproject.withpt.application.chat.repository.participant.ParticipantRepository;
import com.sideproject.withpt.application.chat.repository.room.ChatRoomRepository;
import com.sideproject.withpt.application.chat.service.response.CreateRoomResponse;
import com.sideproject.withpt.application.chat.service.response.MessageResponse;
import com.sideproject.withpt.application.chat.service.response.ReadMessageResponse;
import com.sideproject.withpt.application.chat.service.response.RoomListResponse;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.record.diet.repository.DietRepository;
import com.sideproject.withpt.application.user.UserRepository;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.MessageType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.RoomType;
import com.sideproject.withpt.domain.chat.Message;
import com.sideproject.withpt.domain.chat.Participant;
import com.sideproject.withpt.domain.chat.Room;
import com.sideproject.withpt.domain.record.diet.Diets;
import com.sideproject.withpt.domain.user.User;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Transactional
//@ActiveProfiles("test")
@SpringBootTest
class ChatServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private DietRepository dietRepository;

    @Autowired
    private ChatService chatService;

    @DisplayName("채팅방 생성")
    @Nested
    class CreateRoom {

        @DisplayName("신규 채팅방 생성")
        @Test
        void createRoom() {
            // given
            User initiator = userRepository.save(createMember("회원"));
            User partner = userRepository.save(createTrainer("트레이너"));

            CreateRoomRequest request = CreateRoomRequest.builder()
                .id(partner.getId())
                .build();

            // when
            CreateRoomResponse response = chatService.createRoom(initiator.getId(), request);

            // then
            assertThat(response.getMessage()).isEqualTo("채팅방이 생성되었습니다");
            assertThat(response.getRoom())
                .extracting("roomType", "roomName", "unreadMessageCount", "lastReadMessageId", "lastChat", "lastModifiedDate")
                .contains(RoomType.INDIVIDUAL, partner.getName(), 0, null, "", null);

            assertThat(response.getRoom().getPartner())
                .extracting("name", "role")
                .contains("트레이너", Role.TRAINER);

            List<Participant> participants = participantRepository.findAll();
            assertThat(participants).hasSize(2)
                .extracting("user", "roomName")
                .contains(
                    tuple(initiator, partner.getName()),
                    tuple(partner, initiator.getName())
                );
        }

        @DisplayName("신규 채팅방 2개 생성")
        @Test
        void createRoom2() {
            // given
            User initiator = userRepository.save(createMember("회원"));
            User partner1 = userRepository.save(createTrainer("트레이너1"));
            User partner2 = userRepository.save(createTrainer("트레이너2"));

            CreateRoomRequest request1 = CreateRoomRequest.builder()
                .id(partner1.getId())
                .build();

            CreateRoomRequest request2 = CreateRoomRequest.builder()
                .id(partner2.getId())
                .build();

            // when
            chatService.createRoom(initiator.getId(), request1);
            chatService.createRoom(initiator.getId(), request2);

            // then
            List<Participant> participants = participantRepository.findAll();
            assertThat(participants).hasSize(4)
                .extracting("user")
                .contains(
                    initiator, initiator, partner1, partner2
                );
        }

        @DisplayName("채팅방이 이미 존재할 때")
        @Test
        void roomAlreadyExist() {
            // given
            User initiator = userRepository.save(createMember("회원"));
            User partner = userRepository.save(createTrainer("트레이너"));

            CreateRoomRequest request = CreateRoomRequest.builder()
                .id(partner.getId())
                .build();

            String identifier = generateIdentifierBySHA256(initiator, partner);
            Room room = chatRoomRepository.save(Room.create(identifier));

            participantRepository.saveAll(List.of(
                Participant.create(initiator, room, partner.getName()),
                Participant.create(partner, room, initiator.getName()))
            );

            // when
            CreateRoomResponse response = chatService.createRoom(initiator.getId(), request);

            // then
            assertThat(response.getMessage()).isEqualTo("채팅 방이 이미 존재합니다.");
            assertThat(response.getRoom())
                .extracting("roomType", "roomName", "unreadMessageCount", "lastReadMessageId", "lastChat", "lastModifiedDate")
                .contains(RoomType.INDIVIDUAL, partner.getName(), 0, null, "", null);

        }

        @DisplayName("채팅을 요청받은 유저의 식별자 값이 잘못되었습니다")
        @Test
        void INVALID_REQUESTED_CHAT_IDENTIFIER() {
            // given
            User initiator = userRepository.save(createMember("회원"));
            User partner = userRepository.save(createTrainer("트레이너"));

            CreateRoomRequest request = CreateRoomRequest.builder()
                .id(3214L)
                .build();

            String identifier = generateIdentifierBySHA256(initiator, partner);
            Room room = chatRoomRepository.save(Room.create(identifier));

            participantRepository.saveAll(List.of(
                Participant.create(initiator, room, partner.getName()),
                Participant.create(partner, room, initiator.getName()))
            );

            // when // then
            assertThatThrownBy(() -> chatService.createRoom(initiator.getId(), request))
                .isInstanceOf(ChatException.class)
                .hasMessage("채팅을 요청받은 유저의 식별자 값이 잘못되었습니다")
            ;
        }
    }

    @DisplayName("조회 유저의 모든 채팅방 리스트 조회")
    @Test
    void getRoomList() {
        // given
        User user = userRepository.save(createMember("회원"));
        User partner1 = userRepository.save(createTrainer("트레이너1"));
        User partner2 = userRepository.save(createTrainer("트레이너2"));
        User partner3 = userRepository.save(createTrainer("트레이너3"));

        String identifier1 = generateIdentifierBySHA256(user, partner1);
        String identifier2 = generateIdentifierBySHA256(user, partner2);
        String identifier3 = generateIdentifierBySHA256(user, partner3);
        Room room1 = chatRoomRepository.save(Room.create(identifier1));
        Room room2 = chatRoomRepository.save(Room.create(identifier2));
        Room room3 = chatRoomRepository.save(Room.create(identifier3));

        participantRepository.saveAll(List.of(
            Participant.create(user, room1, partner1.getName()),
            Participant.create(partner1, room1, user.getName()))
        );

        participantRepository.saveAll(List.of(
            Participant.create(user, room2, partner2.getName()),
            Participant.create(partner2, room2, user.getName()))
        );

        participantRepository.saveAll(List.of(
            Participant.create(user, room3, partner3.getName()),
            Participant.create(partner3, room3, user.getName()))
        );

        // when
        RoomListResponse response = chatService.getRoomList(user.getId());

        // then
        assertThat(response.getRoomList()).hasSize(3)
            .extracting("roomName", "partner.name")
            .contains(
                Tuple.tuple("트레이너1", "트레이너1"),
                Tuple.tuple("트레이너2", "트레이너2"),
                Tuple.tuple("트레이너3", "트레이너3")
            );

    }

    @DisplayName("유형별 메세지 저장")
    @Nested
    class SaveMessage {

        @DisplayName("일반 메세지 저장")
        @Test
        void saveGeneralMessage() {
            // given
            User sender = userRepository.save(createMember("회원"));
            User receiver = userRepository.save(createTrainer("트레이너"));
            Room room = chatRoomRepository.save(createRoom());

            participantRepository.saveAll(List.of(
                createParticipant(sender, room, receiver.getName()),
                createParticipant(receiver, room, sender.getName()))
            );

            MessageDto request = MessageDto.builder()
                .roomId(room.getId())
                .messageType(MessageType.TALK)
                .sender(sender.getId())
                .receiver(receiver.getId())
                .message("안녕하세요")
                .notRead(1)
                .build();
            LocalDateTime sentAt = LocalDateTime.of(2024, 11, 19, 16, 24);

            // when
            MessageResponse response = chatService.saveMessage(request, sentAt);

            // then
            assertThat(response)
                .extracting("roomId", "sender.name", "receiver.name")
                .contains(room.getId(), sender.getName(), receiver.getName());

            assertThat(response)
                .extracting("message", "messageType", "notRead", "sentAt")
                .contains("안녕하세요", MessageType.TALK, 1, sentAt);
        }

        @DisplayName("식단 공유 메세지 저장")
        @Test
        void saveDietMessage() {
            // given
            User sender = userRepository.save(createMember("회원"));
            User receiver = userRepository.save(createTrainer("트레이너"));
            Room room = chatRoomRepository.save(createRoom());

            participantRepository.saveAll(List.of(
                createParticipant(sender, room, receiver.getName()),
                createParticipant(receiver, room, sender.getName()))
            );

            Diets diets = dietRepository.save(createDiets(DietType.DIET, (Member) sender, LocalDate.now()));

            LocalDateTime sentAt = LocalDateTime.of(2024, 11, 19, 16, 24);

            String message = sender.getName() + " 회원님\n" + String.format("%d월 %d일 %s 식단", sentAt.getMonthValue(), sentAt.getDayOfMonth(), sentAt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN));
            MessageDto request = MessageDto.builder()
                .roomId(room.getId())
                .messageType(MessageType.DIET)
                .sender(sender.getId())
                .receiver(receiver.getId())
                .relatedEntityId(diets.getId())
                .message(message)
                .notRead(1)
                .build();

            // when
            MessageResponse response = chatService.saveMessage(request, sentAt);

            // then
            assertThat(response)
                .extracting("roomId", "sender.name", "receiver.name")
                .contains(room.getId(), sender.getName(), receiver.getName());

            assertThat(response)
                .extracting("message", "messageType", "notRead", "sentAt")
                .contains(message, MessageType.DIET, 1, sentAt);
        }
    }

    @DisplayName("메세지 읽기")
    @Rollback(value = false)
    @Test
    void readMessage() {
        // given
        User sender1 = userRepository.save(createMember("회원1"));
        User sender2 = userRepository.save(createMember("회원2"));
        User receiver = userRepository.save(createTrainer("트레이너"));
        Room room1 = chatRoomRepository.save(createRoom());
        Room room2 = chatRoomRepository.save(createRoom());

        participantRepository.saveAll(List.of(
            createParticipant(sender1, room1, receiver.getName()),
            createParticipant(receiver, room1, sender1.getName()))
        );
        participantRepository.saveAll(List.of(
            createParticipant(sender2, room2, receiver.getName()),
            createParticipant(receiver, room2, sender2.getName()))
        );

        messageRepository.save(createMessage(room1, sender1, receiver, MessageType.TALK, "메세지1", 1));
        messageRepository.save(createMessage(room1, sender1, receiver, MessageType.TALK, "메세지2", 1));
        Message start = messageRepository.save(createMessage(room2, sender2, receiver, MessageType.TALK, "메세지3", 1));
        messageRepository.save(createMessage(room1, sender1, receiver, MessageType.TALK, "메세지4", 1));
        messageRepository.save(createMessage(room1, sender1, receiver, MessageType.TALK, "메세지5", 1));
        messageRepository.save(createMessage(room2, sender2, receiver, MessageType.TALK, "메세지6", 1));
        Message end = messageRepository.save(createMessage(room2, sender2, receiver, MessageType.TALK, "메세지7", 1));

        ReadMessageRequest request = ReadMessageRequest.builder()
            .userId(sender2.getId())
            .roomId(room2.getId())
            .lastReadMessageIdRange(List.of(start.getId() - 1, end.getId()))
            .build();

        // when
        ReadMessageResponse response = chatService.readMessage(request);

        // then
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

    private Diets createDiets(DietType dietType, Member member, LocalDate uploadDate) {
        return Diets.builder()
            .member(member)
            .uploadDate(uploadDate)
            .totalCalorie(1200)
            .totalProtein(200)
            .totalCarbohydrate(300)
            .totalFat(400)
            .targetDietType(dietType)
            .build();
    }

    private String generateIdentifierBySHA256(User user1, User user2) {
        String rawIdentifier = user1.getId() + "_" + user2.getId();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawIdentifier.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}