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
    public List<TagCreateRequestDto> update(Group group, List<TagCreateRequestDto> updateTags ) {
        List<GroupTag> groupTags = groupTagRepository.findAllByGroup( group );

        List<GroupTag> removeGroupTag = new ArrayList<>();
        List<TagCreateRequestDto> removeUpdateTag = new ArrayList<>();

        for (GroupTag gt : groupTags) {
            boolean removeFlag = true;
            for ( TagCreateRequestDto t : updateTags ) {
                if (gt.getTag().getName().equals(t.getName())) {
                    removeUpdateTag.add(t);
                    removeFlag = false;
                    break;
                }
            }
            if (removeFlag) removeGroupTag.add(gt);
        }

        group.getGroupTags().removeAll(removeGroupTag);

        // 존재하는 태그 제외하고, 추가해야할 태그만 리턴
        updateTags.removeAll(removeUpdateTag);

        return updateTags;
    }
}
