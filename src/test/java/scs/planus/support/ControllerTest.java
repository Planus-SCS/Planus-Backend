package scs.planus.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import scs.planus.global.auth.service.JwtProvider;
import scs.planus.global.auth.service.PrincipalDetailsService;

@AutoConfigureDataJpa
@Import(PrincipalDetailsService.class)
@WithUserDetails(value = "planus@planus")
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private JwtProvider jwtProvider;
}
