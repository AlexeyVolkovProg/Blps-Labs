package org.example.firstlabis.model.security;

import java.util.Collections;
import java.util.Set;

import lombok.Getter;

@Getter
public enum Role {
    UNAUTHENTICATED(Collections.emptySet()),
    USER(Set.of(
            Privilege.VIEW_VIDEO,
            Privilege.CREATE_COMPLAINT,
            Privilege.CREATE_VIDEO
    )),
    UNAPPROVED_ADMIN(Set.of(
            Privilege.VIEW_VIDEO,
            Privilege.CREATE_COMPLAINT,
            Privilege.CREATE_VIDEO
    )),
    ADMIN(Set.of(
            Privilege.VIEW_VIDEO,
            Privilege.CREATE_COMPLAINT,
            Privilege.CREATE_VIDEO,
            Privilege.REVIEW_VIDEO
    ));

    private final Set<Privilege> privileges;

    Role(Set<Privilege> privileges) {
        this.privileges = privileges;
    }
}
