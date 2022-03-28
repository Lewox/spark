package spark;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FilterImplTest {

    private static final String PATH_TEST = "/etc/test";
    private static final String ACCEPT_TYPE_TEST  = "test/*";

    private FilterImpl filter;

    @Test
    public void testConstructor(){
        FilterImpl filter = new FilterImpl(PATH_TEST, ACCEPT_TYPE_TEST) {
            @Override
            public void handle(Request request, Response response) {
            }
        };
        assertEquals(PATH_TEST, filter.getPath(), "Should return path specified");
        assertEquals(ACCEPT_TYPE_TEST, filter.getAcceptType(), "Should return accept type specified");
    }

    @Test
    public void testGets_thenReturnGetPathAndGetAcceptTypeSuccessfully() {
        filter = FilterImpl.create(PATH_TEST, ACCEPT_TYPE_TEST, null);
        assertEquals(PATH_TEST, filter.getPath(), "Should return path specified");
        assertEquals(ACCEPT_TYPE_TEST, filter.getAcceptType(), "Should return accept type specified");
    }

    @Test
    public void testCreate_whenOutAssignAcceptTypeInTheParameters_thenReturnPathAndAcceptTypeSuccessfully(){
        filter = FilterImpl.create(PATH_TEST, null);
        assertEquals(PATH_TEST, filter.getPath(), "Should return path specified");
        assertEquals(RouteImpl.DEFAULT_ACCEPT_TYPE, filter.getAcceptType(), "Should return accept type specified");
    }

    @Test
    public void testCreate_whenAcceptTypeNullValueInTheParameters_thenReturnPathAndAcceptTypeSuccessfully(){
        filter = FilterImpl.create(PATH_TEST, null, null);
        assertEquals(PATH_TEST, filter.getPath(), "Should return path specified");
        assertEquals(RouteImpl.DEFAULT_ACCEPT_TYPE, filter.getAcceptType(), "Should return accept type specified");
    }
}
