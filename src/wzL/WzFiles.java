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
    public String[] maplist(){
        String foldpath;
        if (System.getProperty("os.name").contains("Windows")){
       foldpath = ((System.getProperty("user.home"))+("/Documents/Warzone 2100 3.1/maps/"));}
        else {
            foldpath = ((System.getProperty("user.home"))+("/.warzone2100-3.1/maps/")); //linux folderpath
        }
      // foldpath =System.getProperty("user.home")+("/Documents/.warzone2100-3.1/maps/");
       System.out.println(System.getProperty("os.name"));
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
    String [] removeHashFileEnds(String [] list){
        for(int i=0;i<list.length;i++){
            if(list[i].lastIndexOf('-')<=3)
                continue;
            list[i]=list[i].substring(0, list[i].lastIndexOf('-'));
        }
     return list;       
    }
    public String[] modlist(boolean autoload){
       String foldpath;
        if (System.getProperty("os.name").contains("Windows")) {
            foldpath = ((System.getProperty("user.home")) + ("/Documents/Warzone 2100 3.1/mods/"));
        } else {
            foldpath = ((System.getProperty("user.home")) + ("/.warzone2100-3.1/mods/")); //linux folderpath
        }
        makeRemoveAndAutoloadDir(foldpath);
     //  System.out.println("User Home Path: " + System.getenv("UserProfile"));
      //  File file = new File(foldpath);   //%USERPROFILE%
        FilenameFilter onlyWz = new OnlyExt("wz");
        if(autoload) foldpath+="autoload/";
        File[] filesInMods = new File(foldpath).listFiles(onlyWz);
           
        File[] filesInAutoload = new File(foldpath).listFiles(onlyWz);
        String[] list= new String[filesInMods.length]; // +filesInAutoload.length if uncomment last *for* loop
        
        for (int i = 0; i < filesInMods.length; i++)   
             list[i] = filesInMods[i].getName();
        /*for (int i = 0; i < filesInAutoload.length; i++)   
             list[filesInMods.length+i] = filesInAutoload[i].getName();*/

        return list;
    }
    
    void makeRemoveAndAutoloadDir(String path){
        File theMods = new File(path);
        File theDir = new File(path+ ".removed");
        File theAutoloadDir = new File(path+ "autoload");

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
    System.out.println("Making tmp directory: " + path + ".removed");
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
    
    public void findGameExe(){
        
        String foldpath = ((System.getProperty("user.home"))+("/Documents/Warzone 2100 3.1/mods/"));
        
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
