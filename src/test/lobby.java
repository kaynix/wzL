/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author kaynix
 */
public class lobby {

    static int lobbyStatusCode;
    static int motdlength;

    ArrayList<HGame> checkLobby() {
        ArrayList<HGame> ls = new ArrayList<>();

        try {
            Socket sk = new Socket("lobby.wz2100.net", 9990);
            //  System.out.print("Conected to lobby? =" + sk.isConnected() + "\n");
            OutputStream out = sk.getOutputStream();
            String str = "list";
            byte[] bbuf = new byte[64]; //buffer
            out.write(str.getBytes());
            out.flush();
            InputStream in = sk.getInputStream();
            int by;  //read in streamdata has left
            DataInputStream tm = new DataInputStream(in);
            int avaiablegames = tm.readInt(); //gamelist.length
            if (avaiablegames < 1) {
                return ls;
            }
            for (int j = 0; j < avaiablegames; j++) {
                HGame hgm = new HGame();
                hgm.GAMESTRUCT_VERSION = tm.readInt(); //gamestruct_version
                tm.read(bbuf);      //gamename
                for (int i = 0; i < 64; i++) {
                    hgm.gamename[i] = (char) bbuf[i];
                }
                hgm.dwSize = tm.readInt();   //dwsize
                hgm.dwFlags = tm.readInt();  //dwflgs
                for (int i = 0; i < 40; i++) {
                    hgm.hostIP[i] = (char) tm.readByte(); //hostIP
                }
                hgm.dwMaxPlayers = tm.readInt(); //maxplayers
                hgm.dwCurrentPlayers = tm.readInt(); //currentplayers
                tm.skip(255);
                for (int i = 0; i < 40; i++) {
                    hgm.mapname[i] = (char) tm.readByte(); //mapname
                }
                for (int i = 0; i < 40; i++) {
                    hgm.hostname[i] = (char) tm.readByte(); //hostname
                }
                tm.read(bbuf);
                for (int i = 0; i < 64; i++) {  //versionstring
                    if ((char) bbuf[i] == '\0') {
                        hgm.versionstring[i] = (char) bbuf[i];
                        break;
                    } else {
                        hgm.versionstring[i] = (char) bbuf[i];
                    }
                }
                tm.skip(255);
                tm.skip(36);
                ls.add(hgm);  // adding game entity to List
            }
            //  System.out.print(ls.get(avaiablegames - 1).getGamename());
            System.out.print("bytesleft=" + tm.available());
            lobbyStatusCode = tm.readInt();  // lobbyStatusCode
            motdlength = tm.readInt();  // MOTDLength (message of the day)

            while ((by = in.read()) != -1) {    //ending massage & everything that left
                System.out.print((char) by);
            }
            out.close();
            in.close();
            sk.close();
        } catch (IOException e) {
            System.out.print("May be we are offline?");
        }

        return ls;
    }

   /* public static void main(String[] args) {
          lobby obj = new lobby();
         ArrayList<HGame> ls = new ArrayList<>();
         ls = obj.checkLobby();
         HGame gm = (HGame) ls.get(0);
         System.out.print(gm.getGamename());
    }*/
}
