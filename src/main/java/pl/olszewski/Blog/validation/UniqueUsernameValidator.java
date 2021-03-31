package pl.olszewski.Blog.validation;

import org.springframework.beans.factory.annotation.Autowired;
import pl.olszewski.Blog.service.Menagers.AuthorManager;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<ValidUniqueUsername, String> {

    @Autowired
    public AuthorManager authorManager;

    @Override
    public void initialize(ValidUniqueUsername arg) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !authorManager.existsByUsername(s);
    }
}
