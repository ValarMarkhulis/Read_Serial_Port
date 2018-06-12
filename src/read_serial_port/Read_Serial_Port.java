package read_serial_port;
import com.fazecast.jSerialComm.*;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JSlider;

/**
 *
 * @author Taget fra samme gut: https://www.youtube.com/watch?v=cw31L_OwX3A
 */
public class Read_Serial_Port {

    static SerialPort port;
    
    public static void main(String[] args) {
        JFrame vindue = new JFrame();
        JSlider slider = new JSlider();
        slider.setMaximum(127);
        vindue.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // reagér på luk
        vindue.add(slider);
        vindue.pack();
        vindue.setVisible(true);
       
        
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
        
        
        
        //port = SerialPort.getCommPort("COM6");
        
        port.setBaudRate(19200);
        
        if(port.openPort()){
            System.out.println("Porten blev opsat korrekt");
        }else{
            System.out.println("Porten kunne ikke åbnes korrekt, prøv igen!");
        }
        
        
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        
        InputStream in = port.getInputStream();
        while(true) {
        try{
            Thread.sleep(50);
            while(in.available() != 0){
                
                /*
                byte[] readBuffer = new byte[port.bytesAvailable()];
                int numBytes = port.readBytes(readBuffer, readBuffer.length);
                //String data = new String(readBuffer, "UTF-8");
                System.out.println("Bytes: " + numBytes + "Data: " + readBuffer);
                */
                System.out.println((char)in.read());
                slider.setValue((int)in.read());
            }
        } catch(Exception e) {
           e.printStackTrace();
        }
        }
        /*
        Scanner scanner = new Scanner(port.getInputStream());
        while(scanner.hasNext()){
            try{
                String line = scanner.nextLine();
                int number = Integer.parseInt(line);
                System.out.println(number);
            }catch(Exception e){}
        }
        port.closePort();
        */
        
        
        /*
        //SerialPort comPort = SerialPort.getCommPorts()[0];
        //comPort.openPort();
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        InputStream in = port.getInputStream();
        try
        {
           for (int j = 0; j < 1000; ++j)
              System.out.print((char)in.read());
           in.close();
        } catch (Exception e) { e.printStackTrace(); }
        port.closePort();
                */
    }
}

