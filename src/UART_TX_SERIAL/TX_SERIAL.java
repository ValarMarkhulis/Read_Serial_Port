
package UART_TX_SERIAL;

import com.fazecast.jSerialComm.SerialPort;
import java.io.OutputStream;
import java.util.Scanner;

/**
 *
 * @author Jonas
 */
public class TX_SERIAL {

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
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
            System.out.println("Porten blev opsat korrekt");
        }else{
            System.out.println("Porten kunne ikke åbnes korrekt, prøv igen!");
        }
        
        OutputStream out = port.getOutputStream();
        while(true) {
            try{
                Thread.sleep(50);
                String besked = tastatur.nextLine()+"\n";
                byte[] buffer = besked.getBytes("ISO-8859-1");
                port.writeBytes(buffer, besked.length());
                //System.out.println(port.bytesAwaitingWrite());
                out.flush();
                
            } catch(Exception e) {
               e.printStackTrace();
            }
        }
    }
}
