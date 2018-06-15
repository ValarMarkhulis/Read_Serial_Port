package UART_TX_SERIAL;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class TX_SERIAL {

    static SerialPort valgteport;
    static OutputStream out = null;
    static String ModtagetBesked = "";
    static boolean besked_to_long_flag = false;
    static int max_antal_char_modtager = 21;

    public static void main(String[] args) {

        //<editor-fold defaultstate="collapsed" desc="SWING komponenter opsætning">
        JFrame vindue = new JFrame();
        vindue.setTitle("Send data ud på COM port");
        vindue.setSize(600, 500);
        vindue.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // reagér på luk

        // Drop-down box og tilslut knap
        JComboBox<String> portlist = new JComboBox<String>();
        JButton tilslutknap = new JButton("Tilslut");

        JTextField beskeder_out = new JTextField();
        JButton sendknap = new JButton("Send");

        beskeder_out.setColumns(20);
        beskeder_out.setAutoscrolls(true);

        JTextArea beskeder_in = new JTextArea();
        beskeder_in.setEditable(false);

        JScrollPane jScrollPane1 = new javax.swing.JScrollPane();

        DefaultCaret caret = (DefaultCaret) beskeder_in.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(vindue.getContentPane());
        vindue.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jScrollPane1)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(beskeder_out, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(sendknap)))
                                                .addGap(25, 25, 25))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(portlist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(tilslutknap)
                                                .addGap(113, 113, 113))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(portlist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tilslutknap))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(beskeder_out, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(sendknap))
                                .addGap(14, 14, 14))
        );
        //Sæt ScrollPanel sammen med beskeder_in
        jScrollPane1.setViewportView(beskeder_in);
        vindue.pack();

        //Sæt send knappen som knappen der bliver trykket på når man trykker "Enter"
        vindue.getRootPane().setDefaultButton(sendknap);

        // Læg noget i drop-down boxen
        SerialPort ports[] = SerialPort.getCommPorts();

//</editor-fold>

    
        // Find alle COM portene der er tilsluttede
        for (int i = 0; i < ports.length; i++) {
            portlist.addItem(ports[i].getSystemPortName());
        }

        /* Opsæt tilslut knappen og brug en anden proces til at lytte efter data */
        tilslutknap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (tilslutknap.getText().equals("Tilslut")) {
                    //Tilslut
                    valgteport = SerialPort.getCommPort(portlist.getSelectedItem().toString());
                    // Opsætter port
                    valgteport.setComPortParameters(19200, 8, 1, 0);
                    if (valgteport.openPort()) {
                        System.out.println("Porten blev opsat korrekt");
                        tilslutknap.setText("Afbryd");
                        portlist.setEnabled(false);
                        valgteport.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

                        /* Thread der står og modtager karaktere og indsætter dem i textboksen. Den kører sålænge, at knappen
                        viser afbryd*/
                        
                        Thread thread_modtag = new Thread() {
                            @Override
                            public void run() {
                                InputStream in = valgteport.getInputStream();

                                //Aflæs fra Inputstream'en så længe, at brugeren har tilsluttet sig en port
                                while (tilslutknap.getText().equals("Afbryd")) {
                                    try {
                                        Thread.sleep(50);
                                        char karakter = 0;

                                        //Læs så længe at der er data at læse
                                        while (in.available() != 0) {
                                            karakter = (char) in.read();
                                            
                                            if(karakter == 8){
                                                ModtagetBesked = ModtagetBesked.substring(0, ModtagetBesked.length()-1);
                                                throw new Exception("Backspace");
                                            }
                                            
                                            /*Hvis der modtages en ugyldig ASCII-værdi så tilføjere den ikke
                                            det til boksen og smider karateren væk. Dette sker nogle gange, når man
                                            trykker på afbryd knappen*/
                                            /*if (karakter < 32 || karakter > 255) {
                                                throw new Exception("Ugyldig ASCII-værdi modtaget");
                                            }*/

                                            if (ModtagetBesked.length() > 100) {
                                                ModtagetBesked = ModtagetBesked.substring(ModtagetBesked.indexOf("\n") + 1);
                                                while (ModtagetBesked.startsWith("\n")) {
                                                    ModtagetBesked = ModtagetBesked.substring(ModtagetBesked.indexOf("\n") + 1);
                                                }
                                            } else {
                                                ModtagetBesked = ModtagetBesked + karakter;
                                            }

                                            
                                            //System.out.println("Printet noget til skærmen");
                                        }
                                    } catch (Exception e) {
                                    }
                                    beskeder_in.setText(ModtagetBesked);
                                }
                            }
                        };
                        thread_modtag.start();

                    }else { // Hvis porten ikke kan åbnes
                        try {
                            throw new Exception("Porten kunne ikke åbnes korrekt, prøv igen!");
                        } catch (Exception ex) {
                            System.exit(0);
                        }
                    }
                } else {
                    //Afbryd forbindelsen med COM porten
                    System.out.println("Porten blev lukket!");
                    valgteport.closePort();
                    tilslutknap.setText("Tilslut");
                    portlist.setEnabled(true);
                }
            }
        });

        /*Opsæt send knappen og opret en proces der køre når knappen viser "Afbryd" og man trykker send. 
        Processen står for, at sende data på OutputStream'en udfra teksten brugeren kan
        skrive ind og sende via GUI.*/
        sendknap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (tilslutknap.getText().equals("Afbryd")) {
                    // Lav en ny thread og læg data til output Streamen
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            out = valgteport.getOutputStream();
                            besked_to_long_flag = false;

                            try {
                                Thread.sleep(50);
                                String besked = beskeder_out.getText() + "\n";
                                beskeder_out.setText("");
                                String besked2 = null;

                                if (besked.length() > 16 && besked.length() < max_antal_char_modtager) {
                                    besked2 = besked.substring(16, besked.length());
                                    besked = besked.substring(0, 16);
                                    besked_to_long_flag = true;
                                } else if (besked.length() > max_antal_char_modtager) {
                                    //System.out.println();
                                    sendknap.setForeground(Color.RED);
                                    Thread.sleep(1000);
                                    sendknap.setForeground(Color.BLACK);
                                    throw new Exception("Hele beskeden kommer måske ikke frem, da strengen er " + (besked.length() - max_antal_char_modtager) + " karaktere for lang (" + max_antal_char_modtager + " max).");
                                }

                                byte[] buffer = besked.getBytes("ISO-8859-1");
                                valgteport.writeBytes(buffer, besked.length());
                                out.flush();

                                if (besked_to_long_flag) {
                                    byte[] buffer2 = besked2.getBytes("ISO-8859-1");
                                    valgteport.writeBytes(buffer2, besked2.length());
                                    out.flush();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                } else {
                    // Do nothing
                }
            }
        });

        vindue.setVisible(true);
    }
}
