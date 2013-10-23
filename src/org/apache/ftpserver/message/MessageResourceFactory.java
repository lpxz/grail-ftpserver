package org.apache.ftpserver.message;

import java.io.File;
import java.util.List;
import org.apache.ftpserver.message.impl.DefaultMessageResource;

public class MessageResourceFactory {

    private List<String> languages;

    private File customMessageDirectory;

    public MessageResource createMessageResource() {
        return new DefaultMessageResource(languages, customMessageDirectory);
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public File getCustomMessageDirectory() {
        return customMessageDirectory;
    }

    public void setCustomMessageDirectory(File customMessageDirectory) {
        this.customMessageDirectory = customMessageDirectory;
    }
}
