/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imclient;
	 
/**
 *
 * @author Akhil
 */
public class IMClient {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception{
                String username = "test_user1";
	        String password = "test_pass1";
	        XmppManager xmppManager = new XmppManager("akhil-pc", 5222);
	        
	        xmppManager.init();
	        xmppManager.performLogin(username, password);
	        xmppManager.setStatus(true, "Hello everyone");
	         
	        String buddyJID = "test_user2@akhil-pc";
	        String buddyName = "test_user2";
	        xmppManager.createEntry(buddyJID, buddyName);
	         
	        xmppManager.sendMessage("Hello mate", "test_user2@akhil-pc");
	         
	        boolean isRunning = true;
	         
	        xmppManager.destroy();    
    }
}
