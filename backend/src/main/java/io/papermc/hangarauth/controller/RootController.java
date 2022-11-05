package io.papermc.hangarauth.controller;

import org.springframework.boot.info.GitProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class RootController {

    private final GitProperties gitProperties;

    public RootController(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @GetMapping
    public List<Integer> get() {
        return List.of(1, 3, 5);
    }

    @GetMapping("/favicon.ico")
    public void getFavicon() {
        //
    }

    @ResponseBody
    @GetMapping("/version-info")
    public Map<String, String> info() {
        return Map.of(
                "version", gitProperties.get("build.version"),
                "committer", gitProperties.get("commit.user.name"),
                "time", gitProperties.get("commit.time"),
                "commit", gitProperties.getCommitId(),
                "commitShort", gitProperties.getShortCommitId(),
                "message", gitProperties.get("commit.message.short"),
                "tag", Optional.of(gitProperties.get("tags")).orElse(gitProperties.get("closest.tag.name")),
                "behind", gitProperties.get("closest.tag.commit.count")
        );
    }
}
