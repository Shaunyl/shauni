package com.fil.shauni.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Cleanup;

/**
 *
 * @author Shaunyl
 */
public class GeneralUtil {

    private static final String REGEX = "jdbc:oracle:thin:(.*?)@(.*?):(\\d+):(.*?)$";

//    private static final String REGEX_NO_PWD = "jdbc:oracle:thin:(.*?)@(.*?):(\\d+):(.*?)$";
    private static final String REGEX_WITHOUT = "jdbc:oracle:thin:@(.*?):(\\d+):(.*?)$";

    private static final String REGEX_SERVICE = "jdbc:oracle:thin:(.*?)@(.*?):(\\d+)/(.*?)$";

    public static Map<String, String> parseConnectionString(String connectionString) {
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(connectionString);
        boolean valid = m.find();
        if (!valid) {
            // try also with service:
            p = Pattern.compile(REGEX_SERVICE);
            m = p.matcher(connectionString);
            valid = m.find();
            if (!valid) {
                throw new RuntimeException("Connection string is not valid.");
            }
        }
        String password = "", user = "";
        if (!m.group(1).isEmpty()) {
            String[] creds = m.group(1).split("/", 2);
            user = creds[0];
            password = creds[1];
        }
        String host = m.group(2), port = m.group(3), sid = m.group(4);
        Map<String, String> table = new HashMap<>(5);
        table.put("user", user);
        table.put("password", password);
        table.put("host", host);
        table.put("port", port);
        table.put("sid", sid);
        return table;
    }

    public static Map<String, String> parseConnectionStringWithoutPassword(String connectionString) {
        Pattern p = Pattern.compile(REGEX_WITHOUT);
        Matcher m = p.matcher(connectionString);
        boolean valid = m.find();
        if (!valid) {
            throw new RuntimeException("Some stuff in this property list file may be corrupted. No changes were commited.");
        }
        String host = m.group(1), port = m.group(2), sid = m.group(3);
        Map<String, String> table = new HashMap<>(3);
        table.put("host", host);
        table.put("port", port);
        table.put("sid", sid);
        return table;
    }

    public static File[] getAllDirectoryFiles(String filename, final String extension) {
        File file = new File(filename);//fixme
        File parent = file.getParentFile();
        File[] files = parent.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(extension);
            }
        });
        return files;
    }

    public static String getCurrentDate(String format) {
        return new SimpleDateFormat(format).format(new java.util.Date());
    }
    
    public static String getCurrentDate(DateFormat format) {
        return getCurrentDate(format.toString());
    }

    public static String readFile(String path) {
        String content = new Scanner(
                GeneralUtil.class.getResourceAsStream(path), "UTF-8").next();
        return content;
    }

    public static int utf8StringLength(final CharSequence sequence) {
        int count = 0;
        for (int i = 0, len = sequence.length(); i < len; i++) {
            char ch = sequence.charAt(i);
            if (ch <= 0x7F) {
                count++;
            } else if (ch <= 0x7FF) {
                count += 2;
            } else if (Character.isHighSurrogate(ch)) {
                count += 4;
                ++i;
            } else {
                count += 3;
            }
        }
        return count;
    }

    public static String readClob(final Clob c) throws SQLException, IOException {
        StringBuilder sb = new StringBuilder((int) c.length());
        Reader r = c.getCharacterStream();
        char[] cbuf = new char[2048];
        int n = 0;
        while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
            if (n > 0) {
                sb.append(cbuf, 0, n);
            }
        }
        return sb.toString();
    }

    public static void fileToClobField(final String file, final java.sql.Clob clob) throws SQLException {
        try {
            BufferedReader br;
            @Cleanup
            Writer os = clob.setCharacterStream(0L);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
                line = br.readLine();
            }
            os.write(sb.toString());
            br.close();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String repeat(String str, int repeat) {
        String repeated = "";
        for (int i = 0; i < repeat; i++) {
            repeated += str;
        }
        return repeated;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}