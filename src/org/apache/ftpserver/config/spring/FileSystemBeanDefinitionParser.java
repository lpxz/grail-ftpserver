package org.apache.ftpserver.config.spring;

import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class FileSystemBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<? extends FileSystemFactory> getBeanClass(final Element element) {
        return NativeFileSystemFactory.class;
    }

    @Override
    protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
        if (StringUtils.hasText(element.getAttribute("case-insensitive"))) {
            builder.addPropertyValue("caseInsensitive", Boolean.parseBoolean(element.getAttribute("case-insensitive")));
        }
        if (StringUtils.hasText(element.getAttribute("create-home"))) {
            builder.addPropertyValue("createHome", Boolean.parseBoolean(element.getAttribute("create-home")));
        }
    }
}
