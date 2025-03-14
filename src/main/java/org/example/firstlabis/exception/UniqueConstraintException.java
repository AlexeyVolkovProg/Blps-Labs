package org.example.firstlabis.exception;


import jakarta.validation.ValidationException;

public class UniqueConstraintException extends ValidationException {
    public UniqueConstraintException(Class<?> entity, String field, String value){
        super(String.format(
                "Value '%s' of field '%s' for entity '%s' does not match unique constraint",
                value, field, entity.getSimpleName()
        ));
    }
}
