/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imclient;

import java.sql.*;

/**
 * Manages MySQL database connection and interaction
 * 
 * @author Akhil
 */
public class DBManager {
    private static Connection connection;
    
    /**
     * Connect to the MySQL database using the provided credentials
     * 
     * @param database  name of the database to use
     * @param name      username to use for database connection
     * @param password  password to use for database connection
     */
    public static void databaseConnect(String database, String name, String password){
        try{  
            Class.forName("com.mysql.jdbc.Driver");  
            connection = DriverManager.getConnection(  
                    "jdbc:mysql://akhil-pc:3306/"+database, name, password);
        }
        catch(ClassNotFoundException | SQLException e){ 
            System.out.println(e);
        }  
    }
    
    /**
     * Retrieves messages stored in the database. Only messages between the two
     * required users will be retrieved.
     * 
     * @param user1 Username of the current IMClient user
     * @param user2 Username of the any other user
     * @return
     * @throws SQLException 
     */
    public static ResultSet getMessages(String user1, String user2) throws SQLException{
        PreparedStatement stmt = connection.prepareStatement(
                "select sender, body, timestamp from messages "
                + "where (sender=? and receiver=?) "
                + "or (sender=? and receiver=?)");
        stmt.setString(1, user1);
        stmt.setString(2, user2);
        stmt.setString(3, user2);
        stmt.setString(4, user1);
        ResultSet rs = stmt.executeQuery();
        return rs;
    }
    
    /**
     * Stores the message in the database for future retrieval.
     * <p>
     * Useful when the other user is not currently online and will view the
     * messages at a later time.
     * 
     * @param sender
     * @param receiver
     * @param body
     * @throws SQLException 
     */
    public static void storeMessage(String sender, String receiver, String body) throws SQLException{
        PreparedStatement stmt = connection.prepareStatement(
                "insert into messages(sender, receiver, body) "
                + "values (?, ?, ?);");
        stmt.setString(1, sender);
        stmt.setString(2, receiver);
        stmt.setString(3, body);
        stmt.executeUpdate();
    }
    
    /**
     * Close the database connection
     * 
     * @throws SQLException 
     */
    public static void closeConnection() throws SQLException{
        connection.close();
    }
}
