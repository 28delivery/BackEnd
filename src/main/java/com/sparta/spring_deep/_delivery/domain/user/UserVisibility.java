package com.sparta.spring_deep._delivery.domain.user;

public enum UserVisibility {
    PUBLIC(Visibility.PUBLIC),
    PRIVATE(Visibility.PRIVATE);

    private final String visibility;

    UserVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }

    public static class Visibility {
        public static final String PUBLIC = "public";
        public static final String PRIVATE = "private";
    }
}
