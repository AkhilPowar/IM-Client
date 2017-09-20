/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imclient;

import java.io.IOException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import org.jxmpp.jid.impl.*;
import org.jxmpp.jid.*;
import org.jxmpp.util.*;
/**
 *
 * @author Akhil
 */
public class XmppManager {
    
    private static final int PACKET_REPLY_TIMEOUT = 1000; // millis
    
    private final String server;
    private final int port;
    private final static String DOMAIN = "akhil-pc";
    
    private XMPPTCPConnectionConfiguration config;
    private AbstractXMPPConnection connection;

    private ChatManager chatManager;
    private MessageListener messageListener;
    
    public XmppManager(String server, int port) {
        this.server = server;
        this.port = port;
    }
    
    public void init() throws XMPPException {
            
        System.out.println(String.format("Initializing connection to server %1$s port %2$d", server, port));

        SmackConfiguration.setDefaultReplyTimeout(PACKET_REPLY_TIMEOUT);
        
        try{
        config = XMPPTCPConnectionConfiguration.builder()
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                .setHost(server)
                .setPort(port)
                .setXmppDomain(DOMAIN)
                .setUsernameAndPassword("test_user1", "test_pass1")
                .setCompressionEnabled(false)
                .build();
        
        connection = new XMPPTCPConnection(config);
        connection.connect();
        }
        catch(IOException | InterruptedException | SmackException | XMPPException e){
            System.out.println(e);
        }
        
        System.out.println("Connected: " + connection.isConnected());
        
        chatManager = ChatManager.getInstanceFor(connection);
        messageListener = new MyMessageListener();
        
    }
    
    public void performLogin(String username, String password) throws XMPPException {
        if (connection!=null && connection.isConnected()) {
            try{
                connection.login(username, password);
            }
            catch(IOException | InterruptedException | SmackException | XMPPException e){
                System.out.println(e);
            }
        }
    }

    public void setStatus(boolean available, String status) {
        
        Presence.Type type = available? Type.available: Type.unavailable;
        Presence presence = new Presence(type);
        
        presence.setStatus(status);
        try{
            connection.sendStanza(presence);
        }
        catch(InterruptedException | NotConnectedException e){
            System.out.println(e);
        }
        
    }
    
    public void destroy() {
        if (connection!=null && connection.isConnected()) {
            connection.disconnect();
        }
    }
    
    public void sendMessage(String message, String buddyJID) throws Exception {
        System.out.println(String.format("Sending mesage '%1$s' to user %2$s", message, buddyJID));
        Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(buddyJID));
        chat.send(message);
    }
    
    public void createEntry(String user, String name) throws Exception {
        System.out.println(String.format("Creating entry for buddy '%1$s' with name %2$s", user, name));
        Roster roster = Roster.getInstanceFor(connection);
        roster.createEntry(JidCreate.bareFrom(user), name, null);
    }
    
    class MyMessageListener implements MessageListener {

        @Override
        public void processMessage(Message message) {
            String from = message.getFrom().toString();
            String body = message.getBody();
            System.out.println(String.format("Received message '%1$s' from '2$s'", body, from));
        }

    }
    
}
