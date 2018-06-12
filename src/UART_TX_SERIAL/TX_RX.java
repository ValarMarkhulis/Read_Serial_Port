
package UART_TX_SERIAL;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 *
 * @author Jonas
 */
public class TX_RX {

    static SerialPort port;
    
    public static void main(String[] args) {
        
        
        // VÆLGER PORT.
        SerialPort ports[] = SerialPort.getCommPorts();
        System.out.println("Vælg en COM port: ");
        int i = 1;
        for(SerialPort port : ports){
            System.out.println(i++ + ". "+ port.getSystemPortName());
        }
        Scanner tastatur = new Scanner(System.in);
        int valg= 0;
        valg = tastatur.nextInt();
        port = ports[valg - 1];
        
        // Opsætter port
        port.setComPortParameters(19200, 8, 1, 0);
        //port.setBaudRate(19200);
        if(port.openPort()){
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
            System.out.println("Porten blev opsat korrekt");
        }else{
            System.out.println("Porten kunne ikke åbnes korrekt, prøv igen!");
        }
        
        OutputStream out = port.getOutputStream();
        InputStream in = port.getInputStream();
        while(true) {
            boolean besked_to_long_flag = false;
            try{
                Thread.sleep(100);
                String besked = tastatur.nextLine()+"\n";
                String besked2 = null;
                if(besked.length() > 16 && besked.length() < 35){
                    besked2 = besked.substring(16, besked.length());
                    besked = besked.substring(0,16);
                    besked_to_long_flag = true;
                }else if(besked.length() > 35){
                    //System.out.println();
                    throw new Exception("Hele beskeden kommer måske ikke frem, da strengen er "+(besked.length()-35)+" karaktere for lang (34 max).");
                }
                
                byte[] buffer = besked.getBytes("ISO-8859-1");
                port.writeBytes(buffer, besked.length());
                Thread.sleep(100);
                out.flush();
                if(besked_to_long_flag){
                    byte[] buffer2 = besked2.getBytes("ISO-8859-1");
                    port.writeBytes(buffer2, besked2.length());
                    Thread.sleep(100);      
                    out.flush();
                }
                while(in.available() != 0) {
                    System.out.print((char)in.read());
                    Thread.sleep(100);
                }
            } catch(Exception e) {
               e.printStackTrace();
            }
        }
    }
}
