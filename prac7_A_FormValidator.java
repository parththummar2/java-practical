import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) @interface NotBlank {}
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) @interface MaxLength { int value(); }
class SignupForm {
    @NotBlank @MaxLength(20) private String username;
    @NotBlank @MaxLength(40) private String email;
    SignupForm(String username, String email) { this.username = username; this.email = email; }
}
public class prac7_A_FormValidator {
    static List<String> validate(Object object) throws IllegalAccessException {
        List<String> errors = new ArrayList<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object raw = field.get(object);
            String value = raw == null ? null : raw.toString();
            if (field.isAnnotationPresent(NotBlank.class) && (value == null || value.isBlank()))
                errors.add(field.getName() + " must not be blank");
            MaxLength max = field.getAnnotation(MaxLength.class);
            if (max != null && value != null && value.length() > max.value())
                errors.add(field.getName() + " must be at most " + max.value() + " characters");
        }
        return errors;
    }
    public static void main(String[] args) throws Exception {
        SignupForm form = new SignupForm(" ", "averylongemailaddress@example.com");
        List<String> errors = validate(form);
        System.out.println("Validation errors:");
        for (String error : errors) System.out.println("- " + error);
        System.out.println("Total errors: " + errors.size());
    }
}
