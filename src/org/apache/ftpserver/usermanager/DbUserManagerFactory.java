package org.apache.ftpserver.usermanager;

import javax.sql.DataSource;
import org.apache.ftpserver.FtpServerConfigurationException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.impl.DbUserManager;

public class DbUserManagerFactory implements UserManagerFactory {

    private String adminName = "admin";

    private String insertUserStmt;

    private String updateUserStmt;

    private String deleteUserStmt;

    private String selectUserStmt;

    private String selectAllStmt;

    private String isAdminStmt;

    private String authenticateStmt;

    private DataSource dataSource;

    private PasswordEncryptor passwordEncryptor = new Md5PasswordEncryptor();

    public UserManager createUserManager() {
        if (dataSource == null) {
            throw new FtpServerConfigurationException("Required data source not provided");
        }
        if (insertUserStmt == null) {
            throw new FtpServerConfigurationException("Required insert user SQL statement not provided");
        }
        if (updateUserStmt == null) {
            throw new FtpServerConfigurationException("Required update user SQL statement not provided");
        }
        if (deleteUserStmt == null) {
            throw new FtpServerConfigurationException("Required delete user SQL statement not provided");
        }
        if (selectUserStmt == null) {
            throw new FtpServerConfigurationException("Required select user SQL statement not provided");
        }
        if (selectAllStmt == null) {
            throw new FtpServerConfigurationException("Required select all users SQL statement not provided");
        }
        if (isAdminStmt == null) {
            throw new FtpServerConfigurationException("Required is admin user SQL statement not provided");
        }
        if (authenticateStmt == null) {
            throw new FtpServerConfigurationException("Required authenticate user SQL statement not provided");
        }
        return new DbUserManager(dataSource, selectAllStmt, selectUserStmt, insertUserStmt, updateUserStmt, deleteUserStmt, authenticateStmt, isAdminStmt, passwordEncryptor, adminName);
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
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

    public PasswordEncryptor getPasswordEncryptor() {
        return passwordEncryptor;
    }

    public void setPasswordEncryptor(PasswordEncryptor passwordEncryptor) {
        this.passwordEncryptor = passwordEncryptor;
    }
}
