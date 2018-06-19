package read_serial_Graph;
import com.fazecast.jSerialComm.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author https://www.youtube.com/watch?v=cw31L_OwX3A
 */
public class read_serial_Graph {


    static SerialPort valgteport;
    static int x = 0;
    
    public static void main(String[] args) {
        // TODO code application logic here
        JFrame vindue = new JFrame();
        vindue.setTitle("Graf over Serial input");
        vindue.setSize(600,500);
        vindue.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // reagér på luk
        vindue.setLayout(new BorderLayout());
        //vindue.pack();
        
        // Drop-down box og tilslut knap
        JComboBox<String> portlist = new JComboBox<String>();
        JButton tilslutknap = new JButton("Tilslut");
        JPanel topPanel = new JPanel();
        topPanel.add(portlist);
        topPanel.add(tilslutknap);
        vindue.add(topPanel, BorderLayout.NORTH);
        
        // Læg noget i drop-down boxen
        SerialPort ports[] = SerialPort.getCommPorts();
        
        for (int i = 0; i < ports.length; i++) {
            portlist.addItem(ports[i].getSystemPortName());
        }
        
        // Lav Grafen
        XYSeries xKordinater = new XYSeries("X koordinaterne ");
        XYSeries yKordinater = new XYSeries("Y koordinaterne ");
        
        XYSeriesCollection dataset = new XYSeriesCollection(xKordinater);
        dataset.addSeries(yKordinater);
        
        JFreeChart chart = ChartFactory.createXYLineChart("Værdier modtaget fra serial porten", "Tid", "Værdier", dataset);
        vindue.add(new ChartPanel(chart), BorderLayout.CENTER);
        
        
        // Opsæt tilslut knappen og brug en anden proces til at lytte efter data
        tilslutknap.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent ae) {
                if(tilslutknap.getText().equals("Tilslut")){
                    //Tilslut
                    valgteport = SerialPort.getCommPort(portlist.getSelectedItem().toString());
                    valgteport.setBaudRate(19200);
                    if(valgteport.openPort()){
                        System.out.println("Porten blev opsat korrekt");
                        tilslutknap.setText("Afbryd");
                        portlist.setEnabled(false);
                        valgteport.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

                    }else{
                        try {
                            //System.out.println("");
                            throw new Exception("Porten kunne ikke åbnes korrekt, prøv igen!");
                        } catch (Exception ex) {
                            System.exit(0);
                        }
                    }
                    
                    
                    
                    // Lav en ny thread og læg data i grafen
                    Thread thread = new Thread(){
                        @Override public void run(){
                            InputStream in = valgteport.getInputStream();
                            OutputStream out = valgteport.getOutputStream();
                            int ciffer1 = 0;
                            int ciffer10 = 0;
                            int samlet = 0;
                            byte[] buffer = null;
                            String besked = "c";
                            try {
                                buffer = besked.getBytes("ISO-8859-1");
                            } catch (UnsupportedEncodingException ex) {
                                ex.printStackTrace();
                            }
                            
                            while(true){
                                try{
                                    Thread.sleep(50);
                                    
                                    //Sender start signalet til LC-3'en
                                    valgteport.writeBytes(buffer, besked.length());
                                    out.flush();
                                    
                                    // Indlæser x-koordinatet
                                    ciffer1 = in.read();
                                    ciffer10 = in.read()<<5;
                                    samlet = ciffer10+ciffer1;
                                    System.out.println(""+samlet);
                                    xKordinater.add(x++,samlet);
                                    
                                    // Indlæser y-koordinatet
                                    ciffer1 = in.read();
                                    ciffer10 = in.read()<<5;
                                    samlet = ciffer10+ciffer1;
                                    System.out.println(""+samlet);
                                    yKordinater.add(x++,samlet);                                    
                                    vindue.repaint();
                                    //}
                                    
                                }catch(Exception e){}
                            }
                            //scanner.close();
                        }
                    };
                    thread.start();
                }else{
                    //Afbryd forbindelsen med COM porten
                    valgteport.closePort();
                    tilslutknap.setText("Tilslut");
                    portlist.setEnabled(true);
                    xKordinater.clear();
                    yKordinater.clear();
                    
                    x = 0;
                }
            }
        });
        
        
        vindue.setVisible(true);
        //Kommentar
        
    }

}
