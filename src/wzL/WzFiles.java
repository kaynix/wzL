/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wzL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author kaynix
 */
public class WzFiles {
    static String wzapath;
    static String wzdatadir;
    static String wzconfigpath;
    static String userHome;
    static String pathMods;
    static String pathMaps;
    static String pathAutoLoad;

    /**
     * Default constructor with default configure folders Warzone settings its
     * look to Documents user's folders, like: "/Documents/Warzone 2100 3.1/"
     */
    public WzFiles() {
        userHome = System.getProperty("user.home");
        wzdatadir = System.getProperty("user.dir");
        if (System.getProperty("os.name").contains("Windows")) { //windows folders

            /*wzconfigpath = userHome + ("/Documents/Warzone 2100 3.1/");
            pathMaps = userHome + ("/Documents/Warzone 2100 3.1/maps/");
            pathMods = userHome + ("/Documents/Warzone 2100 3.1/mods/");
            pathAutoLoad = userHome + ("/Documents/Warzone 2100 3.1/mods/autoload/");
            wzapath = wzdatadir + "/warzone2100.exe";*/
            pathMods = wzconfigpath +"mods/"; 
            pathMaps = wzconfigpath + "maps/";
            pathAutoLoad = wzconfigpath + "mods/autoload/";
        } else { //linux folders
          /*  wzconfigpath = userHome + ("/.warzone2100-3.1/");
            pathMaps = userHome + ("/.warzone2100-3.1/maps/");
            pathMods = userHome + ("/.warzone2100-3.1/mods/");
            pathAutoLoad = userHome + ("/.warzone2100-3.1/mods/autoload/");
            wzapath = wzdatadir + "/warzone2100";*/
            pathMods = wzconfigpath +"mods/"; 
            pathMaps = wzconfigpath + "maps/";
            pathAutoLoad = wzconfigpath + "mods/autoload/";
          //  wzapath = wzdatadir + "/warzone2100";

        }
    }

    /**
     * this constructor for users that have custom wz config/game directories in
     * this case we don't care about in witch OS we are in. Its receives
 location of wz config dir and main game executable dir.Its enough to
 identify other necessary game directories
     *
     * @param wzconfigpath wz config dir location
     * @param wzapath game main executable dir location
     */
    
    public WzFiles(String wzconfigpath, String wzapath){
        userHome = System.getProperty("user.home");
        wzdatadir = System.getProperty("user.dir");
        WzFiles.wzconfigpath = wzconfigpath;
        WzFiles.wzapath = wzapath;
        WzFiles.pathMods = wzconfigpath + "/mods/";
        WzFiles.pathMaps = wzconfigpath + "/maps/";
        WzFiles.pathAutoLoad = pathMods + "autoload/";
    }
    
    /**
     * Its looks for multiplayer profile files that are usually located at
 wzconfigpath+"multiplay/players/" returns String array of dir names
     *
     * @return
     */
    public String[] profilelist(){
        String foldpath = wzconfigpath+"multiplay/players/";
        File dir = new File(foldpath);  
        FilenameFilter onlyWz = new OnlyExt("sta");
        File[] files = dir.listFiles(onlyWz);
        List<String> ls = new ArrayList<String>();
        String[] rezult = new String[files.length];
        try {
            for (File file : files) {
                // rezult[fileInList] = files[fileInList].getName();
                ls.add(file.getName());
            }
        } catch (Exception e) {
            System.out.println("Error === this are no MPprofiles files in wz flder");
        }
        rezult = ls.toArray(new String[0]);
        // f*cking spinner requires non null or non empty list
        if(rezult.length==0 || rezult==null) return new String[]{"empty"};
        return rezult;
    } 
    
    /**
     * Returns String array of map dir names. !!With hash endings, use
     * removeHashFileEnds to remove for better looking names to fit in jList
     *
     * @return String array of map dir names
     */
    public String[] maplist(){
        String foldpath = pathMaps;
    //   System.out.println("User Home Path: " + System.getenv("UserProfile"));
        File file = new File(foldpath);   //%USERPROFILE%
        if (!file.exists()) {
            return null;
        }
        FilenameFilter onlyWz = new OnlyExt("wz");
        File[] files = file.listFiles(onlyWz);
        String[] list= new String[files.length];
        for (int fileInList = 0; fileInList < files.length; fileInList++)
            {
             list[fileInList] = files[fileInList].getName();
            }
        return list;
    } 
       
    String [] removeHashFileEnds(String [] list){
        for(int i=0;i<list.length;i++){
            if(list[i].lastIndexOf('-')<=3)
                continue;
            list[i]=list[i].substring(0, list[i].lastIndexOf('-'));
        }
     return list;       
    }
    
    String addHashFileEnd(String mapname) {
        String[] mls = maplist();
        for (String ml : mls) 
            if (ml.startsWith(mapname)) 
                return ml; 
        return null;
    }
    
    /**
     * Returns String array of mod dir names
     *
     * @param autoload set true if you want to include /autoload Wz folder to be
     * scanned for available mods, if not only files in /Mods folder will be
     * added
     * @return String array of mod list dir names
     */
    public String[] modlist(boolean autoload){
       String foldpath = pathMods;
        mkRmALinModsfold(foldpath);
     //  System.out.println("User Home Path: " + System.getenv("UserProfile"));
      //  File dir = new File(foldpath);   //%USERPROFILE%
        FilenameFilter onlyWz = new OnlyExt("wz");
        if(autoload) foldpath=pathAutoLoad;
        File[] filesInMods = new File(foldpath).listFiles(onlyWz);
           
   //     File[] filesInAutoload = new File(foldpath).listFiles(onlyWz);
        String[] list= new String[filesInMods.length]; // +filesInAutoload.length if uncomment last *for* loop
        
        for (int i = 0; i < filesInMods.length; i++)   
             list[i] = filesInMods[i].getName();
        /*for (int i = 0; i < filesInAutoload.length; i++)   
             list[filesInMods.length+i] = filesInAutoload[i].getName();*/

        return list;
    }
    
    void mkRmALinModsfold(String pathMods){
        File theMods = new File(pathMods);
        File theDir = new File(pathMods+ ".removed");
        File theAutoloadDir = new File(pathMods+ "autoload");

  // if the directory does not exist, create it
        if(!theMods.exists()){
            System.out.println("Can't see Mods folder");
            try {
                theMods.mkdir();
            } catch (SecurityException se) {
                //handle it
                System.out.println("Error: Couldn't creat Mods directory. Run as Administrator");
            }
        }
        
  if (!theDir.exists()) {
    System.out.println("Making tmp directory: " + pathMods + ".removed");
    boolean result = false;

    try{
        theDir.mkdir();
        result = true;
     } catch(SecurityException se){
        //handle it
         System.out.println("Error: Couldn't creat a tmp directory. Run as Administrator");
     }        
     if(result) {    
       System.out.println("DIR '.removed' - created");  
     }
  }
  if (!theAutoloadDir.exists()) {
      theAutoloadDir.mkdir();
      System.out.println("DIR 'autoload' - created"); 
  }
    }
    void setWzConfig(String key, String val){
        try{
      Properties p = new Properties();
      p.load(new FileInputStream(wzconfigpath+"/config"));
      //System.out.println("masterserver_name = " + p.getProperty("mapHash"));
     // p.list(System.out);
      p.setProperty(key, val);
      p.store(new FileOutputStream(wzconfigpath+"/config"), "/* Config file updated with WzLauncher*/");
      }
    catch (Exception e) {
      System.out.println(e);
      }
}
    
}

class OnlyExt implements FilenameFilter {
String ext;
public OnlyExt(String ext) {
this.ext = "." + ext;
}
@Override
public boolean accept(File dir, String name) {
return name.endsWith(ext);
}
} 
