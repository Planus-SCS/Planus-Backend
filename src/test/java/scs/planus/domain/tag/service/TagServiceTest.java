package scs.planus.domain.tag.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.tag.dto.TagCreateRequestDto;
import scs.planus.domain.tag.entity.Tag;
import scs.planus.domain.tag.repository.TagRepository;
import scs.planus.support.ServiceTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class TagServiceTest extends ServiceTest {
    private static final String TAG_NAME = "테스트 태그 이름";

    private final TagRepository tagRepository;

    private final TagService tagService;

    @Autowired
    public TagServiceTest(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
        tagService = new TagService(tagRepository);
    }

    @DisplayName("Tag 를 생성할 수 있다.")
    @Test
    void create() {
        // given
        TagCreateRequestDto tagCreateRequestDto = TagCreateRequestDto.builder()
                .name(TAG_NAME)
                .build();

        // when
        Tag createdTag = tagService.create(tagCreateRequestDto);

        // then
        assertThat(createdTag.getName()).isEqualTo(TAG_NAME);
    }

    @DisplayName("List<TagCreateRequestDto> 를 List<Tag> 로 변환할 수 있다.")
    @Test
    void transformToTag_Success() {
        // given
        List<TagCreateRequestDto> tagCreateRequestDtos = IntStream.range(0, 5)
                .mapToObj(i -> TagCreateRequestDto.builder()
                        .name(TAG_NAME + i)
                        .build())
                .collect(Collectors.toList());

        // when
        List<Tag> transformTags = tagService.transformToTag(tagCreateRequestDtos);

        // then
        assertThat(transformTags).hasSize(tagCreateRequestDtos.size());
    }

    @DisplayName("5개 중에 2개가 존재한다면 새로운 Tag 는 3개만 생성되어야 한다.")
    @Test
    void transformToTag_Success_Create_Only_3() {
        // given
        List<Tag> tags = IntStream.range(0, 2)
                .mapToObj(i -> Tag.builder()
                        .name(TAG_NAME + i)
                        .build())
                .collect(Collectors.toList());

        tagRepository.saveAll(tags);

        List<TagCreateRequestDto> tagCreateRequestDtos = IntStream.range(0, 5)
                .mapToObj(i -> TagCreateRequestDto.builder()
                        .name(TAG_NAME + i)
                        .build())
                .collect(Collectors.toList());

        // when
        List<Tag> transformTags = tagService.transformToTag(tagCreateRequestDtos);
        List<Tag> findAllTags = tagRepository.findAll();

        // then
        assertThat(transformTags).hasSize(tagCreateRequestDtos.size());
        assertThat(findAllTags).hasSize(tagCreateRequestDtos.size());
    }
}