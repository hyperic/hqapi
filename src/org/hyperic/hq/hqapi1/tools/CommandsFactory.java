package org.hyperic.hq.hqapi1.tools;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Factory that returns a Map of all {@link Command}s in the classpath, keyed by
 * their name (obtained from Command.getName())
 * @author jhickey
 * 
 */
public class CommandsFactory implements FactoryBean<Map<String, Command>>, ApplicationContextAware {
    private ApplicationContext applicationContext;

    public Map<String, Command> getObject() throws Exception {
        Map<String, Command> commands = new TreeMap<String, Command>();
        Map<String, Command> commandBeans = applicationContext.getBeansOfType(Command.class);
        for (Map.Entry<String, Command> entry : commandBeans.entrySet()) {
            commands.put(entry.getValue().getName(), entry.getValue());
        }
        return commands;
    }

    @SuppressWarnings("unchecked")
    public Class getObjectType() {
        return Map.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

}
