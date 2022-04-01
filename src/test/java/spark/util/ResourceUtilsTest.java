package spark.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.jupiter.api.Test;
import spark.utils.ResourceUtils;

public class ResourceUtilsTest {

    @Test
    public void testGetFile_whenURLProtocolIsNotFile_thenThrowFileNotFoundException() throws MalformedURLException {
        URL url = new URL("http://example.com/");
        final FileNotFoundException ex = assertThrows(FileNotFoundException.class, () -> ResourceUtils.getFile(url, "My File Path"));
        assertEquals("My File Path cannot be resolved to absolute file path " +
              "because it does not reside in the file system: http://example.com/", ex.getMessage());
    }

    @Test
    public void testGetFile_whenURLProtocolIsFile_thenReturnFileObject() throws
                                                                         MalformedURLException,
                                                                         FileNotFoundException,
                                                                         URISyntaxException {
        URL url = new URL("file://public/file.txt");
        File file = ResourceUtils.getFile(url, "Some description");

        assertEquals(file, new File(ResourceUtils.toURI(url).getSchemeSpecificPart()), "Should be equals because URL protocol is file");
    }

}
