/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.Timer;
import sun.awt.image.ToolkitImage;

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
    private String modDirPath;
    
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            new Thread(new Incoming()).start();            
            int random = (int )(Math.random() * 100 + 1);
            NICK = NICK + random;
            writer.write("NICK " + NICK + "\r\n");
            writer.write("USER " + NICK
                    + " 8 * : Kaynix's IRC chat application\r\n");
            writer.flush();
        } catch (Exception e) {
            System.exit(0);
        }
    }


    private class Incoming implements Runnable {
        
        @Override
        public void run() {

        
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                
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
                    
                    if (line.indexOf("JOIN #warzone2100-games") >= 0){ //  user has Join
                        if(line.substring(1, line.indexOf("!")).compareTo(NICK)!=0)
                        listModel.addElement(line.substring(1, line.indexOf("!")));
                        jTextArea1.append(NJFrm.timestmp()+line.substring(1, line.indexOf("!"))+" has Join\r\n");
                    }
                    if (line.indexOf("QUIT :") >= 0){ //  quit user
                        
                        jTextArea1.append(NJFrm.timestmp()+line.substring(1, line.indexOf("!"))+"has quit/disconnected.\r\n");
                        listModel.removeElement(line.substring(1, line.indexOf("!")));
                    }
                    if (line.indexOf("!") >= 0 && line.indexOf("PRIVMSG #warzone2100-games") >= 0) {  //"!~"
                                                
                        jTextArea1.append(NJFrm.timestmp()+line.substring(1, line.indexOf("!")) +": " + line.substring(line.indexOf(CHANNEL) + 20)+ "\r\n");
                         //  jTextArea1.append(line + "\r\n");
                    }

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
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jTextField2 = new javax.swing.JTextField();
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
        jButton10 = new javax.swing.JButton();
        jCheckBox2 = new javax.swing.JCheckBox();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jButton4 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        DefaultListModel ls = new DefaultListModel();
        jList3.setModel(ls);
        test.WzFiles ff = new test.WzFiles();
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
        test.WzFiles ff2 = new test.WzFiles();
        String[] strings2 = ff.modlist(false);
        for(int i=0;i<strings2.length;i++)
        ls2.addElement(strings2[i]);
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WarzoneOnline pre-alpha 0.14");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        setMinimumSize(new java.awt.Dimension(640, 580));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
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

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 14)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Connecting...\n");
        jScrollPane2.setViewportView(jTextArea1);

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 988, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jScrollPane2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 346, Short.MAX_VALUE)
                            .addComponent(jButton2)
                            .addGap(182, 182, 182)
                            .addComponent(jButton1))
                        .addComponent(jTextField1))
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 454, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
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

        jLabel3.setText("Game Path");
        jLabel3.setToolTipText("Find warzone2100 executable file");

        jButton8.setText("browse...");
        jButton8.setEnabled(false);

        jButton9.setText("config");
        jButton9.setEnabled(false);

        String foldpath = ((System.getProperty("user.home"))+("/Documents/Warzone 2100 3.1/config"));
        jTextField4.setText(foldpath);

        jLabel4.setText("Config dir");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alpha", "Beta", "Gamma", "Tutorial", "Fastplay" }));

        jCheckBox1.setText("Start game from");
        jCheckBox1.setEnabled(false);

        jLabel5.setText("Data dir");

        jLabel6.setText("(Linux only ?)");

        jLabel7.setText("Mod dir");
        jLabel7.setToolTipText("Find warzone2100 executable file");

        foldpath = ((System.getProperty("user.home"))+("/Documents/Warzone 2100 3.1/mods/"));
        jTextField6.setText(foldpath);

        jButton10.setText("browse...");
        jButton10.setEnabled(false);

        jCheckBox2.setText("Resolution");
        jCheckBox2.setEnabled(false);

        jTextField7.setText("1600");

        jTextField8.setText("900");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton9)
                                    .addComponent(jLabel6)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton10))))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox2)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCheckBox1)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton8)))
                .addContainerGap(482, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox2)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(259, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Options", jPanel2);

        jList2.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12)); // NOI18N
        jList2.setModel(new javax.swing.AbstractListModel() { test.WzFiles ff = new test.WzFiles();
            String[] strings = ff.removeHashFileEnds(ff.maplist());
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane4.setViewportView(jList2);

        jButton4.setText("del");
        jButton4.setEnabled(false);

        jList3.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12)); // NOI18N
        jList3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(jList3);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/test/up.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/test/down.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("add");
        jButton7.setEnabled(false);

        jLabel1.setText("MODs");

        jLabel2.setText("Maps");

        jList4.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12)); // NOI18N
        jList4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane6.setViewportView(jList4);

        jLabel9.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12)); // NOI18N
        jLabel9.setText("Activate");

        jLabel10.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12)); // NOI18N
        jLabel10.setText("DeActivete");

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
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton7)
                        .addGap(22, 22, 22)
                        .addComponent(jButton4)))
                .addContainerGap(560, Short.MAX_VALUE))
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
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4)
                            .addComponent(jButton7)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5))
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10))
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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
                .addContainerGap(662, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(427, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Ladder/Scoreboard/Tournaments", jPanel4);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/test/wz_faceboook_logo.png"))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Droid Sans", 0, 12)); // NOI18N
        jLabel12.setText("<html>\nThis launcher made by Kaynix for warzone2100 players to make online play more comfortable.<br>\nIf you have any suggestions/improvments or found bug contact me kaynix29@gmail.com or at forum pm to <b>Terminator</b><br>\nWarzone2100 Online version alpha 0.14:<br>\n- chat improvments<br>\n- add mods managment<br>\n- add support for windows and linux OS (no MAC OS)<br>\n- few bug fixes<br>\n<br>\n<p>run launcher in terminal to see logs.</p>\n</html>"); // NOI18N
        jLabel12.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(141, Short.MAX_VALUE))
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
    

    void chatrun() {
        try {
            String server = "irc.freenode.net";
            String nick = "simple_bot";
            String login = "simple_bot";

            // The channel which the bot will join.
            String channel = "#irchacks";

            Socket socket = new Socket(server, 6667);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Log on to the server.
            writer.write("NICK " + nick + "\r\n");
            writer.write("USER " + login + " 8 * : Java IRC Bot\r\n");
            writer.flush();

            line = null;
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("004") >= 0) {
                    // We are now logged in.
                    break;
                } else if (line.indexOf("433") >= 0) {
                    System.out.println("Nickname is already in use.");
                    return;
                }
            }
            line = null;
            // Join the channel.
            writer.write("JOIN " + channel + "\r\n");
            writer.flush();

            // Keep reading lines from the server.
            while ((line = reader.readLine()) != null) {
                if (line.toUpperCase().startsWith("PING ")) {
                    // We must respond to PINGs to avoid being disconnected.
                    writer.write("PONG " + line.substring(5) + "\r\n");
                    // System.out.println(line);
                    writer.write("PRIVMSG " + channel + " :I got pinged!\r\n");
                    //  writer.write("PRIVMSG termix :R y receiving it ?\r\n");
                    writer.flush();
                    writer.write("NAMES #irchacks");
                    writer.flush();
                } else {
                    // Print the raw line received by the bot.
                    jTextArea1.append(line);
                }
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Irchat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Irchat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
            Process p;
        try {
            // TODO add your handling code here:
            System.out.print("warzone2100.exe --join=" + jTable1.getValueAt(jTable1.getSelectedRow(), 4));
            if (System.getProperty("os.name").contains("Windows")) {
                p = Runtime.getRuntime().exec("warzone2100.exe --join=" + jTable1.getValueAt(jTable1.getSelectedRow(), 4));
            }
            p = Runtime.getRuntime().exec("./warzone2100 --join=" + jTable1.getValueAt(jTable1.getSelectedRow(), 4));
          //  Process p2 = Runtime.getRuntime().exec("warzone2100 --join=" + jTable1.getValueAt(jTable1.getSelectedRow(), 4));
        } catch (IOException ex) {
            Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        Process p;
        try {
            // TODO add your handling code here:
            if (System.getProperty("os.name").contains("Windows")) {
                p = Runtime.getRuntime().exec("warzone2100.exe --host");
            }
            p = Runtime.getRuntime().exec("./warzone2100 --host");

        } catch (IOException ex) {
            Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && jTextField1.getText().length() > 0) {
            try {
                writer.write("PRIVMSG #warzone2100-games :" + jTextField1.getText().trim() + "\r\n");
                writer.flush();
                jTextArea1.append(NJFrm.timestmp()+NICK +": "+ jTextField1.getText().trim()+ "\r\n");
                jTextField1.setText("");
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
            writer.write("QUIT");
            writer.flush();
            System.out.println("irc >> /quit.");
        } catch (IOException ex) {
            Logger.getLogger(NJFrm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // Deactivation MODS here
        String modname;
      
        if (System.getProperty("os.name").contains("Windows")) {
            modDirPath = ((System.getProperty("user.home")) + ("/Documents/Warzone 2100 3.1/mods/"));
        } else {
            modDirPath = ((System.getProperty("user.home")) + ("/.warzone2100-3.1/mods/")); //linux folderpath
        }
        modname = (String)jList3.getSelectedValue(); System.out.println(modDirPath+"autoload/"+modname);
        try{
        File mfile = new File(modDirPath+"autoload/"+modname);
        mfile.renameTo(new File(modDirPath+modname));} catch (Exception io){
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
      
        if (System.getProperty("os.name").contains("Windows")) {
            modDirPath = ((System.getProperty("user.home")) + ("/Documents/Warzone 2100 3.1/mods/"));
        } else {
            modDirPath = ((System.getProperty("user.home")) + ("/.warzone2100-3.1/mods/")); //linux folderpath
        }
        modname = (String)jList4.getSelectedValue(); System.out.println(modDirPath+modname); 
        try{
        File mfile = new File(modDirPath+modname);
        mfile.renameTo(new File(modDirPath+"autoload/"+modname));} catch (Exception io){
            System.out.println("Error moving file ::Activation error:: \r\n");
        }
        // refreshing GUI lists
        DefaultListModel ls  = (DefaultListModel) jList4.getModel();
        ls.removeElement(modname);
        ls = (DefaultListModel) jList3.getModel();
        ls.addElement(modname);
    }//GEN-LAST:event_jButton5ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
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
