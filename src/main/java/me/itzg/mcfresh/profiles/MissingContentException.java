package me.itzg.mcfresh.profiles;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MissingContentException extends RuntimeException {

    public MissingContentException(String message) {
        super(message);
    }
}
