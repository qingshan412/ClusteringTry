package com.image.process;

import java.util.ArrayList;


public class ClusterTest
{

	public static void main(String[] args) throws Exception 
	{
		String Dir = "/home/qs/Documents/Data/2015SP/";
		ArrayList<String> FileList = GetFileDir.AllFile(Dir + "Pic/");
		ArrayList<GetImageInfo> ImageInfos = new ArrayList<GetImageInfo>();
		for (String dir:FileList)
		{
			//System.out.println(dir);
			ImageInfos.add(new GetImageInfo(dir));
		}
		for(int i = 0; i < ImageInfos.size(); i++)ImageInfos.get(i).PrintInfo();
			//System.out.println(ImageInfos.get(i).Features[8]);
		System.out.println("finished get image info");
		
		//GetFileDir.GetCSVFile(Dir, ImageInfos);
		//System.out.println("finished convert data");
		
		
		

	    /*
		int k=2;
		  List<Vector> vectors=getPoints(points);
		  File testData=new File("testdata");
		  if (!testData.exists()) {
		    testData.mkdir();
		  }
		  testData=new File("testdata/points");
		  if (!testData.exists()) {
		    testData.mkdir();
		  }
		  Configuration conf=new Configuration();
		  FileSystem fs=FileSystem.get(conf);
		  writePointsToFile(vectors,"testdata/points/file1",fs,conf);
		  Path path=new Path("testdata/clusters/part-00000");
		  SequenceFile.Writer writer=new SequenceFile.Writer(fs,conf,path,Text.class,Cluster.class);
		  for (int i=0; i < k; i++) {
		    Vector vec=vectors.get(i);
		    Cluster cluster=new Cluster(vec,i,new EuclideanDistanceMeasure());
		    writer.append(new Text(cluster.getIdentifier()),cluster);
		  }
		  writer.close();
		  KMeansDriver.run(conf,new Path("testdata/points"),new Path("testdata/clusters"),new Path("output"),new EuclideanDistanceMeasure(),0.001,10,true,false);
		  SequenceFile.Reader reader=new SequenceFile.Reader(fs,new Path("output/" + Cluster.CLUSTERED_POINTS_DIR + "/part-m-00000"),conf);
		  IntWritable key=new IntWritable();
		  WeightedVectorWritable value=new WeightedVectorWritable();
		  while (reader.next(key,value)) {
		    System.out.println(value.toString() + " belongs to cluster " + key.toString());
		  }
		  reader.close();*/
		  
	}
	
}
