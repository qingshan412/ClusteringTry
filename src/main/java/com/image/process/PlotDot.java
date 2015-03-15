package com.image.process;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;

public class PlotDot extends Applet
{
	/**
	 * 
	 */
	

	public PlotDot()
	{
		PlotDot i = new PlotDot();
		Graphics g = i.getGraphics();
		paint(g);
	}
	  
	 public void paint(Graphics g)
	 {
		 /*Color c = g.getColor();  
		   g.fillOval(100, 100, 50, 50);  
		   g.setColor(c); */
	    int startx = 50;
	    int starty = 500;
	    int endx = 500;
	    int endy = 500;
	    g.setColor(Color.black);
	    g.drawLine(startx, starty, startx, endy);
	    g.drawLine(startx, starty, endx, starty);
	}
}
