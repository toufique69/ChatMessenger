/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messengerclient1;

/**
 *
 * @author Toufique Hasan
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class SQLServerConnect {
    /**
 * It's a method to connect SQL server database, which returns Connection, 
 * if connected.
 * <p>
 * This method always returns immediately, whether or not the 
 * Connection exists. When Connection is successful, it returns
 * connection. If Connection was not possible, it returns null. 
 * 
 *
 * @param UserName is database username, for example - default is sa
 * @param ServerPassword is password for the server user
 * @param dataBaseName is database name string to be passed
 * @return      Connection is returned
 * @see         Connection
 */
    public static Connection ConnectDB(String UserName, String ServerPassword, String dataBaseName){
         
         
         String dbURL = "jdbc:sqlserver://localhost:1433;user=" + UserName + ";password=" + ServerPassword +
            ";databaseName=" + dataBaseName + ";integratedSecurity=true;";
         
         try{
            
         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
         Connection con = DriverManager.getConnection(dbURL, "","");
         
         System.out.println("Connected database successfully...");
      
         return con;
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
        
    }
    
    public static void Close(Connection connect)
    {
        try {
            if(connect != null)
                connect.close();
            System.out.println("Disconnected");
        } catch(SQLException e)
        {
            System.out.println(e+"Error");
        }
    }
    
        /**
     *this method is used to search sql query in database
     * @param con the connected connection
     * @param search is the query string needed to search
     * @return returns ResultSet after query completes
     */    
public static ResultSet SearchDB(Connection con, String search){
    if(con != null){
        try{
            PreparedStatement pst = con.prepareStatement(search);
            ResultSet rs=pst.executeQuery();
            return rs;
        }catch(Exception e)
        {
            //JOptionPane.showMessageDialog(null, e);
            return null;
        }
    }
    else
    {
       JOptionPane.showMessageDialog(null, "Server not connected");
       return null;
    }  
  }

    /**
     *this method is used to update/delete/insert data into database,
     * using sql command.
     * @param con the connected connection
     * @param update is the sql command needed to update, delete or insert
     * @return returns boolean. if Updating was possible, true, if any error occurred, false.
     */ 

public static boolean UpdateDB(Connection con, String update){
    if(con != null){
    try{
        Statement stmt=con.createStatement();
        stmt.executeUpdate(update);
     //   System.out.println(success);
        return true;
    }catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, "Unable to Insert Data");
            return false;
        }
    }
    else{
        JOptionPane.showMessageDialog(null, "Server not connected");
        return false;
    }
}
}
