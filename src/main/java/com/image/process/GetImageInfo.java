package com.image.process;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.devlib.schmidt.imageinfo.ImageInfo;

public class GetImageInfo
{

	public String PathDir;
	public String Suffix;
	
	public int BitsPerPixel;
	public int Format;
	public int Height;
	public int Width;
	public int NumberOfComments;
	public int PhysicalHeightDpi;
	public int PhysicalWidthDpi;
	public float PhysicalHeightInch;
	public float PhysicalWidthInch;
	public double Alpha;
	public double Red;
	public double Green;
	public double Blue;
	
	public double[] Features=new double[22214];
	
	public ArrayList<Double> Al = new ArrayList<Double>();
	public ArrayList<Double> Re = new ArrayList<Double>();
	public ArrayList<Double> Gr = new ArrayList<Double>();
	public ArrayList<Double> Bl = new ArrayList<Double>();
	public ArrayList<Double> Gray = new ArrayList<Double>();
	
	
	public GetImageInfo(String Dir) throws Exception
	{
		
		InputStream in = new FileInputStream(Dir);
		
		PathDir = Dir;

		ImageInfo ii = new ImageInfo();
		ii.setInput(in); // in can be InputStream or RandomAccessFile
		ii.setDetermineImageNumber(true); // default is false
		ii.setCollectComments(true); // default is false
		if (!ii.check()) {
		  System.err.println("Not a supported image file format.");
		  return;
		}
		
		BitsPerPixel = ii.getBitsPerPixel();
		Features[0]=(float)BitsPerPixel;
		
		Format = ii.getFormat();
		Features[1]=(float)Format;
		
		Height = ii.getHeight();
		Features[2]=(float)Height;
		
		Width = ii.getWidth();
		Features[3]=(float)Width;
		
		NumberOfComments = ii.getNumberOfComments();
		Features[4]=(float)NumberOfComments;
		
		PhysicalHeightDpi = ii.getPhysicalHeightDpi();
		Features[5]=(float)PhysicalHeightDpi;
		
		PhysicalWidthDpi = ii.getPhysicalWidthDpi();
		Features[6]=(float)PhysicalWidthDpi;
		
		PhysicalHeightInch = ii.getPhysicalHeightInch();
		Features[7]=PhysicalHeightInch;
		
		PhysicalWidthInch = ii.getPhysicalWidthInch();		
		Features[8]=PhysicalWidthInch;
		
		switch (Format)
		{
			case 0:
				Suffix = ".jpg";
				break;
			case 2:
				Suffix = ".png";
				break;
			case 1:
				Suffix = ".gif";
				break;
			case 3:
				Suffix = ".bmp";
				break;
			case 4:
				Suffix = ".pcx";
				break;
			case 5:
				Suffix = ".iff";
				break;
			case 6:
				Suffix = ".ras";
				break;
			case 7:
				Suffix = ".pbm";
				break;
			case 8:
				Suffix = ".pgm";
				break;
			case 9:
				Suffix = ".ppm";
				break;
			case 10:
				Suffix = ".psd";
				break;
			case 11:
				Suffix = ".swf";
				break;
			default:
				Suffix = "." + ii.getFormatName();
				System.out.println(ii.getFormatName());
				break;
		}
		
		//used for debug
		//System.out.println(ii.getFormatName() + ", " + Format + "," + Width + " x " + Height + " pixels, " + BitsPerPixel + " bits per pixel, " + NumberOfComments + " comment(s)." + PhysicalHeightDpi);
		 // there are other properties, check out the API documentation
		
		 
		in.close();
		
		
		
		
		//using BufferedImage"ColorModel: " + 
		BufferedImage bufferedImage = ImageIO.read(new FileInputStream(Dir));
		ARGBInfo(bufferedImage);
		grayImage(bufferedImage);
		//System.out.println(bufferedImage.getColorModel().getColorSpace().getNumComponents());
	}
	
	
	private void ARGBInfo(BufferedImage image) 
	{
	    int w = image.getWidth();
	    int h = image.getHeight();
	    double a,r,g,b;
	    Alpha = 0.0;
	    Red = 0.0;
	    Green = 0.0;
	    Blue = 0.0;
	    
	    
	    for (int i = 0; i < h; i++) 
	    {
	      for (int j = 0; j < w; j++) 
	      {
	        int pixel = image.getRGB(j, i);
	        
	        a = (pixel >> 24) & 0xff;
	        r = (pixel >> 16) & 0xff;;
	        g = (pixel >> 8) & 0xff;;
	        b = (pixel) & 0xff;
	        
	        Alpha += a;
	        Red += r;
	        Green += g;
	        Blue += b;
	        
	        Al.add(a);
	        Re.add(r);
	        Gr.add(g);
	        Bl.add(b);
	        Gray.add((r+g+b)/3);
//	        int alpha = (pixel >> 24) & 0xff;
//	        int red = (pixel >> 16) & 0xff;
//	        int green = (pixel >> 8) & 0xff;
//	        int blue = (pixel) & 0xff;
//	      
	      }
	    }
	    
	    Alpha = Alpha/(w*h);
	    Features[9]=Alpha;
	    Red = Red/(w*h);
	    Features[10]=Red;
	    Green = Green/(w*h);
	    Features[11]=Green;
	    Blue = Blue/(w*h);
	    Features[12]=Blue;
	    
	  }
	
	
	public void grayImage(BufferedImage image)
	{  	      
	    //int width = image.getWidth();  
	    //int height = image.getHeight();  
	      
	    BufferedImage grayImage = new BufferedImage(Width, Height, BufferedImage.TYPE_BYTE_GRAY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_GRAY  
	    for(int i= 0 ; i < Width ; i++)
	    {  
	        for(int j = 0 ; j < Height; j++)
	        {  
		        int rgb = image.getRGB(i, j);  
		        grayImage.setRGB(i, j, rgb);  
	        }  
	    }  
	      
	    //File newFile = new File(System.getProperty("user.dir")+"/src/2722425974762424027.jpg");  
	    //ImageIO.write(grayImage, "jpg", newFile);  
	}  
	
	
	public String GetInfo()
	{
		String Info ="{" + BitsPerPixel + " " + Format + " " + Height + " "	+ Width + " " + NumberOfComments 
				+ " " + PhysicalHeightDpi + " " + PhysicalWidthDpi + " " + PhysicalHeightInch + " " 
				+ PhysicalWidthInch + "}";
		return Info;
	}
	
	
	public void PrintInfo()
	{
		System.out.println(PathDir + "{" + BitsPerPixel + "," + Format + "," + Height + ","	+ Width + "," + NumberOfComments 
				+ "," + PhysicalHeightDpi + "," + PhysicalWidthDpi + "," + PhysicalHeightInch + "," 
				+ PhysicalWidthInch + "},");
	}
}
