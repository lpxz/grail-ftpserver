package org.apache.ftpserver.usermanager.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import org.apache.ftpserver.FtpServerConfigurationException;
import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.DbUserManagerFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbUserManager extends AbstractUserManager {

    private final Logger LOG = LoggerFactory.getLogger(DbUserManager.class);

    private String insertUserStmt;

    private String updateUserStmt;

    private String deleteUserStmt;

    private String selectUserStmt;

    private String selectAllStmt;

    private String isAdminStmt;

    private String authenticateStmt;

    private DataSource dataSource;

    public DbUserManager(DataSource dataSource, String selectAllStmt, String selectUserStmt, String insertUserStmt, String updateUserStmt, String deleteUserStmt, String authenticateStmt, String isAdminStmt, PasswordEncryptor passwordEncryptor, String adminName) {
        super(adminName, passwordEncryptor);
        this.dataSource = dataSource;
        this.selectAllStmt = selectAllStmt;
        this.selectUserStmt = selectUserStmt;
        this.insertUserStmt = insertUserStmt;
        this.updateUserStmt = updateUserStmt;
        this.deleteUserStmt = deleteUserStmt;
        this.authenticateStmt = authenticateStmt;
        this.isAdminStmt = isAdminStmt;
        Connection con = null;
        try {
            con = createConnection();
            LOG.info("Database connection opened.");
        } catch (SQLException ex) {
            LOG.error("Failed to open connection to user database", ex);
            throw new FtpServerConfigurationException("Failed to open connection to user database", ex);
        } finally {
            closeQuitely(con);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getSqlUserInsert() {
        return insertUserStmt;
    }

    public void setSqlUserInsert(String sql) {
        insertUserStmt = sql;
    }

    public String getSqlUserDelete() {
        return deleteUserStmt;
    }

    public void setSqlUserDelete(String sql) {
        deleteUserStmt = sql;
    }

    public String getSqlUserUpdate() {
        return updateUserStmt;
    }

    public void setSqlUserUpdate(String sql) {
        updateUserStmt = sql;
    }

    public String getSqlUserSelect() {
        return selectUserStmt;
    }

    public void setSqlUserSelect(String sql) {
        selectUserStmt = sql;
    }

    public String getSqlUserSelectAll() {
        return selectAllStmt;
    }

    public void setSqlUserSelectAll(String sql) {
        selectAllStmt = sql;
    }

    public String getSqlUserAuthenticate() {
        return authenticateStmt;
    }

    public void setSqlUserAuthenticate(String sql) {
        authenticateStmt = sql;
    }

    public String getSqlUserAdmin() {
        return isAdminStmt;
    }

    public void setSqlUserAdmin(String sql) {
        isAdminStmt = sql;
    }

    public boolean isAdmin(String login) throws FtpException {
        if (login == null) {
            return false;
        }
        Statement stmt = null;
        ResultSet rs = null;
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(ATTR_LOGIN, escapeString(login));
            String sql = StringUtils.replaceString(isAdminStmt, map);
            LOG.info(sql);
            stmt = createConnection().createStatement();
            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException ex) {
            LOG.error("DbUserManager.isAdmin()", ex);
            throw new FtpException("DbUserManager.isAdmin()", ex);
        } finally {
            closeQuitely(rs);
            closeQuitely(stmt);
        }
    }

    protected Connection createConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        return connection;
    }

    public void delete(String name) throws FtpException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(ATTR_LOGIN, escapeString(name));
        String sql = StringUtils.replaceString(deleteUserStmt, map);
        LOG.info(sql);
        Statement stmt = null;
        try {
            stmt = createConnection().createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            LOG.error("DbUserManager.delete()", ex);
            throw new FtpException("DbUserManager.delete()", ex);
        } finally {
            closeQuitely(stmt);
        }
    }

    public void save(User user) throws FtpException {
        if (user.getName() == null) {
            throw new NullPointerException("User name is null.");
        }
        Statement stmt = null;
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(ATTR_LOGIN, escapeString(user.getName()));
            String password = null;
            if (user.getPassword() != null) {
                password = getPasswordEncryptor().encrypt(user.getPassword());
            } else {
                ResultSet rs = null;
                try {
                    User userWithPassword = selectUserByName(user.getName());
                    if (userWithPassword != null) {
                        password = userWithPassword.getPassword();
                    }
                } finally {
                    closeQuitely(rs);
                }
            }
            map.put(ATTR_PASSWORD, escapeString(password));
            String home = user.getHomeDirectory();
            if (home == null) {
                home = "/";
            }
            map.put(ATTR_HOME, escapeString(home));
            map.put(ATTR_ENABLE, String.valueOf(user.getEnabled()));
            map.put(ATTR_WRITE_PERM, String.valueOf(user.authorize(new WriteRequest()) != null));
            map.put(ATTR_MAX_IDLE_TIME, user.getMaxIdleTime());
            TransferRateRequest transferRateRequest = new TransferRateRequest();
            transferRateRequest = (TransferRateRequest) user.authorize(transferRateRequest);
            if (transferRateRequest != null) {
                map.put(ATTR_MAX_UPLOAD_RATE, transferRateRequest.getMaxUploadRate());
                map.put(ATTR_MAX_DOWNLOAD_RATE, transferRateRequest.getMaxDownloadRate());
            } else {
                map.put(ATTR_MAX_UPLOAD_RATE, 0);
                map.put(ATTR_MAX_DOWNLOAD_RATE, 0);
            }
            ConcurrentLoginRequest concurrentLoginRequest = new ConcurrentLoginRequest(0, 0);
            concurrentLoginRequest = (ConcurrentLoginRequest) user.authorize(concurrentLoginRequest);
            if (concurrentLoginRequest != null) {
                map.put(ATTR_MAX_LOGIN_NUMBER, concurrentLoginRequest.getMaxConcurrentLogins());
                map.put(ATTR_MAX_LOGIN_PER_IP, concurrentLoginRequest.getMaxConcurrentLoginsPerIP());
            } else {
                map.put(ATTR_MAX_LOGIN_NUMBER, 0);
                map.put(ATTR_MAX_LOGIN_PER_IP, 0);
            }
            String sql = null;
            if (!doesExist(user.getName())) {
                sql = StringUtils.replaceString(insertUserStmt, map);
            } else {
                sql = StringUtils.replaceString(updateUserStmt, map);
            }
            LOG.info(sql);
            stmt = createConnection().createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            LOG.error("DbUserManager.save()", ex);
            throw new FtpException("DbUserManager.save()", ex);
        } finally {
            closeQuitely(stmt);
        }
    }

    private void closeQuitely(Statement stmt) {
        if (stmt != null) {
            Connection con = null;
            try {
                con = stmt.getConnection();
            } catch (Exception e) {
            }
            try {
                stmt.close();
            } catch (SQLException e) {
            }
            closeQuitely(con);
        }
    }

    private void closeQuitely(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
    }

    protected void closeQuitely(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
            }
        }
    }

    private BaseUser selectUserByName(String name) throws SQLException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(ATTR_LOGIN, escapeString(name));
        String sql = StringUtils.replaceString(selectUserStmt, map);
        LOG.info(sql);
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = createConnection().createStatement();
            rs = stmt.executeQuery(sql);
            BaseUser thisUser = null;
            if (rs.next()) {
                thisUser = new BaseUser();
                thisUser.setName(rs.getString(ATTR_LOGIN));
                thisUser.setPassword(rs.getString(ATTR_PASSWORD));
                thisUser.setHomeDirectory(rs.getString(ATTR_HOME));
                thisUser.setEnabled(rs.getBoolean(ATTR_ENABLE));
                thisUser.setMaxIdleTime(rs.getInt(ATTR_MAX_IDLE_TIME));
                List<Authority> authorities = new ArrayList<Authority>();
                if (rs.getBoolean(ATTR_WRITE_PERM)) {
                    authorities.add(new WritePermission());
                }
                authorities.add(new ConcurrentLoginPermission(rs.getInt(ATTR_MAX_LOGIN_NUMBER), rs.getInt(ATTR_MAX_LOGIN_PER_IP)));
                authorities.add(new TransferRatePermission(rs.getInt(ATTR_MAX_DOWNLOAD_RATE), rs.getInt(ATTR_MAX_UPLOAD_RATE)));
                thisUser.setAuthorities(authorities);
            }
            return thisUser;
        } finally {
            closeQuitely(rs);
            closeQuitely(stmt);
        }
    }

    public User getUserByName(String name) throws FtpException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            BaseUser user = selectUserByName(name);
            if (user != null) {
                user.setPassword(null);
            }
            return user;
        } catch (SQLException ex) {
            LOG.error("DbUserManager.getUserByName()", ex);
            throw new FtpException("DbUserManager.getUserByName()", ex);
        } finally {
            closeQuitely(rs);
            closeQuitely(stmt);
        }
    }

    public boolean doesExist(String name) throws FtpException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(ATTR_LOGIN, escapeString(name));
            String sql = StringUtils.replaceString(selectUserStmt, map);
            LOG.info(sql);
            stmt = createConnection().createStatement();
            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException ex) {
            LOG.error("DbUserManager.doesExist()", ex);
            throw new FtpException("DbUserManager.doesExist()", ex);
        } finally {
            closeQuitely(rs);
            closeQuitely(stmt);
        }
    }

    public String[] getAllUserNames() throws FtpException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String sql = selectAllStmt;
            LOG.info(sql);
            stmt = createConnection().createStatement();
            rs = stmt.executeQuery(sql);
            ArrayList<String> names = new ArrayList<String>();
            while (rs.next()) {
                names.add(rs.getString(ATTR_LOGIN));
            }
            return names.toArray(new String[0]);
        } catch (SQLException ex) {
            LOG.error("DbUserManager.getAllUserNames()", ex);
            throw new FtpException("DbUserManager.getAllUserNames()", ex);
        } finally {
            closeQuitely(rs);
            closeQuitely(stmt);
        }
    }

    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        if (authentication instanceof UsernamePasswordAuthentication) {
            UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;
            String user = upauth.getUsername();
            String password = upauth.getPassword();
            if (user == null) {
                throw new AuthenticationFailedException("Authentication failed");
            }
            if (password == null) {
                password = "";
            }
            Statement stmt = null;
            ResultSet rs = null;
            try {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(ATTR_LOGIN, escapeString(user));
                String sql = StringUtils.replaceString(authenticateStmt, map);
                LOG.info(sql);
                stmt = createConnection().createStatement();
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    try {
                        String storedPassword = rs.getString(ATTR_PASSWORD);
                        if (getPasswordEncryptor().matches(password, storedPassword)) {
                            return getUserByName(user);
                        } else {
                            throw new AuthenticationFailedException("Authentication failed");
                        }
                    } catch (FtpException e) {
                        throw new AuthenticationFailedException("Authentication failed", e);
                    }
                } else {
                    throw new AuthenticationFailedException("Authentication failed");
                }
            } catch (SQLException ex) {
                LOG.error("DbUserManager.authenticate()", ex);
                throw new AuthenticationFailedException("Authentication failed", ex);
            } finally {
                closeQuitely(rs);
                closeQuitely(stmt);
            }
        } else if (authentication instanceof AnonymousAuthentication) {
            try {
                if (doesExist("anonymous")) {
                    return getUserByName("anonymous");
                } else {
                    throw new AuthenticationFailedException("Authentication failed");
                }
            } catch (AuthenticationFailedException e) {
                throw e;
            } catch (FtpException e) {
                throw new AuthenticationFailedException("Authentication failed", e);
            }
        } else {
            throw new IllegalArgumentException("Authentication not supported by this user manager");
        }
    }

    private String escapeString(String input) {
        if (input == null) {
            return input;
        }
        StringBuilder valBuf = new StringBuilder(input);
        for (int i = 0; i < valBuf.length(); i++) {
            char ch = valBuf.charAt(i);
            if (ch == '\'' || ch == '\\' || ch == '$' || ch == '^' || ch == '[' || ch == ']' || ch == '{' || ch == '}') {
                valBuf.insert(i, '\\');
                i++;
            }
        }
        return valBuf.toString();
    }
}
