package me.itzg.mcfresh.profiles;

public class ProfileAccessException extends RuntimeException {

    public ProfileAccessException(String message) {
        super(message);
    }

    public ProfileAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
