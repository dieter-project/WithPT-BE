package com.sideproject.withpt.application.chat.repository.room;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.chat.repository.message.MessageRepository;
import com.sideproject.withpt.application.chat.repository.participant.ParticipantRepository;
import com.sideproject.withpt.application.chat.service.response.RoomInfoResponse;
import com.sideproject.withpt.application.user.UserRepository;
import com.sideproject.withpt.common.type.Role;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class ChatRoomQueryRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MessageRepository messageRepository;


    @DisplayName("조회 유저의 모든 채팅방 리스트 조회")
    @Test
    void findAllRoomInfoBy() {
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
        List<RoomInfoResponse> responses = chatRoomRepository.findAllRoomInfoBy(user);

        // then
        assertThat(responses).hasSize(3)
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