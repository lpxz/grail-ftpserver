package org.apache.ftpserver.clienttests;

public class FeatTest extends ClientTestTemplate {

    public void test() throws Exception {
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        client.sendCommand("FEAT");
        String[] featReplies = client.getReplyString().split("\r\n");
        for (int i = 0; i < featReplies.length; i++) {
            if (i == 0) {
                assertEquals("211-Extensions supported", featReplies[i]);
            } else if (i + 1 == featReplies.length) {
                assertEquals("211 End", featReplies[i]);
            } else {
                assertEquals(' ', featReplies[i].charAt(0));
                assertTrue(featReplies[i].charAt(1) != ' ');
            }
        }
    }
}
