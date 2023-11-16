package com.sideproject.withpt.application.pt.controller.request;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class RemovePtMembersRequest {

    List<Long> memberIds;

}
