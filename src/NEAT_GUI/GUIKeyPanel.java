package NEAT_GUI;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class GUIKeyPanel extends JPanel{
	
	public GUIKeyPanel() {
		setBackground(Color.DARK_GRAY);
		this.setBounds(0, 0, 1250, 300);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	private void paintNode(Graphics2D g2d, Color c, float alpha, double x_coor, double y_coor){
		g2d.setStroke(new BasicStroke(1f));
		
		/*Paint the node*/
		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2d.setComposite(alcom);
		g2d.setPaint(c);
		g2d.fill(new Rectangle2D.Double(x_coor, y_coor,10,10));	
		
		/*Draw the grid*/
		alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		g2d.setComposite(alcom);
		g2d.setPaint(Color.BLACK);
		g2d.draw(new Rectangle2D.Double(x_coor, y_coor,10,10));
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		
		float alpha;
		double x_coor;
		double y_coor;
		
		/*Painting the input nodes*/
		x_coor = 10;
		y_coor = 10;
		alpha = 1f;
		paintNode(g2d, Color.RED, alpha, x_coor, y_coor);
		
		y_coor += 20;
		alpha = 0.7f;
		paintNode(g2d, Color.RED, alpha, x_coor, y_coor);
		
		y_coor += 20;
		alpha = 0f;
		paintNode(g2d, Color.RED, alpha, x_coor, y_coor);
		
		/*Painting the hidden nodes*/
		y_coor += 20;
		alpha = 1f;
		paintNode(g2d, Color.BLUE, alpha, x_coor, y_coor);
		
		y_coor += 20;
		alpha = 0.7f;
		paintNode(g2d, Color.BLUE, alpha, x_coor, y_coor);
		
		/*Painting the output nodes*/
		y_coor += 20;
		alpha = 1f;
		paintNode(g2d, Color.GREEN, alpha, x_coor, y_coor);
		
		y_coor += 20;
		alpha = 0.7f;
		paintNode(g2d, Color.GREEN, alpha, x_coor, y_coor);
	}

}