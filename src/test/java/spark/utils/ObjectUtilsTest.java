package spark.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ObjectUtilsTest {

    @Test
    public void testIsEmpty_whenArrayIsEmpty() {
        assertTrue(ObjectUtils.isEmpty(new Object[]{}), "Should return true because array is empty");
    }

    @Test
    public void testIsEmpty_whenArrayIsNotEmpty() {
        assertFalse(ObjectUtils.isEmpty(new Integer[]{1,2}), "Should return false because array is not empty");
    }
}
