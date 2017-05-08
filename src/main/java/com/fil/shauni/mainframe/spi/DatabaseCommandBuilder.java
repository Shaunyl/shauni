package com.fil.shauni.mainframe.spi;

import com.fil.shauni.command.DatabaseConfiguration;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.io.DBConfigurationFileManager;
import com.fil.shauni.io.PropertiesFileManager;
import java.util.List;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Chiara
 */
@Component @NoArgsConstructor
public class DatabaseCommandBuilder implements CommandBuilder {

    @Inject
    private DBConfigurationFileManager dbConfigurationFileManager;

    @Inject
    private PropertiesFileManager propertiesFileManager;

    @Override
    public void initialize(CommandContext ctx) throws ShauniException {
        boolean crypto = ctx.isCrypto();
        List<String> urls = null;
        if (crypto) {
            urls = dbConfigurationFileManager.read();
        } else {
            urls = propertiesFileManager.readAll(DatabaseConfiguration.MULTIDB_CONN);
        }

        ctx.setUrls(urls);
    }

}
