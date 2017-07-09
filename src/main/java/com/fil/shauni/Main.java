package com.fil.shauni;

import com.fil.shauni.mainframe.ui.CommandLinePresentationControl;
import java.util.Arrays;
import java.util.Locale;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Log4j2
public class Main {

    public static final AnnotationConfigApplicationContext beanFactory = new AnnotationConfigApplicationContext();
    
    static {
        beanFactory.getEnvironment().setDefaultProfiles("production");
        beanFactory.register(BeanConfiguration.class);
        beanFactory.refresh();
    }

    public static void main(String[] args) {
        Locale.setDefault(new Locale("en"));

        CommandLinePresentationControl cliControl = beanFactory.getBean(CommandLinePresentationControl.class);
                
        try {
            if (args == null) {
                log.error("No arguments provided.\nAborting..");
                return;
            }
            cliControl.executeCommand(Arrays.asList(args));
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
