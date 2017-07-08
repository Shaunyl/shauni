package com.fil.shauni.command;

import com.beust.jcommander.ParameterException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Log4j2
public class CommandLineSupporter {

    public static void printCliHelp(Map<String, Command> commands) {
        commands.entrySet().forEach((e) -> {
            log.info("{}:\n\t{}", e.getKey(), e.getValue().getDescription());
        });
        log.info("\nEnter '<command> help' for list of supported options for that command.");
    }
    
    public static void printVersion() {
        log.info("Shauni version 1.0.2-45"); // FIXME
    }
    
    public static boolean isThere(List<String> args, final @NonNull String option) {
        noArguments(args);
        for (String arg : args) {
            if (arg.matches("-" + option + ".*")) {
                return true;
            }
        }
        return false;
    }

    public static void integrate(List<String> args) {
        noArguments(args);
        for (int i = 0; i < args.size(); i++) {
            String s = args.get(i);
            if (s.matches("@.+")) {
                throw new ParameterException("@ syntax is not supported. Use -parfile instead.");
            }
        }
    }

    /**
     *
     * @param <T>
     * @param parameter the parameter to research.
     * @param clazz
     * @return the value of the parameter researched. Null if no one could be
     * found.
     */
    public static <T> T getValue(List<String> args, String parameter, Class<T> clazz, T def) {
        noArguments(args);
        T value = def;
        for (String arg : args) {
            if (arg.matches("-" + parameter + "=.+")) {
                String v = arg.replaceFirst("-" + parameter + "=", "");
                if (clazz.isAssignableFrom(Integer.class)) {
                    value = (T) (Integer) Integer.parseInt(v);
                } else if (clazz.isAssignableFrom(String.class)) {
                    value = (T) v;
                } else if (clazz.isAssignableFrom(Boolean.class)) {
                    v = v.equals("y") ? "true" : "false";
                    // Check better boolean parameter when passing inexistent parameters..
                    value = (T) (Boolean) Boolean.parseBoolean(v);
                } else { // That is a checked exception, because I know that the shell here does not support the passed type, so I have to handle that.
                    // In this case is the caller that should handle the exception..
                    throw new RuntimeException("Type not supported.");
                }
                break;
            }
        }
        return value;
    }

    /**
     *
     * @param args list of arguments
     * @return command name [it is always the first parameter]
     */
    public static String getCommandName(List<String> args) {
        noArguments(args);
        return args.get(0);
    }

    public static String getCommand(List<String> args) {
        String cmd = getCommandName(args);
        for (int i = 1; i < args.size(); i++) {
            cmd += " " + args.get(i);
        }
        return cmd;
    }

    public static void checkForNoDashParameters(List<String> args) {
        noArguments(args);
        int count = 0;
        Matcher m = Pattern.compile("([-a-zA-Z0-9]+=[^\"]\\S*|[-a-zA-Z0-9]+=\".+?\")\\s*").matcher(getCommand(args));
        while (m.find()) {
            String ms = m.group(1);
            if (!ms.startsWith("-")) {
                count++;
            }
        }
        if (count > 0) {
            throw new ParameterException("Options without a dash in front are not supported");
        }
    }

    private static boolean isNullOrEmpty(final Collection<?> c) {
        return c == null || c.isEmpty();
    }

    private static void noArguments(List<String> args) {
        if (isNullOrEmpty(args)) {
            throw new ParameterException("No command found.");
        }
    }

}
