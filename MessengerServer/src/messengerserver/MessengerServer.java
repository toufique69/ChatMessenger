/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messengerserver;

/**
 *
 * @author Toufique Hasan
 */

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
public class MessengerServer {

    public static DataOutputStream outToClient[] = new DataOutputStream[20];
    public static BufferedReader inFromClient[] = new BufferedReader[20];
    public static ServerSocket welcomeSocket;
    public static Socket connectionSocket[] = new Socket[20];
    String ret="";
    String value="";
    
    public static void main(String[] args) throws Exception{
      
    Statement statement;
    PreparedStatement preparedStatement;
    ResultSet resultSet;
    int i=0;
    
    welcomeSocket = new ServerSocket(6782);
    System.out.println(welcomeSocket.isClosed());
       
    for (i = 0;; i++) {
        System.out.println("waiting\n ");
        connectionSocket[i] = welcomeSocket.accept();
            
        inFromClient[i] = new BufferedReader(new InputStreamReader(connectionSocket[i].getInputStream()));
        outToClient[i] = new DataOutputStream(connectionSocket[i].getOutputStream());
        System.out.println("Connected: "+i);
        String logg=inFromClient[i].readLine();
            
            
        try
        {
            String[] usepass=logg.split(":");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection con =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
            PreparedStatement pr=con.prepareStatement("select*from UserLogin where Username='"+usepass[1]+"'"+"and Password='"+usepass[2]+"'");
            ResultSet res=pr.executeQuery();
                
                
            if(res.next())
            {
                outToClient[i].writeBytes("Match"+'\n');
                Connection conn =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                PreparedStatement prr=conn.prepareStatement("insert into Online(username,loopid) values('"+usepass[1]+"','"+i+"')");
                prr.execute();
            }
            else
            {
                outToClient[i].writeBytes("No Match"+'\n');
                connectionSocket[i]=null;
                outToClient[i]=null;
                inFromClient[i]=null;
                if(i==0)
                {
                    i=0;
                }
                else
                {
                    i=i-1;
                }      
            }       
        }
        catch(Exception e)
        {
            System.out.println(e);
            
        }
        SThread s=new SThread(inFromClient[i], outToClient[i], i);
            
        s.start();
        s.join();
        System.out.println("Disconnected!!!");
            
    }
    }
}

class SThread extends Thread {

    BufferedReader inFromClient;
    DataOutputStream outToClient;
    String clientSentence;
    int srcid,z;
    String username;

    public SThread(BufferedReader in, DataOutputStream out, int a) 
    {
        inFromClient = in;
        outToClient = out;
        srcid = a;   
    }
    @Override
    public void run() 
    {
        int count=0;
        while(count<18000)
        {
            if(count==17999)
            {
                try
                {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                Connection con =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                PreparedStatement pr=con.prepareStatement("update Online set flag='"+0+"' where username='"+username+"'"+"and loopid='"+srcid+"'");
                pr.executeUpdate();
                break;
                }
                catch(Exception ex)
                {
                    System.out.println(ex);
                }
            }
            else
            {
            try 
            {
                count++;
                System.out.println(count);
                
                clientSentence = inFromClient.readLine();
                System.out.println(clientSentence); 
                
                if(clientSentence.contains("Notification"))
                {
                    try
                    {
                        String value = null;
                        String[] notif=clientSentence.split(":");
                        username=notif[1];
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        Connection con =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                        PreparedStatement pr=con.prepareStatement("select COUNT(*) as count from Notification where ToClint='"+notif[1]+"'"+"and Flag='"+1+"'");
                        ResultSet res=pr.executeQuery();
                    
                        if(res.next())
                        {
                            int co=res.getInt("count");
                            value = String.valueOf(co);
                        }
                        outToClient.writeBytes(value+'\n');
                    }
                        catch(Exception e)
                        {
            
                        }
                }
                else if(clientSentence.contains("Accept"))
                {
                    try
                    {
                        String sendreq="";
                        String[] accept=clientSentence.split(":");
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        Connection con =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                        PreparedStatement pr=con.prepareStatement("select * from Notification where ToClint='"+accept[1]+"'"+"and Flag='"+1+"'");
                        ResultSet res=pr.executeQuery();
                        
                        if(res.next())
                        {
                            sendreq=res.getString("FromClint");
                            System.out.println(sendreq);
                        }
                        outToClient.writeBytes(sendreq+'\n');   
                    }
                    catch(Exception e)
                    {
                        
                    }
                    
                }
                
                else if(clientSentence.contains("AddFriend"))
                {
                    try
                    {
                        String[] request=clientSentence.split(":");
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        Connection con =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                        PreparedStatement pr=con.prepareStatement("update Notification set Flag='"+0+"' where FromClint='"+request[2]+"'"+"and ToClint='"+request[1]+"'");
                        pr.executeUpdate();
                    
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        Connection conn =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                        PreparedStatement prr=conn.prepareStatement("insert into UserInfo(username,friendname) values('"+request[1]+"','"+request[2]+"')");
                        PreparedStatement prrr=conn.prepareStatement("insert into UserInfo(username,friendname) values('"+request[2]+"','"+request[1]+"')");

                        prr.execute();
                        prrr.execute(); 
                    }
                    catch(Exception ex)
                    {
                        System.out.println(ex);
                    }
                }
                else if(clientSentence.contains("RejectFriend"))
                {
                    try
                    {
                        
                    }
                    catch(Exception ex)
                    {
                        System.out.println(ex);
                    }
                }
                
                else if(clientSentence.contains("Search"))
                {
                    try
                    {
                        String[] search=clientSentence.split(":");
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        Connection con =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                        PreparedStatement pr=con.prepareStatement("select * from UserInfo where username='"+search[1]+"'"+"and friendname='"+search[2]+"'");
                        ResultSet res=pr.executeQuery();
                        if(res.next())
                        {
                            outToClient.writeBytes("Exist"+'\n');
                        }
                        else
                        {
                            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                            Connection conn =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                            PreparedStatement prr=conn.prepareStatement("select * from UserLogin where Username='"+search[2]+"' except select * from UserLogin where Username='"+search[1]+"'");
                            ResultSet ress=prr.executeQuery();
                            if(ress.next())
                            {
                                outToClient.writeBytes("Found"+'\n');
                            }
                            else
                            {
                                outToClient.writeBytes("Not Found"+'\n');
                            }
                            
                        }
                    }
                    catch(Exception ex)
                    {
                        System.out.println(ex);
                    }
                }
                else if(clientSentence.contains("SendRequest"))
                {
                    try
                    {
                        String[] sendreq=clientSentence.split(":");
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        Connection con =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                        PreparedStatement prr=con.prepareStatement("insert into Notification(FromClint,ToClint) values('"+sendreq[1]+"','"+sendreq[2]+"')");
                        prr.execute();
                        outToClient.writeBytes("ok"+'\n');
                    }
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }
                }
                else if(clientSentence.contains("Friend"))
                {
                    try
                    {
                        String value=null;
                        String[] name=new String[5];
                        String[] friend=clientSentence.split(":");
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        Connection con =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                        PreparedStatement pr=con.prepareStatement("select COUNT(*) as count from UserInfo where username='"+friend[1]+"'");
                        ResultSet res=pr.executeQuery();
                
                        if(res.next())
                        {
                            int co=res.getInt("count");
                            value = String.valueOf(co);
                        }
                        outToClient.writeBytes(value+'\n');
                        
                        Connection conn =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                        PreparedStatement prr=con.prepareStatement("select * from UserInfo where username='"+friend[1]+"'");
                        ResultSet ress=prr.executeQuery();
                        
                        while(ress.next())
                        {
                            outToClient.writeBytes(ress.getString("friendname")+'\n');
                        } 
                    }
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }
                }
                else if(clientSentence.contains("msg_notification"))
                {
                    try
                    {
                        String[] msg_notif=clientSentence.split(":");
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        Connection con =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                        PreparedStatement prr=con.prepareStatement("insert into Message(FromClint,ToClint) values('"+msg_notif[1]+"','"+msg_notif[2]+"')");
                        prr.execute(); 
                    }
                    catch(Exception ex)
                    {
                        System.out.println(ex);
                    }
                }
                else if(clientSentence.contains("msg_count"))
                {
                    try
                    {
                        String value = null;
                        String[] msg_co=clientSentence.split(":");
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        Connection con =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                        PreparedStatement pr=con.prepareStatement("select COUNT(*) as count from Message where ToClint='"+msg_co[1]+"'"+"and flag='"+1+"'");
                        ResultSet res=pr.executeQuery();
                        
                        if(res.next())
                        {
                            int co=res.getInt("count");
                            value = String.valueOf(co);
                            outToClient.writeBytes(value+'\n');
                            
                            Connection conn =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                            PreparedStatement prr=conn.prepareStatement("select * from Message where ToClint='"+msg_co[1]+"'"+"and flag='"+1+"'");
                            ResultSet ress=prr.executeQuery();
                            
                            if(ress.next())
                            {
                                String name=ress.getString("FromClint");
                                outToClient.writeBytes(name+'\n'); 
                            }
                        }
                    }
                        catch(Exception e)
                        {
                                    System.out.println(e);
                        }
                }
                else if(clientSentence.contains("Msg_Flag_Down"))
                {
                    try
                    {
                        String[] down=clientSentence.split(":");
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        Connection con =DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ChatMessenger;user=sa;password=123456789;");
                        PreparedStatement pr=con.prepareStatement("update Message set flag='"+0+"' where FromClint='"+down[2]+"'"+"and ToClint='"+down[1]+"'");
                        pr.executeUpdate();   
                    }
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }
                }
                        
                else if(count==17999)
                {
                    System.out.println("chatserver.SThread.run()");
                }
            } 
            
            catch (Exception e) 
            {
                
            }
        }      
    }
}
}