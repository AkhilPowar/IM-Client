/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imclient;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import org.jxmpp.jid.impl.*;
import org.jxmpp.jid.*;
import org.jxmpp.util.XmppStringUtils;
/**
 * Class to manage interactions between IMClient and XMPP server.
 * 
 * @author Akhil
 */
public class XmppManager {
    
    private static final int PACKET_REPLY_TIMEOUT = 1000; // in milliseconds
    
    private final String server;
    private final int port;
    private final static String DOMAIN = "akhil-pc";
    
    private XMPPTCPConnectionConfiguration config;
    private AbstractXMPPConnection connection;

    private ChatManager chatManager;
    
    public XmppManager(String server, int port) {
        this.server = server;
        this.port = port;
    }
    
    /**
     * Initiate the XMPP connection using the Smack API
     * 
     * @throws org.jivesoftware.smack.XMPPException
     */
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
        chatManager.addIncomingListener(new MyMessageListener());
        
    }
    
    /**
     * Connect to the server using provided credentials
     * 
     * @param username  username for login
     * @param password  plaintext password for login
     * @throws org.jivesoftware.smack.XMPPException
     */
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
    
    /**
     * Set the presence and status of the user
     * 
     * @param available boolean denoting whether user is to be shown as online
     * @param status    a string containing the desired public status
     */
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
    
    /**
     * Returns a set of contacts from the roster
     * 
     * @param user  user whose contacts are to be retrieved
     * @return      contact list
     */
    public Set<String> getContacts(String user){
        Roster roster = Roster.getInstanceFor(connection);
        Set<RosterEntry> rosterSet = roster.getEntries();
        Set<String> rosterStringSet = new HashSet<>();
        for (RosterEntry re : rosterSet) {
            rosterStringSet.add(re.toString());
        }
        return rosterStringSet;
    }
    
    /**
     * Terminate the connection with the server
     * 
     */
    public void destroy() {
        if (connection!=null && connection.isConnected()) {
            connection.disconnect();
        }
    }
    
    /**
     * Sends message to user whose JID is buddyJID
     * 
     * @param message   text to be sent
     * @param buddyJID  JID of the intended recipient
     * @throws java.lang.Exception
     */
    public void sendMessage(String message, String buddyJID) throws Exception {
        System.out.println(String.format("Sending mesage '%1$s' to user %2$s", message, buddyJID));
        Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(buddyJID));
        chat.send(message);
    }
    
    /**
     * Creates a new roster entry for a buddy(another user)
     * 
     * @param user  the username for the entry
     * @param name  nickname of the user
     * @throws java.lang.Exception
     */
    public void createEntry(String user, String name) throws Exception {
        System.out.println(String.format("Creating entry for buddy '%1$s' with name %2$s", user, name));
        Roster roster = Roster.getInstanceFor(connection);
        roster.createEntry(JidCreate.bareFrom(user), name, null);
    }
    
    /**
     * A custom message listener that updates chat display upon receiving a new
     * message
     */
    class MyMessageListener implements IncomingChatMessageListener {
        
        @Override
        public void newIncomingMessage(EntityBareJid jid, Message message, Chat chat) {
            Roster roster = Roster.getInstanceFor(connection);
            if(!roster.contains(jid)){
                try{
                roster.createEntry(jid, XmppStringUtils.parseLocalpart(jid.toString()), null);
                }
                catch(InterruptedException | SmackException.NoResponseException | NotConnectedException | SmackException.NotLoggedInException | XMPPException.XMPPErrorException e){
                    System.out.println(e);
                }
            }
            String from = message.getFrom().toString();
            String body = message.getBody();
            //TODO - Add code to update chat display using updateChat()
        }

    }
    
}
