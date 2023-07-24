package scs.planus.domain.group.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupTag;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.group.repository.GroupTagRepository;
import scs.planus.domain.tag.dto.TagCreateRequestDto;
import scs.planus.domain.tag.entity.Tag;
import scs.planus.domain.tag.repository.TagRepository;
import scs.planus.support.ServiceTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ServiceTest
class GroupTagServiceTest {
    private static final String TAG_NAME = "테스트 태그 이름";
    private static final int TAG_COUNT = 5;

    private final GroupTagRepository groupTagRepository;
    private final TagRepository tagRepository;
    private final GroupRepository groupRepository;

    private final GroupTagService groupTagService;

    private Group group;

    @Autowired
    public GroupTagServiceTest(GroupTagRepository groupTagRepository,
                               TagRepository tagRepository,
                               GroupRepository groupRepository) {
        this.groupTagRepository = groupTagRepository;
        this.tagRepository = tagRepository;
        this.groupRepository = groupRepository;

        groupTagService = new GroupTagService(groupTagRepository);
    }

    @BeforeEach
    void init() {
        group = groupRepository.findById(1L).orElseThrow();

        List<Tag> tags = IntStream.range(0, TAG_COUNT)
                .mapToObj(i -> Tag.builder()
                        .name(TAG_NAME + i)
                        .build()
                )
                .collect(Collectors.toList());

        tagRepository.saveAll(tags);

        List<GroupTag> groupTags = IntStream.range(0, TAG_COUNT)
                .mapToObj(i -> GroupTag.builder()
                        .group(group)
                        .tag(tags.get(i))
                        .build()
                )
                .collect(Collectors.toList());

        groupTagRepository.saveAll(groupTags);
    }

    @DisplayName("수정하고자 하는 5개 태그인 List<TagCreateRequestDto> 중" +
            "3개가 기존의 GroupTag 와 일치 할때, " +
            "불필요한 태그 2개는 삭제되고," +
            "새로 추가할 태그 2개가 반환되어야 한다.")
    @Test
    void update() {
        // given
        List<TagCreateRequestDto> tagCreateRequestDtos = IntStream.range(2, TAG_COUNT + 2)
                .mapToObj(i -> TagCreateRequestDto.builder()
                        .name(TAG_NAME + i)
                        .build()
                )
                .collect(Collectors.toList());

        // when
        List<TagCreateRequestDto> updateTags = groupTagService.extractToBeUpdatedTags(group, tagCreateRequestDtos);
        List<GroupTag> findGroupTags = groupTagRepository.findAllByGroup(group);

        // then
        assertThat(updateTags).hasSize(2); // 0~4 가 존재하는데 2~6 을 추가했으므로, 5~6 만 추출되어야 한다.
        assertThat(findGroupTags).hasSize(3); // 추가 태그는 아직 저장안된 상태이고, 5개중 불필요한 태그 2개는 삭제되어있어야 한다.
    }
}