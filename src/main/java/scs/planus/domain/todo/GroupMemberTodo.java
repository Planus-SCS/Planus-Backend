package scs.planus.domain.todo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.GroupMember;

import javax.persistence.DiscriminatorValue;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@DiscriminatorValue("GMT")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMemberTodo extends Todo{

    private boolean completion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_id")
    private GroupMember groupMember;
}
