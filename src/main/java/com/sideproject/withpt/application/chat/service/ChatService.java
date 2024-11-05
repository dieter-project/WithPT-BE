package com.sideproject.withpt.application.chat.service;

import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_LIST_REQUEST_ERROR;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_ROOM_ALREADY_EXISTS;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_ROOM_CREATION_ERROR;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.INVALID_REQUESTED_CHAT_IDENTIFIER;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.PARTICIPANT_NOT_FOUND;

import com.sideproject.withpt.application.chat.contoller.request.CreateRoomRequest;
import com.sideproject.withpt.application.chat.contoller.request.MessageRequest;
import com.sideproject.withpt.application.chat.contoller.request.ReadMessageRequest;
import com.sideproject.withpt.application.chat.exception.ChatException;
import com.sideproject.withpt.application.chat.repository.message.MessageRepository;
import com.sideproject.withpt.application.chat.repository.participant.ParticipantRepository;
import com.sideproject.withpt.application.chat.repository.room.ChatRoomRepository;
import com.sideproject.withpt.application.chat.service.response.CreateRoomResponse;
import com.sideproject.withpt.application.chat.service.response.MessageResponse;
import com.sideproject.withpt.application.chat.service.response.ReadMessageResponse;
import com.sideproject.withpt.application.chat.service.response.RoomInfoResponse;
import com.sideproject.withpt.application.chat.service.response.RoomListResponse;
import com.sideproject.withpt.application.member.service.MemberService;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.application.user.UserRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.chat.Participant;
import com.sideproject.withpt.domain.chat.Room;
import com.sideproject.withpt.domain.user.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private final ParticipantRepository participantRepository;
    private final MessageRepository messageRepository;

    private final TrainerService trainerService;
    private final MemberService memberService;
    private final UserRepository userRepository;

    @Transactional
    public CreateRoomResponse createRoom(Long initiatorId, CreateRoomRequest request) {

        User initiator = userRepository.findById(initiatorId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        User partnerUser = userRepository.findById(request.getId())
            .orElseThrow(() -> new ChatException(INVALID_REQUESTED_CHAT_IDENTIFIER));

        String identifier = generateIdentifierBySHA256(initiator, partnerUser);

        try {
            return chatRoomRepository.findByIdentifier(identifier)
                .map(existingRoom ->
                    {
                        Participant participant = participantRepository.findByRoomAndUser(existingRoom, initiator)
                            .orElseThrow(() -> new ChatException(PARTICIPANT_NOT_FOUND));

                        return new CreateRoomResponse(
                            RoomInfoResponse.from(existingRoom, participant, partnerUser),
                            CHAT_ROOM_ALREADY_EXISTS.getMessage()
                        );
                    }
                )
                .orElseGet(() -> {

                    Room room = chatRoomRepository.saveAndFlush(Room.create(identifier));

                    participantRepository.save(Participant.create(partnerUser, room, initiator.getName()));
                    Participant savedParticipant = participantRepository.save(Participant.create(initiator, room, partnerUser.getName()));

                    return new CreateRoomResponse(
                        RoomInfoResponse.from(room, savedParticipant, partnerUser),
                        "채팅방이 생성되었습니다"
                    );
                });
        } catch (Exception e) {
            log.error("Error occurred during chat room creation", e);
            throw new ChatException(CHAT_ROOM_CREATION_ERROR);
        }
    }

    public RoomListResponse getRoomList(Long loginId) {
        try {

            User user = userRepository.findById(loginId)
                .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

            return new RoomListResponse(
                chatRoomRepository.findAllRoomInfoBy(user),
                "채팅방 리스트 조회"
            );
        } catch (Exception e) {
            log.error("Error occurred during chat room creation", e);
            throw new ChatException(CHAT_LIST_REQUEST_ERROR);
        }
    }

    public List<MessageResponse> getChattingList(Long roomId, Long cursor) {
//        try {
//            return chatRoomQueryRepository.findAllChattingList(roomId, cursor);
//        } catch (Exception e) {
//            throw new ChatException(CHAT_LIST_LOAD_ERROR_MESSAGE);
//        }
        return null;
    }

    @Transactional
    public MessageResponse saveMessage(MessageRequest messageRequest) {
//        return chatRoomRepository.findById(messageRequest.getRoomId())
//            .map(exisginRoom -> {
//                    exisginRoom.updateLastChat(messageRequest.getMessage());
//                    Message savedMessage = messageRepository.save(messageRequest.toEntity(exisginRoom));
//
//                    participantRepository.findByRoomAndRole(exisginRoom, messageRequest.getReceiverRole())
//                        .incrementUnreadMessages();
//
//                    participantRepository.findByRoomAndRole(exisginRoom, messageRequest.getSenderRole())
//                        .updateLastChatAndNotReadChat(savedMessage.getId());
//
//                    // TODO : 식단, 운동에 따른 로직 변경 or 추가
//                    return MessageResponse.from(savedMessage, exisginRoom);
//                }
//            )
//            .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND));
        return null;
    }

    @Transactional
    public ReadMessageResponse readMessage(ReadMessageRequest request) {
//        return chatRoomRepository.findById(request.getRoomId())
//            .map(exisginRoom -> {
//                    messageRepository.decrementNotRead(
//                        exisginRoom,
//                        request.getStartLastReadMessageId(),
//                        request.getEndLastReadMessageId()
//                    );
//
//                    participantRepository.findByRoomAndRole(exisginRoom, request.getLoginUserRole())
//                        .updateLastChatAndNotReadChat(request.getEndLastReadMessageId());
//
//                    return new ReadMessageResponse(
//                        exisginRoom.getId(),
//                        request.getLastReadMessageIdRange()
//                    );
//                }
//            )
//            .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND));
        return null;
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
