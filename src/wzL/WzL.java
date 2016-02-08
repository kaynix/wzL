/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wzL;

import javax.swing.JFileChooser;
import java.util.prefs.*;
/**
 *
 * @author kaynix
 */
public class WzL {
    
  //  static String strtosend;
    
      
    public static void main(String[] args) throws Exception {
       
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
              //  System.out.println(info.getName());
              //  System.out.println(System.getProperty("user.dir"));
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NJFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        /*********Locate config folder and wz executable file******************/
        Preferences prefs = Preferences.userNodeForPackage(WzL.class);
        
        if (prefs.keys().length == 0) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setFileHidingEnabled(false);
            fc.setMultiSelectionEnabled(false);
            fc.setDialogTitle("Set Warzone's Config Folder");
            fc.showOpenDialog(fc);
            
            System.out.println("Config folder is : " +fc.getSelectedFile().getPath());
            WzFiles.wzconfigpath = fc.getSelectedFile().getPath() + "/";

            prefs.put("wzconfigpath", WzFiles.wzconfigpath);
            fc.setDialogTitle("Find Warzone's executable file");
            fc.setFileHidingEnabled(true);
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.showOpenDialog(fc);
            System.out.println("Game exe is: " + fc.getSelectedFile().getPath());
            WzFiles.wzapath = fc.getSelectedFile().getPath();
            prefs.put("wzapath", WzFiles.wzapath);
            prefs.flush();
           // prefs.sync();
        } else {
        WzFiles.wzconfigpath = prefs.get("wzconfigpath", null);
        WzFiles.wzapath = prefs.get("wzapath", null);
        }
        /**********And Set Preferences******************************************/
        
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            NJFrm win = new NJFrm();
            @Override
            public void run() {
                win.setVisible(true);
               
            }
        });
    }
}
