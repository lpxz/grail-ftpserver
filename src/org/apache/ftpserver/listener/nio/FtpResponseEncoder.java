package org.apache.ftpserver.listener.nio;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

public class FtpResponseEncoder extends ProtocolEncoderAdapter {

    private static final CharsetEncoder ENCODER = Charset.forName("UTF-8").newEncoder();

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        String value = message.toString();
        IoBuffer buf = IoBuffer.allocate(value.length()).setAutoExpand(true);
        buf.putString(value, ENCODER);
        buf.flip();
        out.write(buf);
    }
}
