package com.sideproject.withpt.application.chat.service;

import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_ROOM_ALREADY_EXISTS;

import com.sideproject.withpt.application.chat.contoller.request.CreateRoomRequest;
import com.sideproject.withpt.application.chat.contoller.response.CreateRoomResponse;
import com.sideproject.withpt.application.chat.contoller.response.CreateRoomResponse.RoomInfo;
import com.sideproject.withpt.application.chat.repository.ChatRoomRepository;
import com.sideproject.withpt.application.chat.repository.ParticipantRepository;
import com.sideproject.withpt.application.member.service.MemberService;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.domain.chat.Participant;
import com.sideproject.withpt.domain.chat.Room;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final TrainerService trainerService;
    private final MemberService memberService;

    @Transactional
    public CreateRoomResponse createRoom(Long loginId, Role loginRole, CreateRoomRequest request) {
        return chatRoomRepository.findByIdentifier(request.getIdentifier())
            .map(existingRoom ->
                new CreateRoomResponse(
                    RoomInfo.createRoomInfo(existingRoom,
                        participantRepository.findByRoomAndRole(existingRoom, loginRole)),
                    CHAT_ROOM_ALREADY_EXISTS.getMessage()
                ))
            .orElseGet(() -> {
                Member member = (loginRole.equals(Role.TRAINER)) ? memberService.getMemberById(request.getId())
                    : memberService.getMemberById(loginId);
                Trainer trainer = (loginRole.equals(Role.TRAINER)) ? trainerService.getTrainerById(loginId)
                    : trainerService.getTrainerById(request.getId());

                Room room = chatRoomRepository.saveAndFlush(Room.createRoom(request.getIdentifier()));

                Arrays.stream(Role.values())
                    .forEach(role -> saveParticipant(trainer, member, role, room));

                return new CreateRoomResponse(
                    RoomInfo.createRoomInfo(room, participantRepository.findByRoomAndRole(room, loginRole)),
                    "채팅방이 생성되었습니다"
                );
            });
    }

    private void saveParticipant(Trainer trainer, Member member, Role role, Room room) {
        String participantName = (role.equals(Role.TRAINER)) ? member.getName() : trainer.getName();
        participantRepository.save(Participant.createParticipant(trainer, member, role, room, participantName));
    }
}
