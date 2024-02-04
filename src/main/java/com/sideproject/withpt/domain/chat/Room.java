package com.sideproject.withpt.domain.chat;

import com.sideproject.withpt.application.type.RoomType;
import com.sideproject.withpt.domain.BaseEntity;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identifier;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    private String lastChat;

    public static Room createRoom(String identifier) {
        return Room.builder()
            .identifier(identifier)
            .type(RoomType.INDIVIDUAL)
            .lastChat("")
            .build();
    }
}
