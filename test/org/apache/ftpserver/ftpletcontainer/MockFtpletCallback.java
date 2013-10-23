package org.apache.ftpserver.ftpletcontainer;

import java.io.IOException;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;

public class MockFtpletCallback extends DefaultFtplet {

    public static FtpletResult returnValue;

    public void destroy() {
    }

    public void init(FtpletContext ftpletContext) throws FtpException {
    }

    public FtpletResult onAppendEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onAppendStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onConnect(FtpSession session) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onDeleteEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onDeleteStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onDisconnect(FtpSession session) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onDownloadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onDownloadStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onLogin(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onMkdirEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onMkdirStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onRenameEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onRenameStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onRmdirEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onRmdirStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onSite(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onUploadStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onUploadUniqueEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }

    public FtpletResult onUploadUniqueStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return returnValue;
    }
}
