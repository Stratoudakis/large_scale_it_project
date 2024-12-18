package lsit;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Configuration
@EnableWebSecurity
public class Config {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .oauth2Login(oauth -> oauth
                .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService()))
                .successHandler(authenticationSuccessHandler()) // Add success handler here
            )
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return userRequest -> {
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            Map<String, Object> attributes = oAuth2User.getAttributes();
            List<String> groups = (List<String>) attributes.get("https://gitlab.org/claims/groups");

            Set<GrantedAuthority> authorities = new HashSet<>(oAuth2User.getAuthorities());
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            if (groups != null) {
                if (groups.contains("lsit-ken3239/roles/ClothingStore/Customer")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
                }
                if (groups.contains("lsit-ken3239/roles/ClothingStore/Sales")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_SALES"));
                }
                if (groups.contains("lsit-ken3239/roles/ClothingStore/Credit")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_CREDIT"));
                }
                if (groups.contains("lsit-ken3239/roles/ClothingStore/WarehouseManager")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_WAREHOUSE"));
                }
            }

            return new DefaultOAuth2User(authorities, attributes, "name");
        };
    }

    @Bean
        public AuthenticationSuccessHandler authenticationSuccessHandler() {
    return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
        var userAttributes = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttributes();
        var groups = (List<String>) ((OAuth2User) authentication.getPrincipal()).getAttribute("https://gitlab.org/claims/groups/owner");


        System.out.println("User logged in: ");
        System.out.println("Name: " + userAttributes.get("name"));
        System.out.println("Email: " + userAttributes.get("email"));
        System.out.println("Groups: " + groups);

        // Redirect to the home page
        response.sendRedirect("/");
    };
}
}
