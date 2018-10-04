package io.pivotal.pal.tracker.backlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String endpoint;
    private final Map<Long, ProjectInfo> projectsCache = new ConcurrentHashMap<>();;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo project = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        this.projectsCache.put(projectId, project);
        return project;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        logger.info("Getting project with id {} from cache", projectId);
        return projectsCache.get(projectId);
    }
}
