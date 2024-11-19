package com.sideproject.withpt.application.chat.service;

import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_LIST_REQUEST_ERROR;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_ROOM_ALREADY_EXISTS;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_ROOM_CREATION_ERROR;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.CHAT_ROOM_NOT_FOUND;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.INVALID_REQUESTED_CHAT_IDENTIFIER;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.PARTICIPANT_NOT_FOUND;

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
import com.sideproject.withpt.application.chat.service.response.RoomInfoResponse;
import com.sideproject.withpt.application.chat.service.response.RoomListResponse;
import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.lesson.exception.LessonException;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.member.service.MemberService;
import com.sideproject.withpt.application.record.diet.exception.DietException;
import com.sideproject.withpt.application.record.diet.repository.DietRepository;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.application.user.UserRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.type.MessageType;
import com.sideproject.withpt.domain.chat.Message;
import com.sideproject.withpt.domain.chat.Participant;
import com.sideproject.withpt.domain.chat.Room;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.record.diet.Diets;
import com.sideproject.withpt.domain.user.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
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

    private final LessonRepository lessonRepository;
    private final DietRepository dietRepository;
    private final ImageUploader imageUploader;

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
    public MessageResponse saveMessage(MessageDto request, LocalDateTime sentAt) {
        User sender = userRepository.findById(request.getSender())
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        User receiver = userRepository.findById(request.getReceiver())
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Room room = chatRoomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND));
        Message savedMessage = saveMessageByType(request, sentAt, sender, receiver, room);
        updateParticipantStates(room, sender, receiver, savedMessage);
        return MessageResponse.from(savedMessage, room);
    }

    @Transactional
    public ReadMessageResponse readMessage(ReadMessageRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        Room room = chatRoomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND));

        messageRepository.decrementNotRead(
            room,
            request.getStartLastReadMessageId(),
            request.getEndLastReadMessageId()
        );

        participantRepository.findByRoomAndUser(room, user)
            .ifPresent(participant ->
                participant.updateLastChatAndNotReadChat(request.getEndLastReadMessageId()));

        return new ReadMessageResponse(
            room.getId(),
            request.getLastReadMessageIdRange()
        );

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

    private Message saveMessageByType(MessageDto request, LocalDateTime sentAt, User sender, User receiver, Room room) {

        MessageType messageType = request.getMessageType();

        switch (messageType) {
            case DIET:
                Diets diets = dietRepository.findById(request.getRelatedEntityId())
                    .orElseThrow(() -> DietException.DIET_NOT_EXIST);
                return saveMessage(request.toEntity(room, sender, receiver, sentAt, diets, null));
            case LESSON:
                Lesson lesson = lessonRepository.findById(request.getRelatedEntityId())
                    .orElseThrow(() -> LessonException.LESSON_NOT_FOUND);
                return saveMessage(request.toEntity(room, sender, receiver, sentAt, null, lesson));
            case IMAGE:
            case TALK:
                return saveMessage(request.toEntity(room, sender, receiver, sentAt, null, null));
            default:
                throw new IllegalArgumentException("Invalid message type: " + messageType);
        }
    }

    private void updateParticipantStates(Room room, User sender, User receiver, Message savedMessage) {
        participantRepository.findByRoomAndUser(room, receiver)
            .ifPresent(Participant::incrementUnreadMessages);

        participantRepository.findByRoomAndUser(room, sender)
            .ifPresent(participant -> participant.updateLastChatAndNotReadChat(savedMessage.getId()));

        room.updateLastChat(savedMessage.getMessage());
    }

    private Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
}
