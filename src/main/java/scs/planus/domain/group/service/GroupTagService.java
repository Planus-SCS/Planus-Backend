package scs.planus.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupTag;
import scs.planus.domain.group.repository.GroupTagRepository;
import scs.planus.domain.tag.dto.TagCreateRequestDto;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupTagService {
    private final GroupTagRepository groupTagRepository;

    // TODO : 리펙토링 필요할 듯
    @Transactional
    public List<TagCreateRequestDto> extractToBeUpdatedTags(Group group, List<TagCreateRequestDto> tagDtos) {
        
        List<GroupTag> groupTags = groupTagRepository.findAllByGroup(group);
        List<TagCreateRequestDto> updatedTagsDto = new ArrayList<>(tagDtos);

        for (GroupTag gt : groupTags) {
            boolean isSameGroupTag = false;
            for (TagCreateRequestDto t : tagDtos) {
                if (gt.getTag().getName().equals(t.getName())) {
                    updatedTagsDto.remove(t);
                    isSameGroupTag = true;
                    break;
                }
            }
            if (!isSameGroupTag) {
                group.removeGroupTag(gt);
            }
        }

        return updatedTagsDto;
    }
}
