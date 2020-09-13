package org.trackerlounge;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Main {

	public static String getCurrentPath() {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
		return s;
	}

	public static String getResourcePath() {
		String s = getCurrentPath();
		String path = s + "\\resources\\";
		return path;
	}

	public static Mat loadImage(String file) {
		Imgcodecs imageCodecs = new Imgcodecs();
		Mat image = imageCodecs.imread(file);
		return image;
	}

	public static Mat loadGrayscaleImage(String file) {
		Mat image = Imgcodecs.imread(file, Imgcodecs.IMREAD_GRAYSCALE);
		return image;
	}

	public static void main(String[] args) {
		System.out.println("OpenCVTemplateMatch");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String filename1 = getResourcePath() + "smallTrack.jpg";
//		String filename2 = getResourcePath() + "threeTracks.jpg";
//		String filename2 = getResourcePath() + "fourTracksTest.jpg";
		String filename2 = getResourcePath() + "threeTracksWithDiffRotations.jpg";
		Mat template = Main.loadGrayscaleImage(filename1);
		Mat originalScene = Main.loadGrayscaleImage(filename2);
		Mat scene = Main.loadGrayscaleImage(filename2);

		BasicImageManipulation bim = new BasicImageManipulation();
		
		if (true) { // If true search for multiple versions of the template that have been scaled
					// and rotated in different ways.
			long start = System.currentTimeMillis();
			List<TemplateMatch> allMatches = new ArrayList<>();
			//For this test, I will not rotate the template, I have commented the rotation piece out. 
			//This will allow the code to run to completion faster.
			for (int curWidth = template.width(); curWidth < template.width() + 1; curWidth = curWidth + 1) {
			//				for(int curWidth = template.width()-5; curWidth<template.width()+30; curWidth=curWidth+1) {
				System.out.println("Current Width: " + curWidth);
				for (double templateRotatedByDegree = 0; templateRotatedByDegree < 360; templateRotatedByDegree = templateRotatedByDegree
						+ 1) {
					
					int desiredWidth = curWidth;
					Size currentScale = bim.prepForScaleProportionally(template, desiredWidth);
					Mat scaledGoal = bim.scale(template, currentScale);
					Mat curTemplate = bim.prepForRotationByEnlargingImageBoarder(scaledGoal);
					curTemplate = bim.rotate(curTemplate, templateRotatedByDegree, 1F);
					List<TemplateMatch> matches = Main.search(curTemplate, scene, templateRotatedByDegree);
					allMatches.addAll(matches);
					System.out.print(".");
				}
				System.out.println();
			}

//			for (TemplateMatch m : allMatches) {
//				System.out.println(m.toString());
//			}
//			System.out.println();
//			System.out.println("---------------------- Remove duplicates ----------------------------");
//			int nPixels = 5;
//			List<TemplateMatch> matchesWithNoDuplicate = removeDuplicates(allMatches, nPixels);
//			for (TemplateMatch m : matchesWithNoDuplicate) {
//				System.out.println(m.toString());
//			}

			int nPixels = 5; //how many pixels away (+/-) from our first match can another match to be considered unique
			List<TemplateMatch> matchesWithNoDuplicate = removeDuplicates(allMatches, nPixels);
			Mat matchImg = null;
			for (TemplateMatch match : matchesWithNoDuplicate) {
				Imgproc.rectangle(scene, new Point(match.x, match.y),
						new Point(match.x + match.width, match.y + match.height), new Scalar(255, 255, 255)); 
			//				 We are in grayscale so Scalar only  response to change in the first value in  new Scalar(255, 255, 255)
				Rect rect = new Rect(new Point(match.getX(), match.getY()), new Point(match.getX()+match.getWidth(), match.getY()+match.getHeight()));
				matchImg = bim.getROI(originalScene, rect);
				matchImg = bim.rotate(matchImg, (-1*match.getTemplateRotatedByDegree()), 1F);
				HighGui.imshow("matchImg ("+match.getX()+", "+match.getY()+")", matchImg);
			}

			System.out.println();
			long end = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
			System.out.println("Execution time is " + formatter.format((end - start) / 1000d) + " seconds");
			System.out.println(" ---- OR ----");
			System.out.println("Execution time is " + formatter.format(((end - start) / 1000d) / 60d) + " minutes");
			System.out.println(" ---- OR ----");
			System.out
					.println("Execution time is " + formatter.format((((end - start) / 1000d) / 60d) / 60d) + " hours");
		} else {
			List<TemplateMatch> matches = Main.search(template, scene, 0F);
			for (TemplateMatch match : matches) {
				Imgproc.rectangle(scene, new Point(match.x, match.y),
						new Point(match.x + match.width, match.y + match.height), new Scalar(255, 255, 255)); 
			//				 We are in grayscale so Scalar only  response to change in the first value in  new Scalar(255, 255, 255)
			}
		}

		// HighGui.imshow("Template", template);
		// HighGui.imshow("Scene", scene);

		String resultFile = getResourcePath() + "result.jpg";
		Imgcodecs.imwrite(resultFile, scene);

		HighGui.imshow("result", scene);
		HighGui.waitKey(0);
	}

	public static List<TemplateMatch> removeDuplicates(List<TemplateMatch> matches, int nPixels) {
		List<TemplateMatch> result = new ArrayList<TemplateMatch>();

		for (TemplateMatch m : matches) {
			if (0 == result.size()) {
				result.add(m);
			} else {
				boolean addToResult = true;
				for (TemplateMatch r : result) {
					if (m.getX() <= r.getX() + nPixels && m.getX() >= r.getX() - nPixels) {
						if (m.getY() <= r.getY() + nPixels && m.getY() >= r.getY() - nPixels) {
							addToResult = false;
							break;
						}
					}
				}
				if (addToResult) {
					result.add(m);
				}
			}
		}
		return result;
	}

	public static List<TemplateMatch> search(Mat template, Mat scene, double templateRotatedByDegree) {
		Mat outputImage = new Mat();
		Imgproc.matchTemplate(scene, template, outputImage, Imgproc.TM_CCOEFF_NORMED);
		MinMaxLocResult mmr = Core.minMaxLoc(outputImage);

		double maxVal = mmr.maxVal;
		double rangeMax = maxVal;
		double threshold = 0.80;// I picked an arbitrary threshold.

		/*
		 * Code extracted from:
		 * https://github.com/openpnp/openpnp/blob/develop/src/main/java/org/openpnp/
		 * vision/pipeline/stages/MatchPartTemplate.java#L283
		 */
		List<TemplateMatch> matches = new ArrayList<>();
		for (java.awt.Point point : MatMaxima.matMaxima(outputImage, threshold, rangeMax)) {
			int x = point.x;
			int y = point.y;
			TemplateMatch match = new TemplateMatch(x, y, template.cols(), template.rows(), outputImage.get(y, x)[0],
					templateRotatedByDegree);
			matches.add(match);
		}

//		 for(TemplateMatch match : matches) {
//			 Imgproc.rectangle(scene, new Point(match.x, match.y), new Point(match.x+match.width, match.y+match.height),
//						new Scalar(255, 255, 255)); //We are in grayscale so Scalar only response to change in the first value.
//		 }
		return matches;
	}

}// End of Class
