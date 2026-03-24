package org.sodales;

import org.graalvm.polyglot.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class AESFixedKeyEncryption {

    private static String readResource(String path) throws IOException {
        try (InputStream in = AESFixedKeyEncryption.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null)
                throw new FileNotFoundException("Resource not found: " + path);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static void main(String[] args) throws Exception {
        // Load CryptoJS from the WebJar
        String cryptoJsCode = readResource("crypto.js");

        // Define JS functions
        String jsFunctions = """
            var EncryptAES = function(payload, key) {
                return CryptoJS.AES.encrypt(payload, key).toString();
            };

            var DecryptAES = function(payload, key) {
                var decrypted = CryptoJS.AES.decrypt(payload, key);
                return decrypted.toString(CryptoJS.enc.Utf8);
            };
        """;

        try (Context context = Context.newBuilder("js").allowAllAccess(true).build()) {
            // Load CryptoJS
            context.eval("js", cryptoJsCode);
            // Define encryption/decryption functions
            context.eval("js", jsFunctions);

            // Run encryption
            String plain = "{\"name\":\"Soham\"}";
            String key = "secretkey";

            Value encryptFn = context.getBindings("js").getMember("EncryptAES");
            Value decryptFn = context.getBindings("js").getMember("DecryptAES");

            String encrypted = encryptFn.execute(plain, key).asString();
            System.out.println("Encrypted: " + encrypted);

            String decrypted = decryptFn.execute(encrypted, key).asString();
            System.out.println("Decrypted: " + decrypted);
        }
    }
}
