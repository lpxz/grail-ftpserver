package org.apache.ftpserver.config.spring;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ftpserver.FtpServerConfigurationException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SpringUtil {

    public static List<Element> getChildElements(final Element elm) {
        List<Element> elements = new ArrayList<Element>();
        NodeList childs = elm.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node child = childs.item(i);
            if (child instanceof Element) {
                elements.add((Element) child);
            }
        }
        return elements;
    }

    public static Element getChildElement(final Element parent, final String ns, final String localName) {
        List<Element> elements = getChildElements(parent);
        for (Element element : elements) {
            if ((ns == null || ns.equals(element.getNamespaceURI()) && (localName == null || localName.equals(element.getLocalName())))) {
                return element;
            }
        }
        return null;
    }

    public static String getChildElementText(final Element parent, final String ns, final String localName) {
        List<Element> elements = getChildElements(parent);
        for (Element element : elements) {
            if ((ns == null || ns.equals(element.getNamespaceURI()) && (localName == null || localName.equals(element.getLocalName())))) {
                return DomUtils.getTextValue(element);
            }
        }
        return null;
    }

    public static Object parseSpringChildElement(final Element parent, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
        Element springElm = getChildElement(parent, null, null);
        String ln = springElm.getLocalName();
        if ("bean".equals(ln)) {
            return parserContext.getDelegate().parseBeanDefinitionElement(springElm, builder.getBeanDefinition());
        } else if ("ref".equals(ln)) {
            return parserContext.getDelegate().parsePropertySubElement(springElm, builder.getBeanDefinition());
        } else {
            throw new FtpServerConfigurationException("Unknown spring element " + ln);
        }
    }

    public static boolean parseBoolean(final Element parent, final String attrName, final boolean defaultValue) {
        if (StringUtils.hasText(parent.getAttribute(attrName))) {
            return Boolean.parseBoolean(parent.getAttribute(attrName));
        }
        return defaultValue;
    }

    public static int parseInt(final Element parent, final String attrName) {
        return Integer.parseInt(parent.getAttribute(attrName));
    }

    public static int parseInt(final Element parent, final String attrName, final int defaultValue) {
        if (StringUtils.hasText(parent.getAttribute(attrName))) {
            return Integer.parseInt(parent.getAttribute(attrName));
        }
        return defaultValue;
    }

    public static String parseString(final Element parent, final String attrName) {
        if (parent.hasAttribute(attrName)) {
            return parent.getAttribute(attrName);
        } else {
            return null;
        }
    }

    public static File parseFile(final Element parent, final String attrName) {
        if (StringUtils.hasText(parent.getAttribute(attrName))) {
            return new File(parent.getAttribute(attrName));
        }
        return null;
    }

    public static InetAddress parseInetAddress(final Element parent, final String attrName) {
        if (StringUtils.hasText(parent.getAttribute(attrName))) {
            try {
                return InetAddress.getByName(parent.getAttribute(attrName));
            } catch (UnknownHostException e) {
                throw new FtpServerConfigurationException("Unknown host", e);
            }
        }
        return null;
    }

    public static String parseStringFromInetAddress(final Element parent, final String attrName) {
        if (parseInetAddress(parent, attrName) != null) {
            return parent.getAttribute(attrName);
        }
        return null;
    }
}
