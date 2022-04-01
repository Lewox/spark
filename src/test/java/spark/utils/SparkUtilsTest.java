package spark.utils;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SparkUtilsTest {

    @Test
    public void testConvertRouteToList() {

        List<String> expected = Arrays.asList("api", "person", ":id");

        List<String> actual = SparkUtils.convertRouteToList("/api/person/:id");

        assertThat("Should return route as a list of individual elements that path is made of",
                actual,
                is(expected));
    }

    @Test
    public void testIsParam_whenParameterFormattedAsParam() {
        assertTrue(SparkUtils.isParam(":param"), "Should return true because parameter follows convention of a parameter (:paramname)");
    }

    @Test
    public void testIsParam_whenParameterNotFormattedAsParam() {
        assertFalse(SparkUtils.isParam(".param"), "Should return false because parameter does not follows convention of a parameter (:paramname)");
    }


    @Test
    public void testIsSplat_whenParameterIsASplat() {
        assertTrue(SparkUtils.isSplat("*"), "Should return true because parameter is a splat (*)");
    }

    @Test
    public void testIsSplat_whenParameterIsNotASplat() {
        assertFalse(SparkUtils.isSplat("!"), "Should return false because parameter is not a splat (*)");
    }
}
