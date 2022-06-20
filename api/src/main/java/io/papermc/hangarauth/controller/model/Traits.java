package io.papermc.hangarauth.controller.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class Traits {

    private final Name name;
    private final String email;
    private final String github;
    private final String discord;
    private final String language;
    private final String username;
    private final String minecraft;

    @JsonCreator
    public Traits(final Name name, @NotNull final String email, final String github, final String discord, final String language, @NotNull final String username, final String minecraft) {
        this.name = name;
        this.email = email;
        this.github = StringUtils.trimToNull(github);
        this.discord = StringUtils.trimToNull(discord);
        this.language = StringUtils.trimToNull(language);
        this.username = username;
        this.minecraft = StringUtils.trimToNull(minecraft);
    }

    public Name getName() {
        return this.name;
    }

    public @NotNull String getEmail() {
        return this.email;
    }

    public String getGithub() {
        return this.github;
    }

    public String getDiscord() {
        return this.discord;
    }

    public String getLanguage() {
        return this.language;
    }

    public @NotNull String getUsername() {
        return this.username;
    }

    public String getMinecraft() {
        return this.minecraft;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Traits traits = (Traits) o;
        return Objects.equals(this.name, traits.name) && this.email.equals(traits.email) && Objects.equals(this.github, traits.github) && Objects.equals(this.discord, traits.discord) && Objects.equals(this.language, traits.language) && this.username.equals(traits.username) && Objects.equals(this.minecraft, traits.minecraft);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.email, this.github, this.discord, this.language, this.username, this.minecraft);
    }

    @Override
    public String toString() {
        return "Traits{" +
            "name=" + this.name +
            ", email='" + this.email + '\'' +
            ", github='" + this.github + '\'' +
            ", discord='" + this.discord + '\'' +
            ", language='" + this.language + '\'' +
            ", username='" + this.username + '\'' +
            ", minecraft='" + this.minecraft + '\'' +
            '}';
    }

    public static final class Name {

        private final String last;
        private final String first;

        public Name(final String last, final String first) {
            this.last = StringUtils.trimToNull(last);
            this.first = StringUtils.trimToNull(first);
        }

        public String getLast() {
            return this.last;
        }

        public String getFirst() {
            return this.first;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final Name name = (Name) o;
            return Objects.equals(this.last, name.last) && Objects.equals(this.first, name.first);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.last, this.first);
        }

        @Override
        public String toString() {
            return "Name{" +
                "last='" + this.last + '\'' +
                ", first='" + this.first + '\'' +
                '}';
        }
    }
}
