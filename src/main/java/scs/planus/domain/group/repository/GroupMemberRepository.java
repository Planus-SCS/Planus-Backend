package scs.planus.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("select gm " +
            "from GroupMember gm " +
            "join fetch gm.group " +
            "join fetch gm.member " +
            "where gm.group= :group " +
            "and gm.leader= true")
    Optional<GroupMember> findWithGroupAndLeaderByGroup(@Param("group") Group group );

    @Query("select gm from GroupMember gm " +
            "join fetch gm.group g " +
            "join fetch gm.member m " +
            "where m.id = :memberId and g.id = :groupId and gm.status = 'ACTIVE'")
    Optional<GroupMember> findByMemberIdAndGroupId(@Param("memberId") Long memberId,
                                                   @Param("groupId") Long groupId);

    @Query("select gm from GroupMember gm " +
            "join fetch gm.group g " +
            "join fetch gm.member m " +
            "where m.id = :memberId and g.id = :groupId and gm.status = 'INACTIVE'")
    Optional<GroupMember> findByMemberIdAndGroupIdAndInactive(@Param("memberId") Long memberId,
                                                              @Param("groupId") Long groupId);

    @Query("select gm " +
            "from GroupMember gm " +
            "join fetch gm.group g " +
            "join fetch gm.member m " +
            "where g = :group and gm.status= 'ACTIVE' " +
            "order by gm.leader DESC, gm.onlineStatus DESC")
    List<GroupMember> findAllWithMemberByGroupAndStatus(@Param("group") Group group );

    @Query("select gm from GroupMember gm " +
            "join fetch gm.group g " +
            "join fetch gm.member m " +
            "where g.status = 'ACTIVE' and gm.status = 'ACTIVE' and m.id = :memberId")
    List<GroupMember> findAllByActiveGroupAndMemberId(@Param("memberId") Long memberId);

    @Query("select gm from GroupMember gm " +
            "join fetch gm.group g " +
            "join fetch gm.member m " +
            "where g in :groups")
    List<GroupMember> findAllGroupMemberInGroups(@Param("groups") List<Group> groups);

    @Query("select gm " +
            "from GroupMember gm " +
            "join fetch gm.group " +
            "join fetch gm.member " +
            "where gm.member= :member " +
            "and gm.leader= true")
    List<GroupMember> findWithGroupByLeaderMember(@Param("member") Member member);

}
