package io.libralink.platform.agent.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class ConfirmationHashUtils {

    private ConfirmationHashUtils() {}

    public static String getHash(String id, long expiresAt, String salt, String accountId) throws NoSuchAlgorithmException {
        String input = concatenate(id, expiresAt, salt, accountId);

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(input.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    private static String concatenate(String id, long expiresAt, String salt, String accountId) {
        return id + String.valueOf(expiresAt) + accountId + salt;
    }
}
