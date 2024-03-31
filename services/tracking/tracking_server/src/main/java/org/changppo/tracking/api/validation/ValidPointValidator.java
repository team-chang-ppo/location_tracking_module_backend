package org.changppo.tracking.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;

@Slf4j
public class ValidPointValidator implements ConstraintValidator<ValidPoint, Point> {
    @Override
    public void initialize(ValidPoint constraintAnnotation) {}

    @Override
    public boolean isValid(Point point, ConstraintValidatorContext context) {
        return point != null && isValidLatitude(point.getX()) && isValidLongitude(point.getY());
    }

    private boolean isValidLatitude(double latitude) {
        return latitude >= -90 && latitude <= 90;
    }

    private boolean isValidLongitude(double longitude) {
        return longitude >= -180 && longitude <= 180;
    }
}
