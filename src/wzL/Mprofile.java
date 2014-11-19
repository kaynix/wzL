/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wzL;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kaynix
 */
public class Mprofile {
    
    /*if (strncmp(pFileData, "WZ.STA.v3", 9) != 0)
		{
			return false; // wrong version or not a stats file
		}

		char identity[1001];
		identity[0] = '\0';
		sscanf(pFileData, "WZ.STA.v3\n%u %u %u %u %u\n%1000[A-Za-z0-9+/=]",
		       &st->wins, &st->losses, &st->totalKills, &st->totalScore, &st->played, identity);
		free(pFileData);
		if (identity[0] != '\0')
		{
			st->identity.fromBytes(base64Decode(identity), EcKey::Private);
		}*/
    int wins;
    int losses;
    int totalKills;
    int totalScore;
    int playedGames;
    String playerName;
    final String version = "WZ.STA.v3";
    void readProfile(String playerName) throws IOException{
        WzFiles obj = new WzFiles();
        File file = new File(obj.wzconfigpath+"multiplay/players/"+playerName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            DataInputStream ds = new DataInputStream(new FileInputStream(file));
            ds.skipBytes(10);
            System.out.print((char)ds.readByte());ds.skipBytes(1);System.out.print((char)ds.readByte());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Mprofile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
/*************************************************************************
 
 
 
 PLAYERSTATS stat = getMultiStats(j);
		if(stat.wins + stat.losses < 5)
		{
			iV_DrawImage(FrontImages, IMAGE_MEDAL_DUMMY, x + 4, y + 13);
		}
		else
		{
			stat = getMultiStats(j);

			// star 1 total droid kills
			eval = stat.totalKills;
			if(eval >600)
			{
				iV_DrawImage(FrontImages, IMAGE_MULTIRANK1, x + 4, y + 3);
			}
			else if(eval >300)
			{
				iV_DrawImage(FrontImages, IMAGE_MULTIRANK2, x + 4, y + 3);
			}
			else if(eval >150)
			{
				iV_DrawImage(FrontImages, IMAGE_MULTIRANK3, x + 4, y + 3);
			}

			// star 2 games played (Cannot use stat.played, since that's just the number of times the player exited via the game menu, not the number of games played.)
			eval = stat.wins + stat.losses;
			if(eval >200)
			{
				iV_DrawImage(FrontImages, IMAGE_MULTIRANK1, x + 4, y + 13);
			}
			else if(eval >100)
			{
				iV_DrawImage(FrontImages, IMAGE_MULTIRANK2, x + 4, y + 13);
			}
			else if(eval >50)
			{
				iV_DrawImage(FrontImages, IMAGE_MULTIRANK3, x + 4, y + 13);
			}

			// star 3 games won.
			eval = stat.wins;
			if(eval >80)
			{
				iV_DrawImage(FrontImages, IMAGE_MULTIRANK1, x + 4, y + 23);
			}
			else if(eval >40)
			{
				iV_DrawImage(FrontImages, IMAGE_MULTIRANK2, x + 4, y + 23);
			}
			else if(eval >10)
			{
				iV_DrawImage(FrontImages, IMAGE_MULTIRANK3, x + 4, y + 23);
			}

			// medals.
			if ((stat.wins >= 6) && (stat.wins > (2 * stat.losses))) // bronze requirement.
			{
				if ((stat.wins >= 12) && (stat.wins > (4 * stat.losses))) // silver requirement.
				{
					if ((stat.wins >= 24) && (stat.wins > (8 * stat.losses))) // gold requirement
					{
						iV_DrawImage(FrontImages, IMAGE_MEDAL_GOLD, x + 16, y + 11);
					}
					else
					{
						iV_DrawImage(FrontImages, IMAGE_MEDAL_SILVER, x + 16, y + 11);
					}
				}
				else
				{
					iV_DrawImage(FrontImages, IMAGE_MEDAL_BRONZE, x + 16, y + 11);
				}
			}
		}
 
 
 
*************************************************************************/