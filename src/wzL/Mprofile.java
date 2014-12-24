/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wzL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Multiplayer wz profiles class
 *
 * @author kaynix
 */
public class Mprofile {
		 //      &st->wins, &st->losses, &st->totalKills, &st->totalScore, &st->played, identity);
    int wins;
    int losses;
    int totalKills;
    long totalScore;
    int playedGames;
    String playerName;
    final String version = "WZ.STA.v3";
    
    
    Rank[] calcRank(){
        Rank[] rankmask = new Rank[4];
        if (wins + losses < 5) { //check is it noob or what ?
            rankmask[0] = Rank.NOOB;
        } else {
            //1st star
            if (totalKills > 600) {
                rankmask[1] = Rank.GOLD;// gold;
            } else if (totalKills > 300) {
                rankmask[1] = Rank.SILVER; // silver;
            } else if (totalKills > 150) {
                rankmask[1] = Rank.BRONZE;// bronze;
            }
        //2nd star  games played (Cannot use stat.played, since that's just 
            //the number of times the player exited via the game menu, not the number of games played.)
            if (wins + losses > 200) {
                rankmask[2] = Rank.GOLD;// gold2;
            } else if (wins + losses > 100) {
                rankmask[2] = Rank.SILVER;// silver2;
            } else if (wins + losses > 50) {
                rankmask[2] = Rank.BRONZE;// bronze2;
            }
            //3rd star 
            if (wins > 80) {
                rankmask[3] = Rank.GOLD;// gold3;
            } else if (wins > 40) {
                rankmask[3] = Rank.SILVER;// silver3;
            } else if (wins > 10) {
                rankmask[3] = Rank.BRONZE;// bronze3;
            }
            //final MP medal wins:lose ratio
            if ((wins >= 6) && (wins > (2 * losses))) // bronze requirement.
            {
                if ((wins >= 12) && (wins > (4 * losses))) // silver requirement.
                {
                    if ((wins >= 24) && (wins > (8 * losses))) // gold requirement
                    {
                        rankmask[0] = Rank.GOLD;// goldmedal;
                    } else {
                        rankmask[0] = Rank.SILVER;// silvermedal;
                    }
                } else {
                    rankmask[0] = Rank.BRONZE;// bronzemedal;
                }
            }
        }
 //       System.out.println("RankmaskCalc: "+Arrays.deepToString(rankmask));
        return rankmask;
    }
    URL[] getRankIcons(Rank[] rankmask){
        URL[] iconmask = new URL[4];
        if (rankmask[0] == Rank.NOOB) {
            iconmask[0] = getClass().getResource("/wzL/medal_dummy.png");
        } else {
            for (int i=0; i<4;i++) {
                if(rankmask[i]==null) continue;
                switch (rankmask[i]) {
                   /* case NOOB:
                        iconmask[r.ordinal()] = getClass().getResource("/wzL/medal_dummy.png");
                        break;*/
                    case GOLD:
                        iconmask[i] = getClass().getResource("/wzL/multirank1.png");
                        if(i==0) iconmask[i] = getClass().getResource("/wzL/medal_gold.png"); //if its medal iteration
                        break;
                    case SILVER:
                        iconmask[i] = getClass().getResource("/wzL/multirank2.png");
                        if(i==0) iconmask[i] = getClass().getResource("/wzL/medal_silver.png"); //if its medal iteration
                        break;
                    case BRONZE:
                        iconmask[i] = getClass().getResource("/wzL/multirank3.png");
                        if(i==0) iconmask[i] = getClass().getResource("/wzL/medal_bronze.png"); //if its medal iteration
                        break;
                    default:
                        iconmask[i] = null;
                        break;
                }
            }
        }

        return iconmask;
    }
    Mprofile(String playerFile) throws IOException{
        WzFiles obj = new WzFiles(); playerName = playerFile;
        File file = new File(WzFiles.wzconfigpath + "multiplay/players/" + playerName);
        String str = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            if (0 == version.compareTo(br.readLine())) {
                str = br.readLine();
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Mprofile.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringTokenizer ss = new StringTokenizer(str);
        wins = Integer.parseInt(ss.nextToken());
        losses = Integer.parseInt(ss.nextToken());
        totalKills = Integer.parseInt(ss.nextToken());
        totalScore = Long.parseLong(ss.nextToken());
        playedGames = Integer.parseInt(ss.nextToken());
        //int[] bufi= {wins,losses,totalKills,totalScore,playedGames}; 
       // System.out.print(ss.countTokens());
       // while (ss.hasMoreTokens())
        //   bufi[5-ss.countTokens()] = Integer.parseInt(ss.nextToken());    
    }
    
    
}

enum Rank {

    NOOB, GOLD, SILVER, BRONZE;
}
