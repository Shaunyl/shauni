package com.fil.shauni;

import com.fil.shauni.mainframe.ui.CommandLinePresentationControl;
import java.util.Arrays;
import java.util.Locale;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
//@Log4j2
public class Main {

    public static final BeanFactory beanFactory;

    static {
//        beanFactory = new ClassPathXmlApplicationContext("file:src/main/resources/beans/Beans.xml");
        beanFactory = new GenericXmlApplicationContext();
        ((GenericXmlApplicationContext )beanFactory).getEnvironment().setDefaultProfiles("production");
        ((GenericXmlApplicationContext )beanFactory).load("file:src/main/resources/beans/Beans.xml");
        ((GenericXmlApplicationContext )beanFactory).refresh();
    }

    public static void main(String[] args) {
        Locale.setDefault(new Locale("en"));

        CommandLinePresentationControl cliControl = beanFactory.getBean(CommandLinePresentationControl.class);
        try {
            if (args == null) {
//                log.error("No arguments provided.\nAborting..");
                return;
            }
            cliControl.executeCommand(Arrays.asList(args));
        } catch (Exception e) {
//            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
