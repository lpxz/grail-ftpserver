package org.apache.ftpserver.command.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;

public class OPTS_MLST extends AbstractCommand {

    private static final String[] AVAILABLE_TYPES = { "Size", "Modify", "Type", "Perm" };

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        session.resetState();
        String argument = request.getArgument();
        String listTypes;
        String types[];
        int spIndex = argument.indexOf(' ');
        if (spIndex == -1) {
            types = new String[0];
            listTypes = "";
        } else {
            listTypes = argument.substring(spIndex + 1);
            StringTokenizer st = new StringTokenizer(listTypes, ";");
            types = new String[st.countTokens()];
            for (int i = 0; i < types.length; ++i) {
                types[i] = st.nextToken();
            }
        }
        String[] validatedTypes = validateSelectedTypes(types);
        if (validatedTypes != null) {
            session.setAttribute("MLST.types", validatedTypes);
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_200_COMMAND_OKAY, "OPTS.MLST", listTypes));
        } else {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "OPTS.MLST", listTypes));
        }
    }

    private String[] validateSelectedTypes(final String types[]) {
        if (types == null) {
            return new String[0];
        }
        List<String> selectedTypes = new ArrayList<String>();
        for (int i = 0; i < types.length; ++i) {
            for (int j = 0; j < AVAILABLE_TYPES.length; ++j) {
                if (AVAILABLE_TYPES[j].equalsIgnoreCase(types[i])) {
                    selectedTypes.add(AVAILABLE_TYPES[j]);
                    break;
                }
            }
        }
        return selectedTypes.toArray(new String[0]);
    }
}
