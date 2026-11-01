import java.lang.annotation.*;
import java.lang.reflect.Method;
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) @interface Run {}
class SampleChecks {
    @Run public void checkAddition() { System.out.println("checkAddition passed"); }
    @Run public void checkText() { System.out.println("checkText passed"); }
    public void helperMethod() { System.out.println("helper should not run"); }
}
public class prac7_B_MiniTestRunner {
    public static void main(String[] args) throws Exception {
        SampleChecks target = new SampleChecks(); int ran = 0;
        for (Method method : target.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Run.class)) { method.invoke(target); ran++; }
        }
        System.out.println("Methods run: " + ran);
    }
}
