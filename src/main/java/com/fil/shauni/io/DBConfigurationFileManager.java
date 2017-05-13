package com.fil.shauni.io;

import com.fil.shauni.command.DatabaseConfiguration;
import com.fil.shauni.command.crypto.StoreKey;
import com.fil.shauni.exception.ShauniException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author FTestino
 */
@Component @Log4j
public class DBConfigurationFileManager {

    @Inject
    private StoreKey storeKey;

    private SecretKey secretKey;
    
    public List<String> read() throws ShauniException {
        this.secretKey = storeKey.getKey();
        List<String> urls = new ArrayList<>();
        File file = new File(DatabaseConfiguration.MULTIDB_CONN_ENCRYPTED);
        boolean isThere = file.exists();
        if (!isThere) {
            return null;
        }
        try {
            try (CipherInputStream cis = storeKey.decrypt(file, secretKey); BufferedReader bread = new BufferedReader(new InputStreamReader(cis, "UTF-8"))) {
                
                String line;
                while ((line = bread.readLine()) != null) {
                    String[] e = line.split("=");
                    String ey = e[1];
                    urls.add(ey);
                }
            }
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new ShauniException(1007, e.getMessage());
        }
        return urls;
    }
}
