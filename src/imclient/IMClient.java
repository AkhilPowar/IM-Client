/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imclient;
	 
/**
 *
 * @author Akhil
 * @author Praveen
 * @author Prafull
 */
public class IMClient {
    
    private final static String HOST_NAME = "akhil-pc";
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception{
        String username = "test_user1";
        String password = "test_pass1"; 
        XmppManager xmppManager = new XmppManager(HOST_NAME, 5222);
        
        xmppManager.init();
        xmppManager.performLogin(username, password);
        xmppManager.setStatus(true, "Hello everyone");
	
        String buddyName = "test_user2";
        String buddyJID = buddyName + "@" + HOST_NAME;
        xmppManager.createEntry(buddyJID, buddyName);
        xmppManager.sendMessage("Hello mate", buddyJID);
	while(true){
            
        }
//        xmppManager.destroy();    
    }
}
