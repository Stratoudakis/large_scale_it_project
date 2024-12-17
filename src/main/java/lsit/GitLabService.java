package lsit;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GitLabService {

    private static final String GITLAB_API_URL = "https://gitlab.com/api/v4/user";  // GitLab API URL

    public List<String> getGitLabGroups(OAuth2User user) {
        String accessToken = (String) user.getAttributes().get("access_token");  // Get access token from OAuth2User
        RestTemplate restTemplate = new RestTemplate();
        String url = GITLAB_API_URL + "/groups?access_token=" + accessToken;  // GitLab API to get groups
        
        // Fetch groups from GitLab
        String[] groups = restTemplate.getForObject(url, String[].class);
        
        return List.of(groups);  // Convert the response to a list of groups
    }
}
