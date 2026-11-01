import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) @interface Column { String value(); }
class StudentRow {
    @Column("student_name") String name;
    @Column("student_age") int age;
}
public class prac7_C_ColumnMapper {
    static <T> T mapRow(Map<String, String> row, Class<T> type) throws Exception {
        T object = type.getDeclaredConstructor().newInstance();
        for (Field field : type.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column == null || !row.containsKey(column.value())) continue;
            field.setAccessible(true);
            String value = row.get(column.value());
            if (field.getType() == int.class) field.setInt(object, Integer.parseInt(value));
            else field.set(object, value);
        }
        return object;
    }
    public static void main(String[] args) throws Exception {
        Map<String, String> row = Map.of("student_age", "20", "student_name", "Aarav");
        StudentRow student = mapRow(row, StudentRow.class);
        System.out.println("Name: " + student.name);
        System.out.println("Age: " + student.age);
    }
}
