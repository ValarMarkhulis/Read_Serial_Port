/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package read_serial_port;
import com.fazecast.jSerialComm.*;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JSlider;

/**
 *
 * @author Taget fra samme gut: https://www.youtube.com/watch?v=cw31L_OwX3A
 */
public class Read_Serial_Port {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame vindue = new JFrame();
        JSlider slider = new JSlider();
        slider.setMaximum(1023);
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
        SerialPort port = null;
        valg = tastatur.nextInt();
        port = ports[valg - 1];
        
        
        if(port.openPort()){
            System.out.println("Porten blev opsat korrekt");
        }else{
            System.out.println("Porten kunne ikke åbnes korrekt, prøv igen!");
        }
        
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        
        Scanner data = new Scanner(port.getInputStream());
        while(data.hasNextLine()){
            int nummer = 0;
            try {
                nummer = Integer.parseInt(data.nextLine());
            } catch (Exception e) {
            }
            slider.setValue(nummer);
        }
    }

}
