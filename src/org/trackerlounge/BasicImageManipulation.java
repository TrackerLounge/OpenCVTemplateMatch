package org.trackerlounge;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
/*	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
 
    BasicImageManipulation bim = new BasicImageManipulation();
	
	Mat goal = Main.loadGrayscaleImage(filename1);
	Mat searchArea = Main.loadGrayscaleImage(filename2);
	
	Size scale = bim.prepForScaleProportionally(goal, goal.width()/3);
	Mat smallGoal = bim.scale(goal, scale);
	HighGui.imshow("small goal", smallGoal);
	Mat smallGoalEnlarged = bim.prepForRotationByEnlargingImageBoarder(smallGoal);
	HighGui.imshow("small goal Enlarged", smallGoalEnlarged);
	Mat rotatedGoal = bim.rotate(smallGoalEnlarged, 45, 1);
	HighGui.imshow("rotatedImage", rotatedGoal);
	
	int start_x = 0;
	int start_y = 0;
	int width = searchArea.width() / 2;
	int height = searchArea.height() / 2;
	Rect rect = new Rect(start_x, start_y, width, height);
	Mat roi = bim.getROI(goal, rect);
	HighGui.imshow("roi of searchArea", roi);
 */
public class BasicImageManipulation {
	//https://stackoverflow.com/questions/35666255/get-a-sub-image-using-opencv-java
	public Mat getROI(Mat src, Rect rect) {
		Mat dest = new Mat(src, rect);
		return dest;
	}
	
	public Size prepForScaleProportionally(Mat src, int desiredWidth) {
		int desiredHeight = (int) ((double) src.height() * ((double) desiredWidth / (double) src.width()));
		Size scale = new Size(desiredWidth, desiredHeight);
		return scale;
	}
	
	//https://stackoverflow.com/questions/20902290/how-to-resize-an-image-in-java-with-opencv
	public Mat scale(Mat src, Size scale) {
		Mat dest = new Mat();
		Imgproc.resize(src, dest, scale);
		return dest;
	}
	
	public Mat enlargeImageBoader(Mat src, Mat template) {
		System.out.println("EnlageImageBoarder - src width: "+src.width()+" - height: "+src.height());
		int newWidth = src.width()*2;
		int newHeight = src.height()*2;
		//The template cannot be bigger than the window we are searching in.
		if(template.width()>newWidth) {
			newWidth = template.width();
		}
		
		if(template.height()>newHeight) {
			newHeight = template.height();
		}
		
		int centerX = newWidth/2;
		int centerY = newHeight/2;
		
		System.out.println("Enlarging new width: "+newWidth+", new height: "+newHeight);
		Mat dest = new Mat(new Size(newWidth, newHeight), src.type(), new Scalar(0,0,0));
		
		int rowStart = centerX - (src.width()/2);
		int rowEnd = rowStart+src.width();
		int colStart = centerY - (src.height()/2);
		int colEnd = colStart + src.height();
		
//		int rowStart = newWidth/4;
//		int rowEnd = rowStart+src.width();
//		int colStart = newHeight/4;
//		int colEnd = colStart + src.height();
		System.out.println("Copying src image to rowStart: "+rowStart+", colStart: "+colStart);
		System.out.println("Copying src image to rowEnd: "+rowEnd+", colStart: "+colEnd);
		Rect roi = new Rect(new Point(rowStart, colStart), new Point(rowEnd, colEnd));
		src.copyTo(dest.submat(roi));
//		src.copyTo(dest.submat(rowStart, rowEnd, colStart, colEnd));
		return dest;
	}
	
	/*Got this idea from a python api description - though I didn't find the actual code
	 * 
	 * The api said:
	 * Rotate image without cropping OpenCV, Create new square image with dimension
	 * = diagonal of your initial image. Draw initial image into the center of new
	 * image. Rotate new image. Rotate images (correctly) with OpenCV and Python. In
	 * the remainder of this blog post I’ll discuss common issues that you may run
	 * into when rotating images with OpenCV and Python. Specifically, we’ll be
	 * examining the problem of what happens when the corners of an image are “cut
	 * off” during the rotation process.
	 */
	
	//https://answers.opencv.org/question/212043/copy-smaller-mat-to-part-of-bigger-mat/
	public Mat prepForRotationByEnlargingImageBoarder(Mat src) {
		double diagonalLength = Math.sqrt((src.width()*src.width() + src.height()*src.height()));
		Mat dest = new Mat(new Size(diagonalLength, diagonalLength), src.type(), new Scalar(0,0,0));
		int rowStart = (int)diagonalLength/2 - src.rows()/2;
		int rowEnd = rowStart+src.rows();
		int colStart = (int)diagonalLength/2 - src.cols()/2;
		int colEnd = colStart + src.cols();
		src.copyTo(dest.submat(rowStart, rowEnd, colStart, colEnd));
		return dest;
	}
	
	//https://www.tutorialspoint.com/javaexamples/rotate_image.htm
	public Mat rotate(Mat src, double degrees, double scale) {
		Mat dest = new Mat();
		Mat matrix = Imgproc.getRotationMatrix2D(new Point(src.width() / 2, src.height() / 2), degrees, scale);

		// Rotating the given image
		Imgproc.warpAffine(src, dest, matrix, new Size(src.cols(), src.rows()));
		return dest;
	}
	
}
