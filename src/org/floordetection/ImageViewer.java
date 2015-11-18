package org.floordetection;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageViewer 
{
	private JLabel imageView;
	
	public void show(Mat image)
	{
		show(image, "");
	}
	
	public void show(Mat image,String windowName)
	{
		setSystemLookAndFeel();
		JFrame frame = createJFrame(windowName);
		Image loadedImage = toBufferedImage(image);
		imageView.setIcon(new ImageIcon(loadedImage));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private JFrame createJFrame(String windowName) 
	{
		JFrame frame = new JFrame(windowName);
		imageView = new JLabel();
		final JScrollPane imageScrollPane = new JScrollPane(imageView);
		imageScrollPane.setPreferredSize(new Dimension(640, 480));
		frame.add(imageScrollPane, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}
	
	private void setSystemLookAndFeel() 
	{
		try {
		UIManager.setLookAndFeel
		(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
		e.printStackTrace();
		} catch (InstantiationException e) {
		e.printStackTrace();
		} catch (IllegalAccessException e) {
		e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
		e.printStackTrace();
		}
	}
	
	public Image toBufferedImage(Mat matrix)
	{
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( matrix.channels() > 1 ) {
		type = BufferedImage.TYPE_3BYTE_BGR;
		}
		
		
		int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
		byte [] buffer = new byte[bufferSize];
		matrix.get(0,0,buffer); // get all the pixels
		BufferedImage image = new BufferedImage(matrix.cols(),matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
		return image;
	}
	
	public Mat blur(Mat input, int numberOfTimes){
		Mat sourceImage = new Mat();
		Mat destImage = input.clone();
		for(int i=0;i<numberOfTimes;i++){
			sourceImage = destImage.clone();
			Imgproc.blur(sourceImage, destImage, new Size(3.0, 3.0));
		}
		return destImage;
	}
	
	public Mat erode(Mat input, int elementSize, int elementShape){
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.erode(input,outputImage, element);
		return outputImage;
	}

	

	public Mat dilate(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.dilate(input,outputImage, element);
		return outputImage;
	}

	public Mat open(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.morphologyEx(input,outputImage, Imgproc.MORPH_OPEN, element);
		return outputImage;
	}

	public Mat close(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.morphologyEx(input,outputImage, Imgproc.MORPH_CLOSE, element);
		return outputImage;
	}
	
	private Mat getKernelFromShape(int elementSize, int elementShape) {
		return Imgproc.getStructuringElement(elementShape, new Size(elementSize*2+1, elementSize*2+1), new Point(elementSize, elementSize) );
	}
	


}
