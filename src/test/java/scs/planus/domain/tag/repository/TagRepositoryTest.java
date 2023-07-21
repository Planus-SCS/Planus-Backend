package scs.planus.domain.tag.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.tag.entity.Tag;
import scs.planus.support.RepositoryTest;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class TagRepositoryTest {

    private static final String TAG_NAME = "테스트 태그 이름";
    private final TagRepository tagRepository;

    @Autowired
    public TagRepositoryTest(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @DisplayName("Name 으로 Tag 를 조회할 수 있어야 한다.")
    @Test
    void findByName_Exist() {
        // given
        Tag tag = Tag.builder()
                .name(TAG_NAME)
                .build();

        tagRepository.save(tag);

        // when
        Tag findTag = tagRepository.findByName(TAG_NAME).orElse(null);

        // then
        assertThat(findTag).isNotNull();
        assertThat(findTag.getName()).isEqualTo(TAG_NAME);
    }
}