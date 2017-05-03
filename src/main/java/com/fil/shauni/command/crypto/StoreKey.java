package com.fil.shauni.command.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Log4j @Component
public class StoreKey {

    private final static String KEY_STORE = "config/multicfg.k";

    private final static String KEY_STORE_PASSWD = "filippo";

    private final static String CHIPER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private static PasswordProtection passwdProtection;

    public StoreKey() {
        passwdProtection = new PasswordProtection(KEY_STORE_PASSWD.toCharArray());
    }

    public void setKey() {
        try {
            KeyStore keyStore = loadKeyStore();
            
            SecretKey key = KeyGenerator.getInstance("AES").generateKey();
            KeyStore.SecretKeyEntry keyStoreEntry = new KeyStore.SecretKeyEntry(key);
            
            keyStore.setEntry("multidb.cfg", keyStoreEntry, passwdProtection);
            keyStore.store(new FileOutputStream(KEY_STORE), KEY_STORE_PASSWD.toCharArray());
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        } catch (KeyStoreException | IOException | CertificateException e) {
            log.error(e.getMessage());
        }

    }

    public SecretKey getKey() {
        SecretKey sk = null;
        try {
            KeyStore store = loadKeyStore();
            KeyStore.Entry entry = store.getEntry("multidb.cfg", passwdProtection);
            sk = ((KeyStore.SecretKeyEntry) entry).getSecretKey();
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            log.error(e.getMessage());
        }
        return sk;
    }

    private KeyStore loadKeyStore() {
        try {
            File file = new File(KEY_STORE);

            final KeyStore keyStoreCreate = KeyStore.getInstance("JCEKS");

            if (file.exists()) {
                keyStoreCreate.load(new FileInputStream(file), KEY_STORE_PASSWD.toCharArray());
            } else {
                keyStoreCreate.load(null, null);
                keyStoreCreate.store(new FileOutputStream(file), KEY_STORE_PASSWD.toCharArray());
            }
            return keyStoreCreate;
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public CipherInputStream decrypt(File file, SecretKey key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        FileInputStream fin = new FileInputStream(file);
        byte[] iv = new byte[16];
        if (fin.read(iv) < 16) {
            throw new IllegalArgumentException("Invalid file length (needs a full block for iv)");
        }
        Cipher cipher = Cipher.getInstance(CHIPER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        CipherInputStream cis = new CipherInputStream(fin, cipher);
        return cis;
    }
    
    public CipherOutputStream append(File file, SecretKey key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return append(file, key, null);
    }

    public CipherOutputStream append(File file, SecretKey key, SecureRandom sr) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        RandomAccessFile rfile = new RandomAccessFile(file, "rw");
        byte[] iv = new byte[16];
        byte[] lastBlock = null;
        if (rfile.length() % 16L != 0L) {
            throw new IllegalArgumentException("Invalid file length (not a multiple of block size)");
        } else if (rfile.length() == 16) {
            throw new IllegalArgumentException("Invalid file length (need 2 blocks for iv and data)");
        } else if (rfile.length() == 0L) {
            // new file: start by appending an IV
            if (sr == null) {
                sr = new SecureRandom();
            }
            sr.nextBytes(iv);
            rfile.write(iv);
            // [data] [iv=16]   
        } else {
            // file length is at least 2 blocks (16b for IV, 16b for data)
            rfile.seek(rfile.length() - 32);
            rfile.read(iv); // read first 16b of data (IV)
            byte[] lastBlockEnc = new byte[16];
            rfile.read(lastBlockEnc); // then the last block
            Cipher cipher = Cipher.getInstance(CHIPER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            lastBlock = cipher.doFinal(lastBlockEnc); // decrypt using the last block
            rfile.seek(rfile.length() - 16);
        }
        Cipher cipher = Cipher.getInstance(CHIPER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] out;
        if (lastBlock != null) {
            out = cipher.update(lastBlock);
            if (out != null) {
                rfile.write(out);
            }
        }
        CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(rfile.getFD()), cipher);
        return cos;
    }
}
