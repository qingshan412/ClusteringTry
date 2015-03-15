package com.predictionmarketing.test.ClusteringTry;

/**
 * Hello world!
 *
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.FileChannel;

import org.apache.hadoop.fs.FileSystem;//java.nio.file.FileSystem;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.AEADBadTagException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.jasper.tagplugins.jstl.core.Catch;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.Kluster;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.image.process.GetFileDir;
import com.image.process.GetImageInfo;

public class App 
{
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
    private static final String BASE_PATH = "/home/qs/Documents/Data/2015SP/KM";
    private static final String POINTS_PATH = BASE_PATH + "/points";
    private static final String CLUSTERS_PATH = BASE_PATH + "/clusters";
    private static final String OUTPUT_PATH = BASE_PATH + "/output";
    private static final String FILEC_PATH = BASE_PATH + "/filec";
    
    
    
    @SuppressWarnings("unused")
	public static void main( final String[] args ) throws Exception
    {
    	String Dir = "/home/qs/Documents/Data/2015SP/";
		ArrayList<String> FileList = GetFileDir.AllFile(Dir + "LPic/");
		//double[] PointsTmp;
		ArrayList<GetImageInfo> ImageInfos = new ArrayList<GetImageInfo>();
		for (String dir:FileList)
		{
			//System.out.println(dir);
			ImageInfos.add(new GetImageInfo(dir));
		}
		//for(int i = 0; i < ImageInfos.size(); i++)System.out.println(ImageInfos.get(i).Features[9] + " " + ImageInfos.get(i).Features[10] + " " + ImageInfos.get(i).Features[11] + " " + ImageInfos.get(i).Features[12]);
			//ImageInfos.get(i).PrintInfo();
		System.out.println("finished get image info");
		
		ArrayList<ArrayList<Double>> grayPixelDataArrayList = GetPixelDataArray(ImageInfos);
		
		
		for (int i = 0; i < ImageInfos.size(); i++)
		{
			for (int j = 0; j < grayPixelDataArrayList.get(i).size(); j++)
			{
				ImageInfos.get(i).Features[13+j] = grayPixelDataArrayList.get(i).get(j);
			}
		}
		System.out.println(grayPixelDataArrayList.get(0).size());
		
		System.out.println( "Hello World!" );
        final App application = new App();
        
        try {
            //application.start();
        	ArrayList<String> NoCluster=application.start(ImageInfos);    
        	
        	
        	for (int i = 0; i < NoCluster.size(); i++)
			{
        		File file = new File(FILEC_PATH + "/" + NoCluster.get(i));
				if (!file.exists()) 
                { 
                    file.mkdirs(); 
                }
				
				File sfile=new File(ImageInfos.get(i).PathDir);
				File tfile=new File(FILEC_PATH + "/" + NoCluster.get(i) + "/" + i + ImageInfos.get(i).Suffix);
				
				fileChannelCopy(sfile, tfile);
				
			}
        }
        catch (final Exception e) {
            LOG.error("MahoutTryIt failed", e);
        }
    }
    
    // ---- Fields
    
    private final double[][] points =
    	{{24,0,124,124,0,-1,-1,-1.0,-1.0},
    		{8,2,115,101,0,-1,-1,-1.0,-1.0},
    		{24,0,123,123,0,-1,-1,-1.0,-1.0},
    		{8,2,149,112,0,-1,-1,-1.0,-1.0}};
       /* { { 1, 1 }, { 2, 1 }, { 1, 2 },
        { 2, 2 }, { 3, 3 }, { 8, 8 },
        { 9, 8 }, { 8, 9 }, { 9, 9 } };*/
 
    private final int numberOfClusters = 3;//2;
 
    // ---- Methods
    
    private ArrayList<String> start(ArrayList<GetImageInfo> ImgInfos)throws Exception {
    //private void start(ArrayList<GetImageInfo> ImgInfos)throws Exception {
    	//private void start()throws Exception { 
            final Configuration configuration = new Configuration();
     
            // Create input directories for data
            final File pointsDir = new File(POINTS_PATH);
            if (!pointsDir.exists()) {
                pointsDir.mkdir();
            }
     
            // read the point values and generate vectors from input data
            //final List vectors = vectorize(points);
            final List vectors = vectorize(ImgInfos);
     
            // Write data to sequence hadoop sequence files
            writePointsToFile(configuration, vectors);
     
            // Write initial centers for clusters
            writeClusterInitialCenters(configuration, vectors);
     
            // Run K-means algorithm
            final Path inputPath = new Path(POINTS_PATH);
            final Path clustersPath = new Path(CLUSTERS_PATH);
            final Path outputPath = new Path(OUTPUT_PATH);
            HadoopUtil.delete(configuration, outputPath);
     
            KMeansDriver.run(configuration, inputPath, clustersPath, outputPath, 0.001, 10, true, 0, false);
     
            // Read and print output values
            ArrayList<String> NoCluster=readAndPrintOutputValues(configuration);
            
            return NoCluster;
        }
    
    private void writePointsToFile(final Configuration configuration, final List<Vector> points)
            throws IOException {
     
            final Path path = new Path(POINTS_PATH + "/pointsFile");
            
            FileSystem fs = FileSystem.get(configuration);
     
            final SequenceFile.Writer writer //= new SequenceFile.Writer(fs,configuration,path,SequenceFile.Writer.keyClass(IntWritable.class),SequenceFile.Writer.valueClass(VectorWritable.class));
            =
                SequenceFile.createWriter(
                    fs, configuration,
                    path,//SequenceFile.Writer.FileOption(path),
                    IntWritable.class,//SequenceFile.Writer.keyClass(IntWritable.class),
                    VectorWritable.class);//SequenceFile.Writer.valueClass(VectorWritable.class));
     
            int recNum = 0;
            final VectorWritable vec = new VectorWritable();
     
            for (final Vector point : points) {
                vec.set(point);
                writer.append(new IntWritable(recNum++), vec);
            }
     
            writer.close();
        }
     
    private void writeClusterInitialCenters(final Configuration configuration, final List<Vector> points)
            throws IOException {
            final Path writerPath = new Path(CLUSTERS_PATH + "/part-00000");
            
            FileSystem fs = FileSystem.get(configuration);
     
            final SequenceFile.Writer writer =
                SequenceFile.createWriter(
                    fs, configuration,
                    writerPath,//SequenceFile.Writer.file(writerPath),
                    Text.class,//SequenceFile.Writer.keyClass(Text.class),
                    Kluster.class);//SequenceFile.Writer.valueClass(Kluster.class));
     
            for (int i = 0; i < numberOfClusters; i++) {
                final Vector vec = points.get(i);
     
                // write the initial centers
                final Kluster cluster = new Kluster(vec, i, new EuclideanDistanceMeasure());
                writer.append(new Text(cluster.getIdentifier()), cluster);
            }
     
            writer.close();
        }
        
    private ArrayList<String> readAndPrintOutputValues(final Configuration configuration)
    //private void readAndPrintOutputValues(final Configuration configuration)
                throws IOException {
                final Path input = new Path(OUTPUT_PATH + "/" + Cluster.CLUSTERED_POINTS_DIR + "/part-m-00000");
                
                ArrayList<String> NoCluster=new ArrayList<String>();
                
                FileSystem fs = FileSystem.get(configuration);
         
                final SequenceFile.Reader reader =
                    new SequenceFile.Reader(
                        fs, input,configuration);
                        //SequenceFile.Reader.file(input));
         
                final IntWritable key = new IntWritable();
                final WeightedPropertyVectorWritable value = new WeightedPropertyVectorWritable();
         
                while (reader.next(key, value)) {
                    LOG.info("{} belongs to cluster {}", value.toString(), key.toString());
                    NoCluster.add(key.toString());
                }
                reader.close();
                
                return NoCluster;
            }
         
            // Read the points to vector from 2D array
    public List<Vector> vectorize(final double[][] raw) {
                final List<Vector> points = new ArrayList<Vector>();
         
                for (int i = 0; i < raw.length; i++) {
                    final Vector vec = new RandomAccessSparseVector(raw[i].length);
                    vec.assign(raw[i]);
                    points.add(vec);
                }
         
                return points;
            }
    
    public List<Vector> vectorize(ArrayList<GetImageInfo> ImgInfos)
	{
    	final List<Vector> points = new ArrayList<Vector>();
        
        for (int i = 0; i < ImgInfos.size(); i++) {
            final Vector vec = new RandomAccessSparseVector(ImgInfos.get(i).Features.length);
            vec.assign(ImgInfos.get(i).Features);
            points.add(vec);
        }
        return points;
	}
    
    public static ArrayList<ArrayList<Double>> GetPixelDataArray(ArrayList<GetImageInfo> ImgInfos)
	{
    	if (ImgInfos.isEmpty()) return null;
    	ArrayList<ArrayList<Double>> PixelDataArrayAll = new ArrayList<ArrayList<Double>>();
    	ArrayList<Double> PixelDataArray = new ArrayList<Double>();
    	int w = ImgInfos.get(0).Width;
    	int h = ImgInfos.get(0).Height;
    	int wt,ht;
    	
    	for (int i = 1; i < ImgInfos.size(); i++)
		{
			wt = ImgInfos.get(i).Width;
			ht = ImgInfos.get(i).Height;
			if(w < wt)w = wt;
			if(h < ht)h = ht;
		}
    	
    	for (int i = 0; i < ImgInfos.size(); i++)
		{
			wt = ImgInfos.get(i).Width;
			ht = ImgInfos.get(i).Height;
			for (int j = 0; j < h; j++)
			{
				if (j < ht)
				{
					for (int j2 = 0; j2 < w; j2++)
					{
						if (j2 < wt)
						{
							PixelDataArray.add(ImgInfos.get(i).Gray.get(j*wt+j2));
						}
						else PixelDataArray.add(0.0);
					}
				}
				else 
				{
					for (int j2 = 0; j2 < w; j2++) PixelDataArray.add(0.0);
				}
			}
			
			PixelDataArrayAll.add((ArrayList<Double>) PixelDataArray.clone());
			PixelDataArray.clear();
		}
    	
    	return PixelDataArrayAll;
	}
    
    public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;

        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
