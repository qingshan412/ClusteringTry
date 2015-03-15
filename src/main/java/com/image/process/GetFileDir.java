package com.image.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;

public class GetFileDir
{
	public static ArrayList<String> AllFile(String Path) throws Exception 
	{
	   ArrayList<String> filelist = new ArrayList<String>();
	   filelist.clear();
	   String filePath = Path;
	   getFiles(filePath, filelist);
	   //used for debug
	   //int i;   
	   //for(i = 0; i < filelist.size(); i++)System.out.println(filelist.get(i));	   
	return filelist;
	}
	
	protected static void getFiles(String filePath,ArrayList<String> filelist)
	{
		File root = new File(filePath);
		File[] files = root.listFiles();
		for(File file:files)
		{
			if(file.isDirectory())
			{
				/*
				 * 递归调用
				 */
				getFiles(file.getAbsolutePath(),filelist);
			    //filelist.add(file.getAbsolutePath());
			    //System.out.println("显示"+filePath+"下所有子目录及其文件"+file.getAbsolutePath());
			}
			else
			{
			      //System.out.println("显示"+filePath+"下所有子目录"+file.getAbsolutePath());
				//System.out.println(file.getAbsolutePath());
				filelist.add(file.getAbsolutePath());
			}    
		}
	}
	
	public static void GetCSVFile(String Dir, ArrayList<GetImageInfo> RawData) throws IOException
	{
		//BufferedReader br = new BufferedReader(new FileReader("data/u.data"));
		
		File file = new File(Dir + "Data");
		if (!file.exists()) 
        { 
            file.mkdirs();
        }
		file = new File(Dir + "Data/data.csv");	
		
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		
		String line;
		for (int i = 0; i < RawData.size(); i++) 
		{
			line = RawData.get(i).GetInfo();
			String[] values= line.split(" ",-1);
			bw.write(values[0] + "," + values[1] + "," + values[2] + "," + values[3] + "," + values[4] + "," + values[5] + "," + values[6] + "," + values[7] + "," + values[8] + "\n");
		}
		//br.close();
		bw.close();
	}
	
	
}
