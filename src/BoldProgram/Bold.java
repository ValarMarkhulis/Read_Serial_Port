package BoldProgram;
import java.awt.Color;
import java.awt.Graphics;


public class Bold {
   	double x, y;
	Graphics g;

	public Bold(Graphics g1, int x1, int y1)
	{
            g = g1;
            x = x1;
            y = y1;
            
            
            
//            if(y1 < 820){
//                y = y1;
//            }else{
//                y = 820;
//            }
            

            g.setColor(Color.BLACK);
            g.drawOval((int) x, (int) y, 50, 50);
                
	} 
}
