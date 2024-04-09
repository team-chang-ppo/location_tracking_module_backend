package org.changppo.monitoring.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidFormattedEventException extends RuntimeException{
    private String rawEvent;
}
