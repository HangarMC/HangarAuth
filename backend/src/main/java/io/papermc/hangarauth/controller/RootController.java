package io.papermc.hangarauth.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {

    private final Optional<GitProperties> gitProperties;

    @Autowired
    public RootController(final Optional<GitProperties> gitProperties) {
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
            "version", this.get("build.version", -1),
            "committer", this.get("commit.user.name", "dummy"),
            "time", this.get("commit.time", -1),
            "commit", this.gitProperties.map(GitProperties::getCommitId).orElse("0"),
            "commitShort", this.gitProperties.map(GitProperties::getShortCommitId).orElse("0"),
            "message", this.get("commit.message.short", "dummy"),
            "tag", this.gitProperties.map(gp -> gp.get("tags")).or(() -> this.gitProperties.map(gp -> gp.get("closest.tag.name"))).orElse("dummy"),
            "behind", this.get("closest.tag.commit.count", 0)
        );
    }

    private String get(final String propName, final Object fallback) {
        return this.gitProperties.map(gp -> gp.get(propName)).orElse(fallback.toString());
    }
}
