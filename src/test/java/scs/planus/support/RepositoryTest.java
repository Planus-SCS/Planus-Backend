package scs.planus.support;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import scs.planus.config.ExternalApiConfig;

@DataJpaTest
@Import(ExternalApiConfig.class)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
public abstract class RepositoryTest {
}
