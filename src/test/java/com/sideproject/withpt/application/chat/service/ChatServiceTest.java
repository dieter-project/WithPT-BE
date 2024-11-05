package com.sideproject.withpt.application.chat.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.chat.contoller.request.CreateRoomRequest;
import com.sideproject.withpt.application.chat.exception.ChatException;
import com.sideproject.withpt.application.chat.repository.room.ChatRoomRepository;
import com.sideproject.withpt.application.chat.repository.message.MessageRepository;
import com.sideproject.withpt.application.chat.repository.participant.ParticipantRepository;
import com.sideproject.withpt.application.chat.service.response.CreateRoomResponse;
import com.sideproject.withpt.application.chat.service.response.RoomInfoResponse;
import com.sideproject.withpt.application.chat.service.response.RoomListResponse;
import com.sideproject.withpt.application.user.UserRepository;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.RoomType;
import com.sideproject.withpt.domain.chat.Participant;
import com.sideproject.withpt.domain.chat.Room;
import com.sideproject.withpt.domain.user.User;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
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