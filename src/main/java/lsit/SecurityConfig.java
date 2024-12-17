package lsit;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    private final GitLabService gitLabService;  // Inject GitLabService

    @Autowired
    public SecurityConfig(GitLabService gitLabService) {
        this.gitLabService = gitLabService;  // Inject GitLabService into the constructor
    }
    
        @SuppressWarnings("removal")
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                .csrf().disable()  // Disable CSRF protection if necessary (for simplicity)
                .oauth2Login(withDefaults())  // Enable OAuth2 login with default settings
                .authorizeHttpRequests(a -> a
                    // Restrict /products to users in the 'Sales' role
                    .requestMatchers("/products").hasAuthority("ROLE_Sales")
                    .requestMatchers(HttpMethod.POST, "/products").hasAuthority("ROLE_Sales")  // Restrict POST /products to 'Sales' group
                    .anyRequest().authenticated()  // Ensure all other requests require authentication
                );
    
            return http.build();
        }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            List<String> groups = gitLabService.getGitLabGroups(oauth2User);  // Fetch GitLab groups
            authentication.setAuthenticated(true);  // Mark user as authenticated
        };
    }
}
