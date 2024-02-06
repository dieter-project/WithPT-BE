package com.sideproject.withpt.application.chat.service;

import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_LIST_LOAD_ERROR_MESSAGE;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_LIST_REQUEST_ERROR;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_ROOM_ALREADY_EXISTS;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_ROOM_CREATION_ERROR;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_ROOM_NOT_FOUND;

import com.sideproject.withpt.application.chat.contoller.request.CreateRoomRequest;
import com.sideproject.withpt.application.chat.contoller.request.MessageRequest;
import com.sideproject.withpt.application.chat.contoller.request.ReadMessageRequest;
import com.sideproject.withpt.application.chat.contoller.response.CreateRoomResponse;
import com.sideproject.withpt.application.chat.contoller.response.CreateRoomResponse.RoomInfo;
import com.sideproject.withpt.application.chat.contoller.response.MessageResponse;
import com.sideproject.withpt.application.chat.contoller.response.ReadMessageResponse;
import com.sideproject.withpt.application.chat.contoller.response.RoomListResponse;
import com.sideproject.withpt.application.chat.exception.ChatException;
import com.sideproject.withpt.application.chat.repository.ChatQueryRepository;
import com.sideproject.withpt.application.chat.repository.ChatRoomRepository;
import com.sideproject.withpt.application.chat.repository.MessageRepository;
import com.sideproject.withpt.application.chat.repository.ParticipantRepository;
import com.sideproject.withpt.application.member.service.MemberService;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.domain.chat.Message;
import com.sideproject.withpt.domain.chat.Participant;
import com.sideproject.withpt.domain.chat.Room;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.Arrays;
import java.util.List;
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
    private final ChatQueryRepository chatQueryRepository;
    private final ParticipantRepository participantRepository;
    private final MessageRepository messageRepository;

    private final TrainerService trainerService;
    private final MemberService memberService;

    @Transactional
    public CreateRoomResponse createRoom(Long loginId, Role loginRole, CreateRoomRequest request) {
        try {
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
        } catch (Exception e) {
            log.error("Error occurred during chat room creation", e);
            throw new ChatException(CHAT_ROOM_CREATION_ERROR);
        }
    }

    public RoomListResponse getRoomList(Long loginId, Role loginRole) {
        try {
            Trainer trainer = loginRole.equals(Role.TRAINER) ? trainerService.getTrainerById(loginId) : null;
            Member member = loginRole.equals(Role.MEMBER) ? memberService.getMemberById(loginId) : null;

            return new RoomListResponse(
                chatQueryRepository.findAllRoomInfo(trainer, member, loginRole),
                "채팅방 리스트 조회"
            );
        } catch (Exception e) {
            log.error("Error occurred during chat room creation", e);
            throw new ChatException(CHAT_LIST_REQUEST_ERROR);
        }
    }

    public List<MessageResponse> getChattingList(Long roomId, Long cursor) {
        try {
            return chatQueryRepository.findAllChattingList(roomId, cursor);
        }catch (Exception e) {
            throw new ChatException(CHAT_LIST_LOAD_ERROR_MESSAGE);
        }
    }

    @Transactional
    public MessageResponse saveMessage(MessageRequest messageRequest) {
        return chatRoomRepository.findById(messageRequest.getRoomId())
            .map(exisginRoom -> {
                    exisginRoom.updateLastChat(messageRequest.getMessage());
                    Message savedMessage = messageRepository.save(messageRequest.toEntity(exisginRoom));

                    participantRepository.findByRoomAndRole(exisginRoom, messageRequest.getReceiverRole())
                        .incrementNotReadChat();

                    participantRepository.findByRoomAndRole(exisginRoom, messageRequest.getSenderRole())
                        .updateLastChatAndNotReadChat(savedMessage.getId());

                    // TODO : 식단, 운동에 따른 로직 변경 or 추가
                    return MessageResponse.from(savedMessage, exisginRoom);
                }
            )
            .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND));
    }

    @Transactional
    public ReadMessageResponse readMessage(ReadMessageRequest request) {
        return chatRoomRepository.findById(request.getRoomId())
            .map(exisginRoom -> {
                    messageRepository.decrementNotRead(
                        exisginRoom,
                        request.getStartLastReadMessageId(),
                        request.getEndLastReadMessageId()
                    );

                    participantRepository.findByRoomAndRole(exisginRoom, request.getLoginUserRole())
                        .updateLastChatAndNotReadChat(request.getEndLastReadMessageId());

                    return new ReadMessageResponse(
                        exisginRoom.getId(),
                        request.getLastReadMessageIdRange()
                    );
                }
            )
            .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND));
    }

    private void saveParticipant(Trainer trainer, Member member, Role role, Room room) {
        String participantName = (role.equals(Role.TRAINER)) ? member.getName() : trainer.getName();
        participantRepository.save(Participant.createParticipant(trainer, member, role, room, participantName));
    }


}
