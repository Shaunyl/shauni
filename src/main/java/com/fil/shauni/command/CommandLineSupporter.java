package com.fil.shauni.command;

import com.fil.shauni.exception.ShauniException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author FTestino
 */
@Log4j2
public class CommandLineSupporter {

    private final String[] args;

    public CommandLineSupporter(final String[] args) throws ShauniException {
        if (args == null) {
            throw new ShauniException(600, "Arguments cannot be null!");
        }
        this.args = args;
    }

    /**
     *
     * @param <T>
     * @param parameter the parameter to research.
     * @param clazz
     * @return the value of the parameter researched. Null if no one could be
     * found.
     * @throws com.fil.shauni.exception.ShauniException
     */
    public <T> T getValue(String parameter, Class<T> clazz) throws ShauniException {
        if (args == null || args.length == 0) {
            return null;
        }
        if (parameter == null || parameter.length() == 0) {
            return null;
        }
        T value = null;
        for (String arg : args) {
            if (arg.matches("-" + parameter + "=.+")) {
                String v = arg.replaceFirst("-" + parameter + "=", "");
                if (clazz.isAssignableFrom(Integer.class)) {
                    value = (T) (Integer) Integer.parseInt(v);
                } else if (clazz.isAssignableFrom(String.class)) {
                    value = (T) v;
                } else if (clazz.isAssignableFrom(Boolean.class)) {
                    if (v.equals("y")) {
                        v = "true";
                    } else if (v.equals("n")) {
                        v = "false";
                    }
                    value = (T) (Boolean) Boolean.parseBoolean(v);
                } else { // That is a checked exception, because I know that the shell here does not support the passed type, so I have to handle that.
                    // In this case is the caller that should handle the exception..
                    String message = "not supported by command line.\nAborting..";
                    log.info("{} {}", clazz, message);
                    throw new ShauniException(600, clazz + " " + message);
                }
                break;
            }
        }
        return value;
    }

    /**
     *
     * @return the command name [it is always the first parameter]
     */
    public String getCommandName() {
        if (args == null || args.length == 0) {
            return null;
        }
        return args[0];
    }

    public String getCommand() {
        if (args == null || args.length == 0) {
            return null;
        }
        String cmd = getCommandName();
        for (int i = 1; i < args.length; i++) {
            cmd += " " + args[i];
        }
        return cmd;
    }

    public int countNoDashedParameters() {
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("([-a-zA-Z0-9]+=[^\"]\\S*|[-a-zA-Z0-9]+=\".+?\")\\s*").matcher(getCommand());
        while (m.find()) {
            String ms = m.group(1);
            if (!ms.startsWith("-")) {
                list.add(ms); // Add .replace("\"", "") to remove surrounding quotes.
            }
        }
        return list.size();
    }
}
