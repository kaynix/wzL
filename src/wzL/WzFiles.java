/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wzL;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author kaynix
 */
public class WzFiles {
    String wzapath;
    String wzdatadir;
    String wzconfigpath;
    String userHome;
    String pathMods;
    String pathMaps;
    String pathAutoLoad;

    public WzFiles() {
        userHome = System.getProperty("user.home");
        wzdatadir = System.getProperty("user.dir");
        if (System.getProperty("os.name").contains("Windows")) { //windows folders

            wzconfigpath = userHome + ("/Documents/Warzone 2100 3.1/");
            pathMaps = userHome + ("/Documents/Warzone 2100 3.1/maps/");
            pathMods = userHome + ("/Documents/Warzone 2100 3.1/mods/");
            pathAutoLoad = userHome + ("/Documents/Warzone 2100 3.1/mods/autoload/");
            wzapath = (System.getProperty("user.dir")) + "warzone2100.exe";
        } else { //linux folders
            wzconfigpath = userHome + ("/.warzone2100-3.1/");
            pathMaps = userHome + ("/.warzone2100-3.1/maps/");
            pathMods = userHome + ("/.warzone2100-3.1/mods/");
            pathAutoLoad = userHome + ("/.warzone2100-3.1/mods/autoload/");
            wzapath = wzdatadir + "/warzone2100";

        }
    }
    
    public String[] maplist(){
        String foldpath = pathMaps;
    //   System.out.println("User Home Path: " + System.getenv("UserProfile"));
        File file = new File(foldpath);   //%USERPROFILE%
        FilenameFilter onlyWz = new OnlyExt("wz");
        File[] files = file.listFiles(onlyWz);
        String[] list= new String[files.length];
        for (int fileInList = 0; fileInList < files.length; fileInList++)
            {
             list[fileInList] = files[fileInList].getName();
            }
        return list;
    } 
    
    public String[] profilelist(){
        String foldpath = wzconfigpath+"multiplay/players/";
        File file = new File(foldpath);  
        FilenameFilter onlyWz = new OnlyExt("sta");
        File[] files = file.listFiles(onlyWz);
        String[] list= new String[files.length];
        for (int fileInList = 0; fileInList < files.length; fileInList++)
             list[fileInList] = files[fileInList].getName();  
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
    public String[] modlist(boolean autoload){
       String foldpath =pathMods;
        mkRmALinModsfold(foldpath);
     //  System.out.println("User Home Path: " + System.getenv("UserProfile"));
      //  File file = new File(foldpath);   //%USERPROFILE%
        FilenameFilter onlyWz = new OnlyExt("wz");
        if(autoload) foldpath=pathAutoLoad;
        File[] filesInMods = new File(foldpath).listFiles(onlyWz);
           
        File[] filesInAutoload = new File(foldpath).listFiles(onlyWz);
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
            System.out.println("Don't see Mods folder");
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
