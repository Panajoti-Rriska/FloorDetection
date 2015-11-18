package org.floordetection;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.io.File;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.videoio.Videoio;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

public class MainApp 
{
	
	static
	{ 
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	private JFrame frame;
	JLabel imageLabel1 = new JLabel("", SwingConstants.CENTER);
    JLabel imageLabel2 = new JLabel("", SwingConstants.CENTER);
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	
	public static void main(String[] args) throws Exception 
	{
		MainApp app = new MainApp();
		//app.openArsenalImage();
		app.initGui();
		app.runMainLoop(args);

		
	}
	
	private void initGui()
	{
		/*frame = new JFrame("Floor Detection 0.1");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,600);
		imageLabel = new JLabel();
		imageLabel2 = new JLabel();
		
		frame.add(imageLabel);
		frame.add(imageLabel2);
		frame.setVisible(true);*/
		
		//imageLabel1.setVerticalAlignment(SwingConstants.TOP);
		//imageLabel2.setVerticalAlignment(SwingConstants.TOP);

	   // imageLabel1.setBorder(BorderFactory.createLineBorder(Color.black));
	    //imageLabel2.setBorder(BorderFactory.createLineBorder(Color.black));

	    JFrame frame = new JFrame("Floor Detection 0.1");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JPanel p = new JPanel(new GridLayout(1, 1, 1, 1));
	    p.add(imageLabel1);
	    p.add(imageLabel2);

	    p.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
	    frame.setContentPane(p);
	    frame.setSize(600, 600);
	    frame.setVisible(true);

	}
	
	private void runMainLoop(String[] args) throws InterruptedException
	{
		ImageViewer imageProcessor = new ImageViewer();
		FloodFill floodFill = new FloodFill();
		Mat webcamMatImage = new Mat();

	    Mat mask = new Mat();

			    
		Image tempImage;
		Image tempImage2;
		//VideoCapture capture = new VideoCapture(0);
		VideoCapture capture = new VideoCapture("videos/floor3.mp4");
		
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH,500);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,300);
		int initFrames = (int) capture.get(Videoio.CAP_PROP_FPS);
		
		
		if(capture.isOpened())
		{
			while(true)
			{
				capture.read(webcamMatImage);
				
				if(!webcamMatImage.empty())
				{
					Mat matblurredImage = imageProcessor.blur(webcamMatImage, 1);
					mask.create(new Size(webcamMatImage.cols()+2, webcamMatImage.rows()+2), CvType.CV_8UC1);
					
					mask.setTo(new Scalar(0));
					//Setting range method for fill flood
					floodFill.setRange(FloodFill.FIXED_RANGE);
					
					//Connectivity setting for 8 neighbour pixels
					floodFill.setConnectivity(8);
					
					//Lower and Higher difference of pixels 
					floodFill.setLowerDiff(90);
					floodFill.setUpperDiff(100);
					
					//Here you point the coordinates x y of the pixel to get populated
					floodFill.fill(matblurredImage, mask, webcamMatImage.width()*5/8, webcamMatImage.height()*5/8);
					
					tempImage= imageProcessor.toBufferedImage(matblurredImage);
					tempImage2 = imageProcessor.toBufferedImage(webcamMatImage);
					
					ImageIcon imageIcon = new ImageIcon(tempImage, "Floor Detection 0.1");
					ImageIcon imageIcon2 = new ImageIcon(tempImage2, "Floor Detection 0.1");
					
					imageLabel1.setIcon(imageIcon);
					imageLabel2.setIcon(imageIcon2);
					
					//frame.pack(); //this will resize the window to fit the image
					//frame2.pack();
					
					//delay for frames to be played properly
					TimeUnit.MILLISECONDS.sleep(initFrames);
					
				}else
				{
					System.out.println(" -- Frame not captured -- Break!");
					break;
				}
			}
		}else
		{
			System.out.println("Couldn't open capture.");
		}
	}
	
	protected void startCamera(ActionEvent event)
	{	
		ImageViewer imageProcessor = new ImageViewer();
		Mat webcamMatImage = new Mat();
		//VideoCapture capture = new VideoCapture(0);
		VideoCapture capture = new VideoCapture("videos/floor1.mp4");

			// is the video stream available?
			if (capture.isOpened())
			{
						
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {
					
					@Override
					public void run()
					{
						Image tempImage= imageProcessor.toBufferedImage(webcamMatImage);						
						ImageIcon imageIcon = new ImageIcon(tempImage, "Floor Detection 0.1");
						imageLabel1.setIcon(imageIcon);
						frame.pack(); //this will resize the window to fit the image
					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
				
				
			}
			else
			{
				// log the error
				System.err.println("Impossible to open the camera connection...");
			}
		}

	
	private void openArsenalImage() 
	{
		String filePath = "images/arsenal.jpg";
		Mat newImage = Imgcodecs.imread(filePath);
		if(newImage.dataAddr()==0)
		{
		System.out.println("Couldn't open file " + filePath);
		} else
		{
		ImageViewer imageViewer = new ImageViewer();
		imageViewer.show(newImage, "Loaded image");
		}	
	}
        
}

