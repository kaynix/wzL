/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wzL;

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
    
    
}
