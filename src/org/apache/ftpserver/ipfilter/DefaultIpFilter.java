package org.apache.ftpserver.ipfilter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.mina.filter.firewall.Subnet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIpFilter extends CopyOnWriteArraySet<Subnet> implements IpFilter {

    Logger LOGGER = LoggerFactory.getLogger(DefaultIpFilter.class);

    private static final long serialVersionUID = 4887092372700628783L;

    private IpFilterType type = null;

    public DefaultIpFilter(IpFilterType type) {
        this(type, new HashSet<Subnet>(0));
    }

    public DefaultIpFilter(IpFilterType type, Collection<? extends Subnet> collection) {
        super(collection);
        this.type = type;
    }

    public DefaultIpFilter(IpFilterType type, String addresses) throws NumberFormatException, UnknownHostException {
        super();
        this.type = type;
        if (addresses != null) {
            String[] tokens = addresses.split("[\\s,]+");
            for (String token : tokens) {
                if (token.trim().length() > 0) {
                    add(token);
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Created DefaultIpFilter of type {} with the subnets {}", type, this);
        }
    }

    public IpFilterType getType() {
        return type;
    }

    public void setType(IpFilterType type) {
        this.type = type;
    }

    public boolean add(String str) throws NumberFormatException, UnknownHostException {
        if (str.trim().length() < 1) {
            throw new IllegalArgumentException("Invalid IP Address or Subnet: " + str);
        }
        String[] tokens = str.split("/");
        if (tokens.length == 2) {
            return add(new Subnet(InetAddress.getByName(tokens[0]), Integer.parseInt(tokens[1])));
        } else {
            return add(new Subnet(InetAddress.getByName(tokens[0]), 32));
        }
    }

    public boolean accept(InetAddress address) {
        switch(type) {
            case ALLOW:
                for (Subnet subnet : this) {
                    if (subnet.inSubnet(address)) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Allowing connection from {} because it matches with the whitelist subnet {}", new Object[] { address, subnet });
                        }
                        return true;
                    }
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Denying connection from {} because it does not match any of the whitelist subnets", new Object[] { address });
                }
                return false;
            case DENY:
                if (isEmpty()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Allowing connection from {} because blacklist is empty", new Object[] { address });
                    }
                    return true;
                }
                for (Subnet subnet : this) {
                    if (subnet.inSubnet(address)) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Denying connection from {} because it matches with the blacklist subnet {}", new Object[] { address, subnet });
                        }
                        return false;
                    }
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Allowing connection from {} because it does not match any of the blacklist subnets", new Object[] { address });
                }
                return true;
            default:
                throw new RuntimeException("Unknown or unimplemented filter type: " + type);
        }
    }
}
