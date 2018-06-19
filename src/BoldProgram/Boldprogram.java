package BoldProgram;

import java.awt.Color;
import java.awt.Graphics;
import java.io.OutputStream;
import javax.swing.JFrame;
import com.fazecast.jSerialComm.SerialPort;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;

import javax.swing.JFrame;

/**
 *
 * @author Christian Mark
 */
public class Boldprogram {

    /**
     * @param args the command line arguments
     */
    static SerialPort valgteport;

    static Graphics g;

    public static void main(String[] arg) {
        final JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // reagér på luk
        //f.setSize(400, 150);
        f.setBackground(Color.BLACK);
        
        f.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        f.setUndecorated(true);
        f.setVisible(true);

        g = f.getGraphics();

        // VÆLGER PORT.
        SerialPort ports[] = SerialPort.getCommPorts();
        System.out.println("Vælg en COM port: ");
        int i = 1;
        for (SerialPort port : ports) {
            System.out.println(i++ + ". " + port.getSystemPortName());
        }
        Scanner tastatur = new Scanner(System.in);
        int valg = 0;
        valg = tastatur.nextInt();
        valgteport = ports[valg - 1];

        // Opsætter port
        valgteport.setComPortParameters(19200, 8, 1, 0);

        if (valgteport.openPort()) {
            System.out.println("Porten blev opsat korrekt");
            valgteport.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
            InputStream in = valgteport.getInputStream();
            OutputStream out = valgteport.getOutputStream();

            Runnable runnable;
            runnable = new Runnable() {
                int ciffer10 = 0;
                int ciffer1 = 0;
                char karakter = 0;
                int samlet = 0;
                byte[] buffer;
                String besked = "c";
                int x = 0;
                int y = 0;

                public void run() {
                    while (true) {
                        try {
                            // Start signal til LC-3 computeren
                            buffer = besked.getBytes("ISO-8859-1");

                            //Send start signal
                            valgteport.writeBytes(buffer, besked.length());
                            out.flush();

                            Thread.sleep(100);

                            //Læs x-koordinatet
                            //while (in.available() != 0) {
                            ciffer1 = in.read();
                            ciffer10 = in.read() << 5;
                            samlet = ciffer10 + ciffer1;
                            System.out.println("x:" + samlet);
                            //}
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        x = samlet;

                        try {
                            Thread.sleep(100);

                            //Læs y-koordinatet
                            //while (in.available() != 0) {
                            ciffer1 = in.read();
                            ciffer10 = in.read() << 5;
                            samlet = ciffer10 + ciffer1;
                            System.out.println("y:" + samlet);
                            //}
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        y = samlet;
                        f.repaint();
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        
                        new Bold(g, x, y);
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }

                }
            };
            Thread t = new Thread(runnable);
            t.start();

        }
    }
}
