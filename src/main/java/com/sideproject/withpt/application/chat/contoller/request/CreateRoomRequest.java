package com.sideproject.withpt.application.chat.contoller.request;

import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.INVALID_REQUESTED_CHAT_IDENTIFIER;
import static com.sideproject.withpt.application.chat.exception.ChatErrorCode.INVALID_USER_IDENTIFIER;

import com.sideproject.withpt.application.chat.exception.ChatException;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {

    @NotNull(message = "채팅하려는 유저 ID는 필수입니다.")
    private Long id;

    @ValidEnum(regexp = "MEMBER|TRAINER", enumClass = Role.class)
    private Role role;

    @NotBlank(message = "채팅하려는 유저의 이름을 입력해주세요")
    private String roomName;

    @NotBlank
    @Pattern(regexp = "TRAINER_\\d+&MEMBER_\\d+", message = "식별자가 옳바르지 않습니다. TRAINER 먼저 생성해주세요")
    private String identifier;

    private Map<Role, Long> parseIdentifier() {
        return Arrays.stream(identifier.split("&"))
            .map(part -> part.split("_"))
            .collect(Collectors.toMap(keyValue -> Role.valueOf(keyValue[0]), keyValue -> Long.parseLong(keyValue[1])));
    }

    public void validationIdentifier(Long loginId, Role loginRole) {
        Map<Role, Long> parseIdentifier = parseIdentifier();

        if (!Objects.equals(parseIdentifier.get(loginRole), loginId)) {
            throw new ChatException(INVALID_USER_IDENTIFIER);
        }

        if (!Objects.equals(parseIdentifier.get(role), id)) {
            throw new ChatException(INVALID_REQUESTED_CHAT_IDENTIFIER);
        }
    }
}
