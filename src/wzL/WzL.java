/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wzL;
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
        //draw window
      //  final NJFrm win = new NJFrm();
        java.awt.EventQueue.invokeLater(new Runnable() {
            NJFrm win = new NJFrm();
            @Override
            public void run() {
                win.setVisible(true);
               
            }
        });
    }
}
