package com.sideproject.withpt.application.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.sideproject.withpt.application.member.dto.response.MemberSearchResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class MemberQueryServiceTest {

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberQueryService memberQueryService;

    @Test
    public void searchMembers() {
        // given
        List<MemberSearchResponse> fakeSearchResults = new ArrayList<>();
        fakeSearchResults.add(new MemberSearchResponse(1L, "test1", "nickName1", null, null, null));
        fakeSearchResults.add(new MemberSearchResponse(2L, "test2", "nickName2", null, null, null));

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "name");

        given(memberRepository.findBySearchOption(pageable, "test1", "nickName1"))
            .willReturn(new PageImpl<>(fakeSearchResults, pageable, fakeSearchResults.size()));

        //when
        Page<MemberSearchResponse> searchResults = memberQueryService.searchMembers(pageable, "test1", "nickName1");

        //then
        assertThat(searchResults.getTotalElements()).isEqualTo(2);
        assertThat(searchResults.getContent().get(0).getName()).isEqualTo("test1");
        assertThat(searchResults.getContent().get(0).getNickname()).isEqualTo("nickName1");
    }
}