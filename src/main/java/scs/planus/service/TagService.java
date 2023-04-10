package scs.planus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.Tag;
import scs.planus.dto.tag.TagCreateRequestDto;
import scs.planus.repository.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public Tag create( TagCreateRequestDto tagDto ) {
        Tag tag = Tag.builder()
                .name( tagDto.getName() )
                .build();

        return tagRepository.save( tag );
    }

    public List<Tag> transformToTag( List<TagCreateRequestDto> tagDtos ) {

        return tagDtos.stream()
                .map( tag -> tagRepository.findByName( tag.getName() )
                        .orElseGet( () -> this.create( tag ) ) )
                .collect( Collectors.toList() );
    }
}