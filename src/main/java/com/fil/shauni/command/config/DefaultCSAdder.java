package com.fil.shauni.command.config;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.internal.Lists;
import com.fil.shauni.command.ConfigCommandControl;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Log4j @Component(value = "addcs") @Parameters(separators = "=")
public class DefaultCSAdder extends ConfigCommandControl {

//    @Parameter(required = true, arity = 1)
//    private final List<String> cmd = Lists.newArrayList(1);

    @Parameter(names = "-key", arity = 1, required = true)
    protected String key;

    @Parameter(names = "-user", arity = 1, required = true)
    protected String user;

    @Parameter(names = "-password", arity = 1, required = true)
    protected String password;

    @Parameter(names = "-host", arity = 1, required = true)
    protected String host;

    @Parameter(names = "-port", arity = 1, required = true)
    protected String port;

    @Parameter(names = "-sid", arity = 1, required = true)
    protected String sid;

    @Inject
    private StoreKey sk;

    private SecretKey skey;

    private final String JDBC = "jdbc:oracle:thin:";

    private boolean isMultiDb = false;

    private final Map<String, String> map = new HashMap<>();

    private File f = null;

    public DefaultCSAdder() {
        super();
        isCluster = false;
    }

    @Override
    public void setup() throws ShauniException {
        super.setup();
        this.skey = sk.getKey();
        // check if file is already there
        f = new File(DatabaseConfiguration.MULTIDB_CONN_ENCRYPTED);
        this.isMultiDb = f.exists();
        if (isMultiDb) { // useless
            try {
                CipherInputStream cis = sk.decrypt(f, skey);
                BufferedReader bread = new BufferedReader(new InputStreamReader(cis, "UTF-8"));

                String line;
                // Check integrity with RegEx. Also before adding. FIXME: !!!!!!!!!
                while ((line = bread.readLine()) != null) {
                    String[] e = line.split("=");
                    // Check for name existence
                    String ky = e[0];
                    String ey = e[1];
                    if (key.equals(ky)) {
                        errorCount += 1;
                        throw new ShauniException(1012, "Key " + ky + " already exists");
                    }
                    map.put(ky, ey);
                }
            } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
                errorCount += 1;
                throw new ShauniException(1007, e.getMessage());
            }
        }
    }

    @Override
    public void run() throws ShauniException {
        // Base connection string (SID_NAME)..
        String ptext = key + "=" + JDBC + user + "/" + password + "@" + host + ":" + port + ":" + sid;
        try (CipherOutputStream cos = sk.append(f, skey)) {
            cos.write((ptext + "\n").getBytes("UTF-8"));
            log.info("Connection string has been added:\n  -> " + ptext);
        } catch (Exception e) {
            errorCount += 1;
            throw new ShauniException(1011, "" + e.getMessage());
        }
    }

    @Override
    public void takedown() {
        super.takedown();
    }
}
