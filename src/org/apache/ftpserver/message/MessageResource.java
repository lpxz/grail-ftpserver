package org.apache.ftpserver.message;

import java.util.List;
import java.util.Map;

public interface MessageResource {

    List<String> getAvailableLanguages();

    String getMessage(int code, String subId, String language);

    Map<String, String> getMessages(String language);
}
