package com.sideproject.withpt.common.type;

// 메시지  타입 : 입장, 채팅
// 메시지 타입에 따라서 동작하는 구조가 달라진다.
// 입장과 퇴장 ENTER 과 LEAVE 의 경우 입장/퇴장 이벤트 처리가 실행되고,
// TALK 는 말 그대로 내용이 해당 채팅방을 SUB 하고 있는 모든 클라이언트에게 전달된다.
public enum MessageType {
    ENTER("입장"),
    LEAVE("퇴장"),
    TALK("채팅"),
    DIET("식단"),
    LESSON("수업");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
