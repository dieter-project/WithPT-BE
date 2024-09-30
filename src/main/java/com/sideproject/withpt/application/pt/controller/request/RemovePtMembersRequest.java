package com.sideproject.withpt.application.pt.controller.request;

import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
public class RemovePtMembersRequest {

    List<Long> ptIds;

}
