package spark;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

public class ExceptionMapperTest {

    @Test
    public void testGetInstance_whenDefaultInstanceIsNull() {
        //given
        ExceptionMapper exceptionMapper = null;
        Whitebox.setInternalState(ExceptionMapper.class, "servletInstance", exceptionMapper);

        //then
        exceptionMapper = ExceptionMapper.getServletInstance();
        assertEquals(Whitebox.getInternalState(ExceptionMapper.class, "servletInstance"), exceptionMapper, "Should be equals because ExceptionMapper is a singleton");
    }

    @Test
    public void testGetInstance_whenDefaultInstanceIsNotNull() {
        //given
        ExceptionMapper.getServletInstance(); //initialize Singleton

        //then
        ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();
        assertEquals(Whitebox.getInternalState(ExceptionMapper.class, "servletInstance"), exceptionMapper, "Should be equals because ExceptionMapper is a singleton");
    }
}
