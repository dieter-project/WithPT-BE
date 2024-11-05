package com.sideproject.withpt.application.chat.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.chat.contoller.request.CreateRoomRequest;
import com.sideproject.withpt.application.chat.exception.ChatException;
import com.sideproject.withpt.application.chat.repository.ChatRoomRepository;
import com.sideproject.withpt.application.chat.repository.MessageRepository;
import com.sideproject.withpt.application.chat.repository.ParticipantRepository;
import com.sideproject.withpt.application.chat.service.response.CreateRoomResponse;
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