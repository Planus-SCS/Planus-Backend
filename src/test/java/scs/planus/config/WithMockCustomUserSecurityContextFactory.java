//package scs.planus.config;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.test.context.support.WithSecurityContextFactory;
//
//import java.util.Collections;
//
//public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
//
//    @Override
//    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
//        SecurityContext context = SecurityContextHolder.createEmptyContext();
//        Authentication authentication =
//                new UsernamePasswordAuthenticationToken(customUser.name(), "",
//                        Collections.singleton(new SimpleGrantedAuthority(customUser.role())));
//        context.setAuthentication(authentication);
//        return context;
//    }
//}
