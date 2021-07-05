package ua.training.validation;

import ua.training.dto.BookTranslateContainerDto;
import ua.training.dto.BookTranslateDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BookWithTranslateValidator {

    public boolean validate(BookTranslateContainerDto containerDto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        for (BookTranslateDto bookTranslateDto : containerDto.getDtoList()) {
            Set<ConstraintViolation<BookTranslateDto>> violations1 = validator.validate(bookTranslateDto);
            if (!violations1.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
