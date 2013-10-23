package org.apache.ftpserver.config.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class FtpServerNamespaceHandler extends NamespaceHandlerSupport {

    public static final String FTPSERVER_NS = "http://mina.apache.org/ftpserver/spring/v1";

    public FtpServerNamespaceHandler() {
        registerBeanDefinitionParser("server", new ServerBeanDefinitionParser());
        registerBeanDefinitionParser("nio-listener", new ListenerBeanDefinitionParser());
        registerBeanDefinitionParser("file-user-manager", new UserManagerBeanDefinitionParser());
        registerBeanDefinitionParser("db-user-manager", new UserManagerBeanDefinitionParser());
        registerBeanDefinitionParser("native-filesystem", new FileSystemBeanDefinitionParser());
        registerBeanDefinitionParser("commands", new CommandFactoryBeanDefinitionParser());
    }

    public void init() {
    }
}
