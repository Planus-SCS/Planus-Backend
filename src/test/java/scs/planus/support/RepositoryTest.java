package scs.planus.support;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import scs.planus.config.ExternalApiConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@DataJpaTest
@Import(ExternalApiConfig.class)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
public @interface RepositoryTest {
}
