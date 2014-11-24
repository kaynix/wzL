package wzL;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

 class Chat extends JFrame {

	private static final long serialVersionUID = 1L;
	JPanel panel;
	private final String DESIRED_NICK = "kensclark156";
	private final String SERVER = "irc.esper.net";
	private final int DEFAULT_PORT = 6667;
	private final String CHANNEL = "#bukkitdev";
	private JTextArea textArea;
	private JTextField textField;
	private JScrollPane scroller;
	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;

	public Chat() {
		textArea = new JTextArea(1000, 1000);
		textField = new JTextField(10);
		textField.setPreferredSize(new Dimension(480, 50));
		textArea.setFont(new Font("Serif", Font.PLAIN, 12));
		textArea.setLineWrap(true);
		textArea.setPreferredSize(new Dimension(480, 300));
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		scroller = new JScrollPane(textArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setAutoscrolls(true);
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		panel = new JPanel();
		panel.setFocusable(true);
		panel.setPreferredSize(new Dimension(500, 500));

		this.setTitle("Ken's IRC Client");
		this.setFocusable(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(500, 500);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		panel.setLayout(new BorderLayout());
		panel.add(scroller, BorderLayout.CENTER);
		panel.add(textField, BorderLayout.SOUTH);
		textField.addKeyListener(new InputListener());
		textArea.setText("Welcome to Ken's IRC Chat!");
		this.add(panel);
		this.pack();
		connectToServer();
	}
       /* void chatrun() {
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
*/
	private void connectToServer() {
		try {
			socket = new Socket(SERVER, DEFAULT_PORT);
			writer = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			new Thread(new Incoming()).start();
			new Thread(new Outgoing()).start();
			writer.write("NICK " + DESIRED_NICK + "\r\n");
			writer.write("USER " + DESIRED_NICK
					+ " 8 * : Ken's IRC chat program\r\n");
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private class Incoming implements Runnable {

                @Override
		public void run() {

			String line = "";
			try {

				while ((line = reader.readLine()) != null) {
					if (line.indexOf("001") >= 0) {
						writer.write("JOIN " + CHANNEL + "\r\n");
						writer.flush();
					}
					if (line.startsWith("PING ")) {
						writer.write("PONG " + line.substring(5) + "\r\n");
						writer.flush();
					}
					System.out.println(line);
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}

		}

	}

	private class Outgoing implements Runnable {

		public void run() {

		}

	}
	
	private class InputListener implements KeyListener {

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!(textField.getText().length() == 0)) {
					try {
						writer.write(textField.getText() + "\r\n");
						writer.flush();
						if (textArea.getLineCount() > 1000) {
							int to = textArea.getText().indexOf("\n");
							String newText = textArea.getText().substring(0 , to);
							textArea.setText(newText);
						}
						textArea.setText(textArea.getText() + "\n" + textField.getText());
						textField.setText("");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}

		public void keyReleased(KeyEvent e) {
			
		}

		public void keyTyped(KeyEvent e) {
			
		}
		
	}

}