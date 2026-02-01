package org.dubna.user;

import java.util.Optional;

public class UserContextHolder {

    private static final ThreadLocal<UserEntity> THREAD_LOCAL = new ThreadLocal<>();

    public static Optional<UserEntity> getOptional() {
        return Optional.ofNullable(THREAD_LOCAL.get());
    }

    public static UserEntity getOrThrow() {
        return getOptional().orElseThrow(() -> new RuntimeException("UserContextHolder does not contain any UserEntity"));
    }

    public static void set(UserEntity user) {
        if (user == null) {
            throw new NullPointerException("User cannot be null");
        }
        THREAD_LOCAL.set(user);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

}
