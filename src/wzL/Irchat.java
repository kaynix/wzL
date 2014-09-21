/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wzL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kaynix
 */
public class Irchat {
    public String line;
    
    Irchat(){
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                
            
        try {
            String server = "irc.freenode.net";
            String nick = "simple_bot";
            String login = "simple_bot";

            // The channel which the bot will join.
            String channel = "#irchacks";

            Socket socket = new Socket(server, 6667);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Log on to the server.
            writer.write("NICK " + nick + "\r\n");
            writer.write("USER " + login + " 8 * : Java IRC Bot\r\n");
            writer.flush();
           
            line = null;
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("004") >= 0) {
                    // We are now logged in.
                    break;
                } else if (line.indexOf("433") >= 0) {
                    System.out.println("Nickname is already in use.");
                    return;
                }
            }
            line = null;
            // Join the channel.
            writer.write("JOIN " + channel + "\r\n");
            writer.flush();


            // Keep reading lines from the server.
            while ((line = reader.readLine()) != null) {
                if (line.toUpperCase().startsWith("PING ")) {
                    // We must respond to PINGs to avoid being disconnected.
                    writer.write("PONG " + line.substring(5) + "\r\n");
                    // System.out.println(line);
                    writer.write("PRIVMSG " + channel + " :I got pinged!\r\n");
                    //  writer.write("PRIVMSG termix :R y receiving it ?\r\n");
                    writer.flush();
                    writer.write("NAMES #irchacks");
                    writer.flush();
                } else {
                    // Print the raw line received by the bot.
                    System.out.println(line);
                }
            }
        } catch (UnknownHostException ex) {
                    Logger.getLogger(Irchat.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Irchat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
}
