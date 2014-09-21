/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wzL;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kaynix
 */
public class HGame {
    int gamescount=0;  //available 
    /*lobby protocol order*/
    int GAMESTRUCT_VERSION;
    char [] gamename; //64bytes
    int dwSize; //size of hostname
    int dwFlags; //unused
    char [] hostIP; //40bytes - IP Adress of the host (NOT IN USE 2.3/master!)
    int dwMaxPlayers; // max players
    int dwCurrentPlayers; //current players
    int [] dwUserFlags; //[4]array unused
    char [] secondaryHosts; //40bytes [2]array Additional IP Address of the host (NOT IN USE 2.3/master!)
    char [] extra; // unused 159bytes
    char [] mapname;// 40bytes
    char [] hostname; //host player name
    char [] versionstring; //64byte GameVersion as a string
    char [] modlist; // 255bytes List of mods (i think "," seperated)
    int game_version_major;
    int game_version_minor;
    int privateGame; //(bool) Private game?
    int pureGame; //(bool) any mods?
    int mods; // count of mods
    int gameId; //Game ID generated at server side
    int future1; //unused
    int future2; //unused
    int future3; //unused
    /*lobby protoloc END*/

    public HGame() {
        gamename = new char[64];
        hostIP = new char[40];
        secondaryHosts = new char[40];
        extra = new char[159];
        mapname = new char[40];
        hostname = new char[40];
        versionstring = new char[64];
        modlist = new char[255];
    }
    
    String getGamename(){
        String srt = new String(gamename);
        return srt.trim();
    }
    String getHostIP(){
        String srt = new String(hostIP);
        return srt.trim();
    }
    String getMapname(){
        String srt = new String(mapname);
        return srt.trim();
    }
    String getHostname(){
        String srt = new String(hostname);
        return srt.trim();
    }
    String getVersionstring(){
        String srt = new String(versionstring);
        return srt.trim();
    }
    String getModlist(){
        String srt = new String(modlist);
        return srt.trim();
    }
    
    String getListstringline(){
        String srt = gamescount + " " + getGamename() + " ";
        return srt;
    }
    String getGamenameUTF(){
        byte [] buf = new byte[gamename.length];
        for(int i=0;i<gamename.length;i++)
            buf[i]=(byte)gamename[i];
        
        String srt=null;
        try {
            srt = new String(buf,"UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return srt.trim();
    }
    String getHostnameUTF(){
        byte[] buf = new byte[hostname.length];
        for (int i = 0; i < hostname.length; i++) {
            buf[i] = (byte) hostname[i];
        }
        String srt = null;
        try {
            srt = new String(buf, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return srt.trim();
    }
    
    

}
