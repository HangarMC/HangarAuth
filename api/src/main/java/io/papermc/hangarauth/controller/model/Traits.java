package io.papermc.hangarauth.controller.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Traits {

    private final Name name;
    private final String email;
    private final String github;
    private final String discord;
    private final String language;
    private final String username;
    private final String minecraft;

    @JsonCreator
    public Traits(Name name, @NotNull String email, String github, String discord, String language, @NotNull String username, String minecraft) {
        this.name = name;
        this.email = email;
        this.github = StringUtils.trimToNull(github);
        this.discord = StringUtils.trimToNull(discord);
        this.language = StringUtils.trimToNull(language);
        this.username = username;
        this.minecraft = StringUtils.trimToNull(minecraft);
    }

    public Name getName() {
        return name;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public String getGithub() {
        return github;
    }

    public String getDiscord() {
        return discord;
    }

    public String getLanguage() {
        return language;
    }

    public @NotNull String getUsername() {
        return username;
    }

    public String getMinecraft() {
        return minecraft;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Traits traits = (Traits) o;
        return Objects.equals(name, traits.name) && email.equals(traits.email) && Objects.equals(github, traits.github) && Objects.equals(discord, traits.discord) && Objects.equals(language, traits.language) && username.equals(traits.username) && Objects.equals(minecraft, traits.minecraft);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, github, discord, language, username, minecraft);
    }

    @Override
    public String toString() {
        return "Traits{" +
            "name=" + name +
            ", email='" + email + '\'' +
            ", github='" + github + '\'' +
            ", discord='" + discord + '\'' +
            ", language='" + language + '\'' +
            ", username='" + username + '\'' +
            ", minecraft='" + minecraft + '\'' +
            '}';
    }

    public static final class Name {

        private final String last;
        private final String first;

        public Name(String last, String first) {
            this.last = StringUtils.trimToNull(last);
            this.first = StringUtils.trimToNull(first);
        }

        public String getLast() {
            return last;
        }

        public String getFirst() {
            return first;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Name name = (Name) o;
            return Objects.equals(last, name.last) && Objects.equals(first, name.first);
        }

        @Override
        public int hashCode() {
            return Objects.hash(last, first);
        }

        @Override
        public String toString() {
            return "Name{" +
                "last='" + last + '\'' +
                ", first='" + first + '\'' +
                '}';
        }
    }
}
