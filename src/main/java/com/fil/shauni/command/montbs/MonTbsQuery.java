package com.fil.shauni.command.montbs;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Shaunyl
 */
public class MonTbsQuery {

    public static final String DEFAULT_MONTBS_QUERY;

    static {
        DEFAULT_MONTBS_QUERY = loadResourceToString("/config/queries/montbs.sql");
    }

//    static {
//        InputStream str = null;
//        try {
//            str = MonTbsQuery.class.getResourceAsStream("/config/queries/montbs.sql");
//            DEFAULT_MONTBS_QUERY = IOUtils.toString(str);
//        } catch (IOException e) {
//            throw new IllegalStateException("Failed to read SQL query:\n  -> ", e);
//        } finally {
//            IOUtils.closeQuietly(str);
//        }
//
//    }

    private static String loadResourceToString(final String path) {
        final InputStream stream
                = Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);
        try {
            return IOUtils.toString(stream);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }
}
