package com.sideproject.withpt.domain.record.exercise;

import com.sideproject.withpt.application.type.BodyPart;
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
public class BodyCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BodyPart name;

    private int depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private BodyCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<BodyCategory> children = new ArrayList<>();

    @Builder
    public BodyCategory(BodyPart name, BodyCategory parent, List<BodyCategory> children) {
        this.name = name;
        this.depth = parent != null ? parent.getDepth() + 1 : 1;
        this.parent = parent;
        if(children != null) {
            this.children = children;
            for (BodyCategory child : children) {
                child.setParent(this); // 자식의 부모를 설정
                child.increaseDepth();
            }
        }
    }

    private void setParent(BodyCategory parent) {
        this.parent = parent;
    }

    private void increaseDepth() {
        this.depth += 1;
    }
}