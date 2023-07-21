package scs.planus.domain.group.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.domain.tag.entity.Tag;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class GroupTagTest {
    private Group group;
    private List<Tag> tags;

    @BeforeEach
    void init() {
        group = Group.builder()
                .build();

        tags = IntStream.range(0, 5)
                .mapToObj(i -> Tag.builder()
                        .build()
                )
                .collect(Collectors.toList());
    }

    @DisplayName("List<Tag> 로 GroupTag 를 생성할 수 있다.")
    @Test
    void create() {
        // given
        // when
        List<GroupTag> groupTags = GroupTag.create(group, tags);

        // then
        assertThat(groupTags).hasSize(tags.size());
        assertThat(groupTags.get(0).getGroup()).isEqualTo(group);
    }
}