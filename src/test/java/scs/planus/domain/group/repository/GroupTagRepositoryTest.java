package scs.planus.domain.group.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupTag;
import scs.planus.domain.tag.entity.Tag;
import scs.planus.domain.tag.repository.TagRepository;
import scs.planus.support.RepositoryTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class GroupTagRepositoryTest {
    private static final String TAG_NAME = "테스트 태그 이름";
    private static final int TAG_COUNT = 10;
    private static final int GROUP_TAG_COUNT = 5;

    private final GroupRepository groupRepository;
    private final GroupTagRepository groupTagRepository;
    private final TagRepository tagRepository;

    private Group group;
    private List<Tag> tags;

    @Autowired
    public GroupTagRepositoryTest(GroupRepository groupRepository,
                                  GroupTagRepository groupTagRepository,
                                  TagRepository tagRepository) {
        this.groupRepository = groupRepository;
        this.groupTagRepository = groupTagRepository;
        this.tagRepository = tagRepository;
    }

    @BeforeEach
    void init() {
        group = Group.builder()
                .build();

        groupRepository.save(group);

        tags = IntStream.range(0, TAG_COUNT)
                .mapToObj(i -> Tag.builder()
                        .name(TAG_NAME + i)
                        .build()
                )
                .collect(Collectors.toList());

        tagRepository.saveAll(tags);

        List<GroupTag> groupTags = IntStream.range(0, GROUP_TAG_COUNT)
                .mapToObj(i -> GroupTag.builder()
                        .group(group)
                        .tag(tags.get(i))
                        .build()
                )
                .collect(Collectors.toList());

        groupTagRepository.saveAll(groupTags);
    }

    @DisplayName("Group 으로 GroupTag 들이 Tag 와 함께 조회돼야 한다.")
    @Test
    void findAllByGroup() {
        // given
        // when
        List<GroupTag> findGroupTags = groupTagRepository.findAllByGroup(group);

        List<Tag> findTags = findGroupTags.stream()
                .map(GroupTag::getTag)
                .collect(Collectors.toList());

        // then
        assertThat(findGroupTags).hasSize(GROUP_TAG_COUNT);
        assertThat(findTags).hasSize(GROUP_TAG_COUNT);
    }

    @DisplayName("List<Group>으로 각 GroupTag 들이 Tag 와 함께 조회돼야 한다.")
    @Test
    void findAllTagInGroups() {
        // given
        Group group2 = Group.builder()
                .build();

        groupRepository.save(group2);

        List<GroupTag> group2Tags = IntStream.range(0, 3)
                .mapToObj(i -> GroupTag.builder()
                        .group(group2)
                        .tag(tags.get(i))
                        .build()
                )
                .collect(Collectors.toList());

        groupTagRepository.saveAll(group2Tags);

        List<Group> groups = List.of(group, group2);

        // when
        List<GroupTag> findGroupTags = groupTagRepository.findAllTagInGroups(groups);

        // then
        assertThat(findGroupTags).hasSize(GROUP_TAG_COUNT + 3);
    }
}