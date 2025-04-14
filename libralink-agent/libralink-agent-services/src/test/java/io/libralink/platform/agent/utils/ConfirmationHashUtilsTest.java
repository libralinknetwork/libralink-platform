package io.libralink.platform.agent.utils;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static io.libralink.platform.agent.utils.ConfirmationHashUtils.getHash;
import static org.junit.Assert.assertEquals;

public class ConfirmationHashUtilsTest {

    @Test
    public void test_generate_md5_hash() throws NoSuchAlgorithmException {

        String actualHash = getHash("075e5892-0e32-4a0f-aeb3-e25394a51a7c", 2533597587000L, "671f59f5-1edf-4eca-bbc0-d80ad0e879a2", "f5ac7d82-5a2e-4f9a-84af-869842fc9f40");
        assertEquals("dhFhkEjUVq4jI0d7BBcDWA==", actualHash);
    }
}
