package org.apache.ftpserver.usermanager;

import java.security.SecureRandom;
import org.apache.ftpserver.util.EncryptUtils;

public class SaltedPasswordEncryptor implements PasswordEncryptor {

    private SecureRandom rnd = new SecureRandom();

    private static final int MAX_SEED = 99999999;

    private static final int HASH_ITERATIONS = 1000;

    private String encrypt(String password, String salt) {
        String hash = salt + password;
        for (int i = 0; i < HASH_ITERATIONS; i++) {
            hash = EncryptUtils.encryptMD5(hash);
        }
        return salt + ":" + hash;
    }

    public String encrypt(String password) {
        String seed = Integer.toString(rnd.nextInt(MAX_SEED));
        return encrypt(password, seed);
    }

    public boolean matches(String passwordToCheck, String storedPassword) {
        if (storedPassword == null) {
            throw new NullPointerException("storedPassword can not be null");
        }
        if (passwordToCheck == null) {
            throw new NullPointerException("passwordToCheck can not be null");
        }
        int divider = storedPassword.indexOf(':');
        if (divider < 1) {
            throw new IllegalArgumentException("stored password does not contain salt");
        }
        String storedSalt = storedPassword.substring(0, divider);
        return encrypt(passwordToCheck, storedSalt).equalsIgnoreCase(storedPassword);
    }
}
