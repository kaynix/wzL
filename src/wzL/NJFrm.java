/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wzL;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.zip.ZipException;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author kaynix
 */
public class NJFrm extends javax.swing.JFrame {

    /**
     * Creates new form NJFrm
     */
    static BufferedWriter writer;
    static BufferedReader reader;
    static String[] nameslist;
    private Socket socket;
    private String NICK;
    private final String SERVER = "irc.freenode.net";
    private final int DEFAULT_PORT = 6667;
    private final String CHANNEL = "#warzone2100-games";
    private String line;
    private static DefaultListModel listModel;
    
    static String timestmp(){
        String res;
        Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	//System.out.println( sdf.format(cal.getTime()) );
        res = "[" + sdf.format(cal.getTime()) +"] ";
        return res;
    }
    private void connectToServer() {
        try {
            socket = new Socket(SERVER, DEFAULT_PORT);
            writer = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            new Thread(new Incoming()).start();            
            int random = (int )(Math.random() * 100 + 1);
            NICK = NICK + random;
            writer.write("NICK " + NICK + "\r\n");
            writer.write("USER " + NICK
                    + " 8 * : Kaynix's IRC WZ2100 Launcher\r\n");
            writer.flush();
        } catch (Exception e) {
            System.out.println("Can't connect to IRC server");
            jTextArea1.setText("Can't connect to IRC server");
            jTextArea2.setText("Can't connect to IRC server");
        }
    }


    private class Incoming implements Runnable {
        
        @Override
        public void run() {

        
            try {
              /*  BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));*/
                
             //   DefaultListModel listModel = (DefaultListModel)jList1.getModel();
                
                while ((line = reader.readLine()) != null) {
                    if (line.indexOf("001") >= 0) {
                        writer.write("JOIN " + CHANNEL + "\r\n");
                        writer.flush();
                    }

                    if (line.indexOf("433") >= 0) {
                        System.out.println("Nickname is already in use.");
                        int random = (int) (Math.random() * 100 + 1);
                        NICK = NICK + random;
                    }

                    if (line.startsWith("PING ")) {
                        writer.write("PONG " + line.substring(5) + "\r\n");
                        writer.flush();
                    }
                    if (line.indexOf("353") != -1) { //refreshing names in jLIst

                          listModel.clear();
                        StringTokenizer namestoken = new StringTokenizer(line.substring(line.indexOf(CHANNEL) + 20));  //19 wzchannel
                        nameslist = new String[namestoken.countTokens()]; int i=0;
                        
                        while (namestoken.hasMoreTokens())
                            nameslist[i++] = namestoken.nextToken();
                         
                        javax.swing.SwingUtilities.invokeLater(new Runnable() {  //invoke to Swing thread  it shold be
                            @Override
                            public void run() {
                                for (int i = 0; i < nameslist.length; i++) {
                                    listModel.addElement(nameslist[i]);
                                }
                            }
                        });
                    }
                    if (line.indexOf("332") >= 0) //  channel TOPIC
                        jTextArea1.append(NJFrm.timestmp()+"Done \r\n");
                    Toolkit.getDefaultToolkit().beep();
                    
                    if (line.indexOf("JOIN #warzone2100-games") >= 0){ //  user has Join
                        if(line.substring(1, line.indexOf("!")).compareTo(NICK)!=0)
                        listModel.addElement(line.substring(1, line.indexOf("!")));
                        jTextArea1.append(NJFrm.timestmp()+line.substring(1, line.indexOf("!"))+" has Join\r\n");
                        Toolkit.getDefaultToolkit().beep();
                    }
                    if (line.indexOf("QUIT :") >= 0){ //  quit user
                        
                        jTextArea1.append(NJFrm.timestmp()+line.substring(1, line.indexOf("!"))+"has quit/disconnected.\r\n");
                        listModel.removeElement(line.substring(1, line.indexOf("!")));
                    }
                    if (line.indexOf("!") >= 0 && line.indexOf("PRIVMSG #warzone2100-games") >= 0) {  //"!~"
                                                
                        jTextArea1.append(NJFrm.timestmp()+line.substring(1, line.indexOf("!")) +": " + line.substring(line.indexOf(CHANNEL) + 20)+ "\r\n");
                         //  jTextArea1.append(line + "\r\n");
                        jScrollPane2.getVerticalScrollBar().setValue(jScrollPane2.getVerticalScrollBar().getMaximum());
                        Toolkit.getDefaultToolkit().beep();
                    }
                    jTextArea2.append(line + "\r\n");
                    jScrollPane7.getVerticalScrollBar().setValue(jScrollPane7.getVerticalScrollBar().getMaximum());
                    
                    jTextArea2.setCaretPosition(jTextArea2.getDocument().getLength()); //server log
                    jTextArea1.setCaretPosition(jTextArea1.getDocument().getLength());
                    
                    System.out.println(line);
                }
            } catch (IOException ex) {
                Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    public NJFrm() {
        this.NICK = "wzPLayer";
        
        initComponents();
        connectToServer();
        jTextField2.setText(this.NICK);
        Timer t = new Timer(60000, new ActionListener() { //timer for gamelist refresh 60 sec

            @Override
            public void actionPerformed(ActionEvent ae) {
                hndlr(ae);
            }
        });
        t.start();
        //set Frame wz Icon
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("warzone2100_128x128.png")));
       
    }
    

    public void appendtext(String line) {
        jTextArea1.append(line);
    }

    public String gettex() {
        return jTextField1.getText();
    }

    public synchronized void setchatnames(String[] srt) {
        DefaultListModel listModel = new DefaultListModel();
        jList1.setModel(listModel);
        for (int i = 0; i < srt.length; i++) {
            listModel.addElement(srt[i]);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jTextField2 = new javax.swing.JTextField();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jTextField3 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jCheckBox2 = new javax.swing.JCheckBox();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jSpinner1 = new javax.swing.JSpinner();
        jPanel6 = new javax.swing.JPanel();
        jLstar1 = new javax.swing.JLabel();
        jLstar2 = new javax.swing.JLabel();
        jLstar3 = new javax.swing.JLabel();
        jLmedal = new javax.swing.JLabel();
        jCheckBox3 = new javax.swing.JCheckBox("Fullscreen", true);
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jButton4 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        DefaultListModel ls = new DefaultListModel();
        jList3.setModel(ls);
        wzL.WzFiles ff = new wzL.WzFiles();
        String[] strings = ff.modlist(true);
        for(int i=0;i<strings.length;i++)
        ls.addElement(strings[i]);
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList();
        DefaultListModel ls2 = new DefaultListModel();
        jList4.setModel(ls2);
        wzL.WzFiles ff2 = new wzL.WzFiles();
        String[] strings2 = ff2.modlist(false);
        for(int i=0;i<strings2.length;i++)
        ls2.addElement(strings2[i]);
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Warzone Launcher alpha 0.15e");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        setMinimumSize(new java.awt.Dimension(640, 580));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTabbedPane1.setFont(new java.awt.Font("DejaVu Sans Mono", 1, 12)); // NOI18N

        jButton1.setFont(new java.awt.Font("Droid Sans", 0, 12)); // NOI18N
        jButton1.setLabel("Refresh games list");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hndlr(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "GameName", "Hostname", "Mapname", "version", "hostIP", "Players"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(10);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(12);
        }

        jButton2.setLabel("Join selected Game");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setLabel("Host Game");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTextField1.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12)); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        listModel = new DefaultListModel();
        //listModel.ensureCapacity(100);
        jList1.setModel(listModel);
        jList1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setVerifyInputWhenFocusTarget(false);
        jScrollPane3.setViewportView(jList1);

        jTextField2.setToolTipText("Enter your nick name here");
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
        });

        jTabbedPane2.setPreferredSize(new java.awt.Dimension(164, 89));

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setAutoscrolls(true);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 14)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Connecting...\n");
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 936, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 936, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 262, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Chat", jPanel8);

        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane7.setAutoscrolls(true);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Lucida Console", 0, 14)); // NOI18N
        jTextArea2.setRows(5);
        jTextArea2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane7.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 936, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 936, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 262, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Server Log", jPanel10);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 941, Short.MAX_VALUE)
                .addGap(150, 150, 150))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 504, Short.MAX_VALUE)
                            .addComponent(jButton2)
                            .addGap(182, 182, 182)
                            .addComponent(jButton1))
                        .addComponent(jTextField1))
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(205, 205, 205)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addGap(71, 71, 71))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2)
                        .addComponent(jButton3))
                    .addContainerGap()))
        );

        jTabbedPane1.addTab("Main", jPanel1);

        WzFiles obj = new WzFiles();
        jTextField3.setText(obj.wzapath);
        jTextField3.setEditable(false);
        jTextField3.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12)); // NOI18N

        jLabel3.setText("Game exe Path");
        jLabel3.setToolTipText("Find warzone2100 executable file");

        jButton8.setText("browse...");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("browse...");
        jButton9.setToolTipText("Find warzone's config folder");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jTextField4.setText(obj.wzconfigpath);
        jTextField4.setEditable(false);

        jLabel4.setText("Config dir");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alpha", "Beta", "Gamma", "Tutorial", "Fastplay" }));
        jComboBox1.setEnabled(false);

        jCheckBox1.setText("Start game from");
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                startGameFromHandler(evt);
            }
        });

        jLabel5.setText("Data dir");

        jTextField5.setText(obj.wzdatadir);
        jTextField5.setEditable(false);

        jLabel6.setText("MP profile");

        jLabel7.setText("Mod dir");
        jLabel7.setToolTipText("Find warzone2100 executable file");

        jTextField6.setText(obj.pathMods);
        jTextField6.setEditable(false);

        jCheckBox2.setText("Resolution");

        jTextField7.setText("1600");

        jTextField8.setText("900");

        jSpinner1.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        jSpinner1.setModel(new SpinnerListModel(obj.profilelist()));
        JFormattedTextField tf = ((JSpinner.DefaultEditor) jSpinner1.getEditor()).getTextField();
        tf.setEditable(false);
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                MprofileChanged(evt);
            }
        });

        jPanel6.setBackground(new java.awt.Color(0, 51, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLstar1.setPreferredSize(new java.awt.Dimension(9, 10));

        jLstar2.setPreferredSize(new java.awt.Dimension(9, 10));

        jLstar3.setPreferredSize(new java.awt.Dimension(9, 10));

        jLmedal.setPreferredSize(new java.awt.Dimension(9, 18));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLstar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLstar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLstar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLmedal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(jLstar2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLstar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLstar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLmedal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        jCheckBox3.setText("Fullscreen");

        jButton13.setFont(new java.awt.Font("DejaVu Sans Mono", 1, 12)); // NOI18N
        jButton13.setText("Apply");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setFont(new java.awt.Font("DejaVu Sans Mono", 1, 12)); // NOI18N
        jButton14.setText("RunGame");
        jButton14.setEnabled(false);
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton8, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(348, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBox2)
                                    .addComponent(jCheckBox3))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton13))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCheckBox1)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
                                .addComponent(jButton14))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField6))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox1)
                            .addComponent(jButton14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBox2)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox3))
                    .addComponent(jButton13))
                .addContainerGap(311, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Options", jPanel2);

        DefaultListModel ls2m = new DefaultListModel();
        jList2.setModel(ls2m);
        wzL.WzFiles ff2m = new wzL.WzFiles();
        String[] strings2m = ff2m.removeHashFileEnds(ff2m.maplist());
        for(int i=0;i<strings2m.length;i++)
        ls2m.addElement(strings2m[i]);
        jList2.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12)); // NOI18N
        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList2MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jList2);

        jButton4.setText("del");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jList3.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12)); // NOI18N
        jList3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(jList3);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wzL/up.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wzL/down.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("add map");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel1.setText("MODs");

        jLabel2.setText("Maps");

        jList4.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12)); // NOI18N
        jList4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane6.setViewportView(jList4);

        jLabel9.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12)); // NOI18N
        jLabel9.setText("Activate");

        jLabel10.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12)); // NOI18N
        jLabel10.setText("DeActivete");

        jButton11.setText("add mod");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("del");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview"));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 512, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 512, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton6))
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jButton7)
                                        .addGap(22, 22, 22)
                                        .addComponent(jButton4))
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane4)
                                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton4)
                                    .addComponent(jButton7))
                                .addGap(25, 25, 25))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton5)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jButton6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel10))
                                    .addComponent(jScrollPane6))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton11)
                            .addComponent(jButton12))
                        .addGap(164, 164, 164))
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab("Mods/Maps", jPanel3);

        jLabel11.setText("Something interesting will be here someday");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(884, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(541, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Ladder/Scoreboard/Tournaments", jPanel4);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wzL/wz_faceboook_logo.png"))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Droid Sans", 0, 12)); // NOI18N
        jLabel12.setText("<html> This launcher made by Kaynix for warzone2100 players to make online play more comfortable.<br>\nIf you have any suggestions/improvments or found bug contact me kaynix29@gmail.com or at forum pm to <b>Terminator</b> <br>\n Warzone2100 Launcher version alpha 0.15e++:<br>\n - added profiles<br>\n - added delete mod button<br>\n - added delete map button<br>\n - preparing ladder background<br>\n - improved mods management<br>\n - improved maps management<br>\n - enabled launching game from campaign Alpha,Beta,Gamma<br>\n - enabled fullscreen/resolution changer(direct writing to config file)<br>\n - bug fixes<br>*- add browse buttons for game exe and config folder<br>\n *- fixed issue with game exe and config folder<br>\n *- make use java preferences(location platform dependent)<br>\n **- fixed Java security issues<br>\n **- fixed folder relocation errors, so mods\\maps should work again<br>\n **- fixed profiles empty folder bug<br>\n ++- added map preview in maps\\mod tab<br>\n ++- try to foolproof if player selects wrong folders from 1st time<br>\n ++- added server log chat<br>\n ++- do not shutdown if no Internet connection<br>\n <br><p>run launcher in terminal to see logs.</p> <br>\n <p>For more info visit wz2100.net</p> <br>\n <p>source code: <a href='github.com/kaynix/wzL'>github.com/kaynix/wzL</a></p> </html>"); // NOI18N
        jLabel12.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addContainerGap(137, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(107, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("About", jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void hndlr(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hndlr
        // TODO add your handling code here:
        
        ArrayList<HGame> ls = new lobby().checkLobby();
        int toclear = jTable1.getRowCount();
        for (int k = 0; k < toclear; k++) {
            jTable1.setValueAt("", k, 0);
            jTable1.setValueAt("", k, 1);
            jTable1.setValueAt("", k, 2);
            jTable1.setValueAt("", k, 3);
            jTable1.setValueAt("", k, 4);
            jTable1.setValueAt("", k, 5);
        }

        if (!ls.isEmpty()) { // || ls != null
            for (int k = 0; k < ls.size(); k++) {
                jTable1.setValueAt(ls.get(k).getGamenameUTF(), k, 0);
                jTable1.setValueAt(ls.get(k).getHostnameUTF(), k, 1);
                jTable1.setValueAt(ls.get(k).getMapname(), k, 2);
                jTable1.setValueAt(ls.get(k).getVersionstring(), k, 3);
                jTable1.setValueAt(ls.get(k).getHostIP(), k, 4);
                jTable1.setValueAt(ls.get(k).dwCurrentPlayers + "/" + ls.get(k).dwMaxPlayers + "(" + (ls.get(k).dwMaxPlayers - ls.get(k).dwCurrentPlayers) + ")", k, 5);

            }
        }

    }//GEN-LAST:event_hndlr

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
            Process p;
        try {
            // TODO add your handling code here:
            System.out.println(WzFiles.wzapath + " --join=" + jTable1.getValueAt(jTable1.getSelectedRow(), 4));
            p = Runtime.getRuntime().exec(WzFiles.wzapath + " --join=" + jTable1.getValueAt(jTable1.getSelectedRow(), 4));

        } catch (IOException ex) {
            Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        Process p;
        try {
                p = Runtime.getRuntime().exec(WzFiles.wzapath + " --host");
            

        } catch (IOException ex) {
            Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && jTextField1.getText().length() > 0) {
            try {
                int curtab = jTabbedPane2.getSelectedIndex();
                if (curtab == 0) {
                    writer.write("PRIVMSG #warzone2100-games :" + jTextField1.getText().trim() + "\r\n");
                    writer.flush();
                    jTextArea1.append(NJFrm.timestmp() + NICK + ": " + jTextField1.getText().trim() + "\r\n");
                    jTextField1.setText("");
                }
                if (curtab == 1) {
                    writer.write(jTextField1.getText().trim() + "\r\n");
                    writer.flush();
                    jTextArea2.append("> " + jTextField1.getText().trim() + "\r\n");
                    jTextField1.setText("");
                }
            } catch (IOException ex) {
                Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && jTextField2.getText().length() > 3) {
            try {
                if (NICK.compareToIgnoreCase(jTextField2.getText().trim())!=0) {
                    writer.write("NICK " + jTextField2.getText().trim() + "\r\n");
                    writer.flush();
                    jTextArea1.append(NJFrm.timestmp()+NICK + " >>now is>> " + jTextField2.getText().trim() + "\r\n");
                    NICK = jTextField2.getText();
                }
                writer.write("NAMES " + CHANNEL + "\r\n");
                writer.flush();
            } catch (IOException ex) {
                Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTextField2KeyPressed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        try {
            // TODO add your handling code here:
            System.out.println("irc >> /quit.");
            writer.write("QUIT");
            writer.flush();
            System.out.println("irc >> /quit.");
        } catch (IOException ex) {
            Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
      try {
            // TODO add your handling code here:
            writer.write("QUIT");
            writer.flush();
            System.out.println("irc >> /quit.");
        } catch (IOException ex) {
            Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

    private void MprofileChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_MprofileChanged
        try {
            // TODO add your handling code here:
            Mprofile mp = new Mprofile((String)jSpinner1.getValue());
     //       System.out.println((String)jSpinner1.getValue());
    //        System.out.println(mp.wins+" "+mp.losses+" "+mp.totalKills+" "+mp.totalScore+" ");

            URL[] iconmask = mp.getRankIcons(mp.calcRank());
           // jLstar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wzL/medal_dummy.png")));
            jLmedal.setIcon(null);
            if(iconmask[0]!=null)
            jLmedal.setIcon(new javax.swing.ImageIcon(iconmask[0]));
            
            jLstar1.setIcon(null);
            if(iconmask[1]!=null)
            jLstar1.setIcon(new javax.swing.ImageIcon(iconmask[1]));
            
            jLstar2.setIcon(null);
            if(iconmask[2]!=null)
            jLstar2.setIcon(new javax.swing.ImageIcon(iconmask[2]));
            
            jLstar3.setIcon(null);
            if(iconmask[3]!=null)
            jLstar3.setIcon(new javax.swing.ImageIcon(iconmask[3]));
            
            
         //   for(int i=0;i<4;i++)
         //       System.out.println(iconmask[i]);
            
        } catch (IOException ex) {
            Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_MprofileChanged

    private void startGameFromHandler(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_startGameFromHandler
        // TODO add your handling code here:
        Object obj = evt.getItemSelectable();
        if (obj == jCheckBox1) {
            if (jComboBox1.isEnabled()) {
                jComboBox1.setEnabled(false);
                jButton14.setEnabled(false);
            } else {
                jComboBox1.setEnabled(true);
                jButton14.setEnabled(true);
            }}
    }//GEN-LAST:event_startGameFromHandler

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        WzFiles ff = new WzFiles();
        if (jCheckBox3.isSelected())
            ff.setWzConfig("fullscreen", "true"); else        
            ff.setWzConfig("fullscreen", "false");
        
        if (jCheckBox2.isSelected()) {
            ff.setWzConfig("width", jTextField7.getText().trim());
            ff.setWzConfig("height", jTextField8.getText().trim());
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        Process p; String startfrom = null;  //--game=FASTPLAY --game=TUTORIAL3 --game=CAM_3A --game=CAM_1A --game=CAM_2A
        switch((String)jComboBox1.getSelectedItem()){  
            case "Alpha": startfrom=" --game=CAM_1A"; break;
                case "Beta": startfrom=" --game=CAM_2A"; break;
                    case "Gamma": startfrom=" --game=CAM_3A"; break;
                        case "Tutorial": startfrom=" --game=TUTORIAL3"; break;
                            case "Fastplay": startfrom=" --game=FASTPLAY"; break;
            
        }
        try {
            // TODO add your handling code here:
           p = Runtime.getRuntime().exec(WzFiles.wzapath+startfrom);

        } catch (IOException ex) {
            Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        /*
        * Setting warzone2100 exe by user
        */
        JFileChooser fc = new JFileChooser();
        Preferences prefs = Preferences.userNodeForPackage(WzL.class);
        fc.setDialogTitle("Find Warzone's executable file");
        fc.setMultiSelectionEnabled(false);
        fc.setFileHidingEnabled(true);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.showOpenDialog(fc);
        System.out.println("New wz location is: " +fc.getSelectedFile().getPath());
        WzFiles.wzapath = fc.getSelectedFile().getPath();
        prefs.put("wzapath", WzFiles.wzapath);
        jTextField3.setText(WzFiles.wzapath);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
                JFileChooser fc = new JFileChooser();
        Preferences prefs = Preferences.userNodeForPackage(WzL.class);
        fc.setDialogTitle("Set Warzone's Config Folder");
        fc.setMultiSelectionEnabled(false);
        fc.setFileHidingEnabled(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showOpenDialog(fc);
        System.out.println("New config location is: "+fc.getSelectedFile().getPath());
        WzFiles.wzconfigpath = fc.getSelectedFile().getPath() + "/";
        prefs.put("wzconfigpath", WzFiles.wzconfigpath);
        jTextField4.setText(WzFiles.wzconfigpath);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        String mapname;

        mapname = (String)jList4.getSelectedValue(); //System.out.println(WzFiles.pathMaps+mapname);

        try{
            File mfile = new File(WzFiles.pathMods+mapname);
            System.out.println(mfile.getName());
            if(mfile.exists()) //mfile.delete();
            mfile.renameTo(new File(WzFiles.pathMods+".removed/"+mapname));
            // mfile.renameTo(new File(modDirPath+"autoload/"+mapname));
        } catch (Exception io){
            System.out.println("Error deleting mod file ::Activation error:: \r\n");
        }
        // refreshing GUI lists
        DefaultListModel ls  = (DefaultListModel) jList4.getModel();
        ls.removeElement(mapname);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Warzone map/mod files *.wz","wz"));

        if (evt.getSource() == jButton11) {
            int returnVal = fc.showOpenDialog(NJFrm.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                DefaultListModel ls = (DefaultListModel) jList4.getModel();
                ls.addElement(file.getName());
                System.out.println("Opening: " + file.getName() + ".");

                try {

                    file.renameTo(new File(WzFiles.pathMods + file.getName()));
                } catch (Exception io) {
                    System.out.println("Error moving MOD file :: Adding error ::");
                }

            } else {
                System.out.println("Open command cancelled by user :: Adding MOD error ::");
            }
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Warzone map/mod files *.wz","wz"));

        if (evt.getSource() == jButton7) {
            int returnVal = fc.showOpenDialog(NJFrm.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                DefaultListModel ls = (DefaultListModel) jList2.getModel();
                ls.addElement(file.getName());
                System.out.println("Opening: " + file.getName() + ".");

                try {

                    file.renameTo(new File(WzFiles.pathMaps + file.getName()));
                } catch (Exception io) {
                    System.out.println("Error moving Map file ::Adding error:: \r\n");
                }

            } else {
                System.out.println("Open command cancelled by user :: Adding Map error ::");
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        /*=========MOD DE-ACTIVATION========== */
        String modname;

        /*  if (System.getProperty("os.name").contains("Windows")) {
            modDirPath = ((System.getProperty("user.home")) + ("/Documents/Warzone 2100 3.1/mods/"));
        } else {
            modDirPath = ((System.getProperty("user.home")) + ("/.warzone2100-3.1/mods/")); //linux folderpath
        }*/
        modname = (String)jList3.getSelectedValue(); System.out.println(WzFiles.pathMods+"autoload/"+modname);
        try{
            File mfile = new File(WzFiles.pathMods+"autoload/"+modname);
            mfile.renameTo(new File(WzFiles.pathMods+modname));} catch (Exception io){
            System.out.println("Error moving file ::Deactivation error:: \r\n");
        }

        // refreshing GUI lists
        DefaultListModel ls  = (DefaultListModel) jList3.getModel();
        ls.removeElement(modname);
        ls = (DefaultListModel) jList4.getModel();
        ls.addElement(modname);

    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        String modname;
        /*=========MOD ACTIVATION========== */
        /* if (System.getProperty("os.name").contains("Windows")) {
            modDirPath = (System.getProperty("user.home")) + ("/Documents/Warzone 2100 3.1/mods/");
        } else {
            modDirPath = (System.getProperty("user.home")) + ("/.warzone2100-3.1/mods/"); //linux folderpath
        }*/
        modname = (String)jList4.getSelectedValue(); System.out.println(WzFiles.pathMods+modname);
        try{
            File mfile = new File(WzFiles.pathMods+modname);
            mfile.renameTo(new File(WzFiles.pathMods+"autoload/"+modname));} catch (Exception io){
            System.out.println("Error moving file ::Activation error:: \r\n");
        }
        // refreshing GUI lists
        DefaultListModel ls  = (DefaultListModel) jList4.getModel();
        ls.removeElement(modname);
        ls = (DefaultListModel) jList3.getModel();
        ls.addElement(modname);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // deleting MAP File
        String mapname;
        WzFiles ff = new WzFiles();

        mapname = (String)jList2.getSelectedValue(); //System.out.println(ff.pathMaps+mapname);

        try{
            File mfile = new File(WzFiles.pathMaps+ff.addHashFileEnd(mapname));
            System.out.println(mfile.getName());
            if(mfile.exists()) mfile.delete();
            // mfile.renameTo(new File(modDirPath+"autoload/"+mapname));
        } catch (Exception io){
            System.out.println("Error deleting map file ::Activation error:: \r\n");
        }
        // refreshing GUI lists
        DefaultListModel ls  = (DefaultListModel) jList2.getModel();
        ls.removeElement(mapname);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseClicked
        // TODO add your handling code here:
        WzFiles ff = new WzFiles();

        final String fname = WzFiles.pathMaps + ff.addHashFileEnd((String) jList2.getSelectedValue());
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Graphics g = jPanel9.getGraphics();
                g.clearRect(15, 30, jPanel9.getHeight() - 30, jPanel9.getWidth() - 15);
                jPanel9.revalidate();
                int dots[] = null;
                try {
                    dots = WzFiles.getMapSizeAndHeights(fname);
                } catch (ZipException ex) {
                    g.drawString("Error loading preview, wrong wz file format, try to re-pack it!", 20, 50);
                    Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    g.drawString("Map not found!", 20, 50);
                    Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
                }
                int counter = 0;
                for (int i = 0; i < dots[dots.length - 1]; i++) {
                    for (int j = 0; j < dots[dots.length - 2]; j++) {
                        float color = (dots[counter]) / Float.valueOf("2.55") / 100;
                        g.setColor(Color.getHSBColor(0, 0, color));
                        g.fillRect(15 + j * 2, 30 + i * 2, 2, 2); // 20, 50 - gap from border
                        counter++;
                    }
                }
                jPanel9.paintComponents(g);
            }
        });
    }//GEN-LAST:event_jList2MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JList jList4;
    private javax.swing.JLabel jLmedal;
    private javax.swing.JLabel jLstar1;
    private javax.swing.JLabel jLstar2;
    private javax.swing.JLabel jLstar3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}
