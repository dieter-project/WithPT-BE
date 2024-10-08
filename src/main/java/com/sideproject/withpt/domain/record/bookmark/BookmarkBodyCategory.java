package com.sideproject.withpt.domain.record.bookmark;

import com.sideproject.withpt.common.type.BodyPart;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkBodyCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BodyPart name;

    private int depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private BookmarkBodyCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<BookmarkBodyCategory> children = new ArrayList<>();

    @Builder
    public BookmarkBodyCategory(BodyPart name, BookmarkBodyCategory parent, List<BookmarkBodyCategory> children) {
        this.name = name;
        this.depth = parent != null ? parent.getDepth() + 1 : 1;
        this.parent = parent;
        if (children != null) {
            this.children = children;
            for (BookmarkBodyCategory child : children) {
                child.setParent(this); // 자식의 부모를 설정
                child.increaseDepth();
            }
        }
    }

    private void setParent(BookmarkBodyCategory parent) {
        this.parent = parent;
    }

    private void increaseDepth() {
        this.depth += 1;
    }
}
