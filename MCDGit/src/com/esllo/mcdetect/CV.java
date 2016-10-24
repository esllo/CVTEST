package com.esllo.mcdetect;

import static org.opencv.core.Core.NATIVE_LIBRARY_NAME;
import static org.opencv.core.Core.inRange;
import static org.opencv.core.Core.putText;
import static org.opencv.core.Core.rectangle;
import static org.opencv.core.Core.split;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2HSV;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2BGR;
import static org.opencv.imgproc.Imgproc.RETR_LIST;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.findContours;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class CV {

	final static int MIN = 0;
	final static int MAX = 255;

	static {
		System.loadLibrary(NATIVE_LIBRARY_NAME);
	}

	public static Mat hsvIn(BufferedImage bi, Scalar lower, Scalar upper) {
		return hsvIn(bi, lower.val[0], lower.val[1], lower.val[2], upper.val[0], upper.val[1], upper.val[2]);
	} 

	public static Mat hsvIn(BufferedImage bi, double val, double val2, double val3, double val4, double val5,
			double val6) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, ((DataBufferByte) bi.getRaster().getDataBuffer()).getData());
		Mat hsv = new Mat(mat.size(), CvType.CV_8UC3);
		Mat msk = new Mat(hsv.size(), CvType.CV_8U, new Scalar(255d));
		cvtColor(mat, hsv, COLOR_BGR2HSV);
		ArrayList<Mat> lays = new ArrayList<Mat>();
		split(hsv, lays);
		inRange(hsv, new Scalar(val, val2, val3), new Scalar(val4, val5, val6), msk);
		return msk;
	}

	public static Mat toMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, ((DataBufferByte) bi.getRaster().getDataBuffer()).getData());
		cvtColor(mat, mat, COLOR_RGB2BGR);
		return mat;
	}

	public static BufferedImage toBufferedImage(Mat mat) {
		BufferedImage bo = new BufferedImage(mat.cols(), mat.rows(), (mat.channels() < 2) ? BufferedImage.TYPE_BYTE_GRAY
				: BufferedImage.TYPE_3BYTE_BGR);
		byte[] data = new byte[mat.cols() * mat.rows() * (int) mat.elemSize()];
		mat.get(0, 0, data);
		bo.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
		return bo;
	}

	public static void findContour(Mat target, Mat draw, String tag) {
		List<MatOfPoint> contours = new ArrayList<>();
		findContours(target.clone(), contours, new Mat(), RETR_LIST, CHAIN_APPROX_SIMPLE);
		for (int i = 0; i < contours.size(); i++) {
			if (contourArea(contours.get(i)) > 100) {
				Rect rect = boundingRect(contours.get(i));
				rectangle(draw, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
						new Scalar(20, 150, 20), 3);
				Size tsize = Core.getTextSize(tag, Core.FONT_HERSHEY_DUPLEX, 1.5, 3, null);
				double pox = rect.x + (rect.width / 2 - tsize.width / 2);
				double poy = rect.y + rect.height / 2;
				putText(draw, tag, new Point(pox, poy), Core.FONT_HERSHEY_DUPLEX, 1.5, new Scalar(20, 20, 20), 3);
			}
		}
	}

	final static class ColorValue {
		final static Scalar blueLow = new Scalar(102, 0, 0);
		final static Scalar blueHigh = new Scalar(133, 255, 255);
		final static Scalar greenLow = new Scalar(39, 50, 50);
		final static Scalar greenHigh = new Scalar(78, 220, 200);
		final static Scalar yellowLow = new Scalar(20, 124, 123);
		final static Scalar yellowHigh = new Scalar(38, 255, 255);
		final static Scalar redLow = new Scalar(0, 200, 0);
		final static Scalar redHigh = new Scalar(19, 255, 255);
		final static Scalar whiteLow = new Scalar(0, 0, 0);
		final static Scalar whiteHigh = new Scalar(0, 0, 255);
	}

}
