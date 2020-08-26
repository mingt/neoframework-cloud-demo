
package com.anilallewar.microservices.auth.common;

import java.security.SecureRandom;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created by igylove on 2018/1/3.
 */
public class ExtendedBCryptPasswordEncoder extends BCryptPasswordEncoder {

    public ExtendedBCryptPasswordEncoder() {}

    public ExtendedBCryptPasswordEncoder(int strength) {
        super(strength);
    }

    public ExtendedBCryptPasswordEncoder(int strength, SecureRandom random) {
        super(strength, random);
    }

    // @Override
    // public String encode(CharSequence rawPassword) {
    // // rawPassword = AESCipher.aesDecryptString(rawPassword.toString());
    // return super.encode(rawPassword);
    // }
    //
    // @Override
    // public boolean matches(CharSequence rawPassword, String encodedPassword) {
    // rawPassword = AESCipher.aesDecryptString(rawPassword.toString());
    // return super.matches(rawPassword, encodedPassword);
    // }
}
