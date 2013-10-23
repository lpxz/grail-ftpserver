package org.apache.ftpserver.config.spring;

import java.util.List;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.command.CommandFactoryFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class CommandFactoryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<? extends CommandFactory> getBeanClass(final Element element) {
        return null;
    }

    @Override
    protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
        BeanDefinitionBuilder factoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(CommandFactoryFactory.class);
        ManagedMap commands = new ManagedMap();
        List<Element> childs = SpringUtil.getChildElements(element);
        for (Element commandElm : childs) {
            String name = commandElm.getAttribute("name");
            Object bean = SpringUtil.parseSpringChildElement(commandElm, parserContext, builder);
            commands.put(name, bean);
        }
        factoryBuilder.addPropertyValue("commandMap", commands);
        if (StringUtils.hasText(element.getAttribute("use-default"))) {
            factoryBuilder.addPropertyValue("useDefaultCommands", Boolean.parseBoolean(element.getAttribute("use-default")));
        }
        BeanDefinition factoryDefinition = factoryBuilder.getBeanDefinition();
        String factoryId = parserContext.getReaderContext().generateBeanName(factoryDefinition);
        BeanDefinitionHolder factoryHolder = new BeanDefinitionHolder(factoryDefinition, factoryId);
        registerBeanDefinition(factoryHolder, parserContext.getRegistry());
        builder.getRawBeanDefinition().setFactoryBeanName(factoryId);
        builder.getRawBeanDefinition().setFactoryMethodName("createCommandFactory");
    }
}
