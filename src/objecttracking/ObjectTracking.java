/*
 * Copyright (C) 2016 roscoche
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author Felipe Roscoche
 * @website roscoche.com
 */

package objecttracking;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvAbsDiff;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_OTSU;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvDilate;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvErode;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;

public class ObjectTracking {

    public static void main(String args[]) {
        TrackNTag objt = new TrackNTag();
        IplImage contorno;
        IplImage img, thresholdimg;
        IplImage firstFrame, background = null;
        IplImage aux;
        IplImage diffimg;
        boolean isFirstFrame = true;
        //Creating video window and defining the input video.
        CanvasFrame video = new CanvasFrame("Video");
        video.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        FrameGrabber grabber = new OpenCVFrameGrabber("videos/v4.avi");

        try {

            //Starting capture of the video and main loop.
            grabber.start();
            while (true) {
                //If it is first frame, then save it for posterior calculations.
                if (isFirstFrame) {
                    firstFrame = grabber.grab();
                    if (firstFrame != null) {
                        background = IplImage.create(firstFrame.width(), firstFrame.height(), IPL_DEPTH_8U, 1);
                        cvCvtColor(firstFrame, background, CV_RGB2GRAY);
                        cvThreshold(background, background, 127, 255, CV_THRESH_OTSU);
                        isFirstFrame = false;
                    } else {
                        break;
                    }
                } else {
                    img = grabber.grab();

                    if (img != null) {
                        thresholdimg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
                        aux = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
                        diffimg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
                        cvCvtColor(img, aux, CV_RGB2GRAY);
                        cvThreshold(aux, thresholdimg, 127, 255, CV_THRESH_OTSU);
                        cvAbsDiff(background, thresholdimg, diffimg);
                        cvErode(diffimg, diffimg, null, 7);
                        cvDilate(diffimg, diffimg, null, 9);
                        contorno = objt.trackBox(diffimg, img);
                    } else {
                        break;
                    }

                    video.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
                    video.showImage(contorno);

                }

            }
        } catch (FrameGrabber.Exception e) {
        }
    }
}
