package scs.planus.support;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.repository.GroupTodoCompletionRepository;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.domain.todo.repository.TodoRepository;
import scs.planus.global.config.QueryDslConfig;

@DataJpaTest
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
@Import(QueryDslConfig.class)
public abstract class RepositoryTest {

    protected TodoQueryRepository todoQueryRepository;

    @Autowired
    protected JPAQueryFactory queryFactory;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected GroupRepository groupRepository;

    @Autowired
    protected GroupMemberRepository groupMemberRepository;

    @Autowired
    protected TodoRepository todoRepository;

    @Autowired
    protected TodoCategoryRepository todoCategoryRepository;

    @Autowired
    protected GroupTodoCompletionRepository groupTodoCompletionRepository;

    @BeforeEach
    void initRepository() {
        todoQueryRepository = new TodoQueryRepository(queryFactory);
    }
}
