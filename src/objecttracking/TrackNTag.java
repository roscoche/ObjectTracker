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

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvFont;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvPutText;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_LIST;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY_INV;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrackNTag {

    
    ArrayList<Tag> allTags;
    boolean usadas[] = null;

    public TrackNTag() {
        this.allTags = new ArrayList<>();

    }

    public int getBestTag(CvPoint centroid, int area) {
        CvPoint auxpoint;
        Tag auxtag;
        double distance, bestDistance = 999;
        int best = -1;
        for (int i = 0; i < allTags.size(); i++) {
            if (!usadas[i]) {
                auxtag = allTags.get(i);
                auxpoint = auxtag.getCentroide();
                distance = sqrt(pow((centroid.x() - auxpoint.x()), 2) + pow((centroid.y() - auxpoint.y()), 2));
                System.out.println("distance"+distance);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = i;
                }
            }

        }
        return best;

    }

    public IplImage trackBox(IplImage diffimg, IplImage original) {
        IplImage aux = original.clone();
        IplImage inverted = cvCreateImage(cvGetSize(original), IPL_DEPTH_8U, 1);
        cvThreshold(diffimg, inverted, 70, 255, CV_THRESH_BINARY_INV);
        CvSeq cvSeq = new CvSeq();
        CvMemStorage memory = CvMemStorage.create();
        int newnTags = cvFindContours(inverted, memory, cvSeq, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
        if (newnTags > allTags.size()) usadas = new boolean[newnTags];
        else if (newnTags < allTags.size()) usadas = new boolean[allTags.size()];
        else Arrays.fill(usadas, false);
        int n =1;
        System.out.println("new ntags "+newnTags);
        System.out.println("alltags "+allTags.size());
        for (CvSeq c = cvSeq; c!= null; c = c.h_next()) {
            int top = original.height(), right = 0, bottom = 0, left = original.width();
            for (int i = 0; i < c.total(); i++) {
                CvPoint p = new CvPoint(cvGetSeqElem(c, i));
                if (p.y() > bottom) bottom = p.y();
                if (p.y() < top) top = p.y();
                if (p.x() > right) right = p.x();
                if (p.x() < left) left = p.x();
            }
            int w = right - left;
            int h = bottom - top;
            CvPoint centroide = new CvPoint(w / 2, h / 2);
            if (n > allTags.size()) {
                allTags.add(new Tag(centroide, UUID.randomUUID().toString().substring(0, 4), w * h));
                
                System.out.println("nova etiqueta");
            }

            int bestTag = getBestTag(centroide, w * h);

            System.out.println("bt" + bestTag);
            usadas[bestTag] = true;
            Tag newtag = allTags.get(bestTag);
            newtag.setCentroide(centroide);
            newtag.setArea(w * h);
            allTags.set(bestTag, newtag);
            CvFont font;
            //font = cvFont("Times", 12, CvScalar.MAGENTA, CV_FONT_NORMAL, CV_STYLE_NORMAL,0);
            font = new CvFont(0, 1, 1, 5, 2, 0);
            cvRectangle(aux, new CvPoint(left, top), new CvPoint(right, bottom), CvScalar.BLUE, 2, 0, 0);
            cvPutText(aux, allTags.get(bestTag).getNome(), new CvPoint(left, top), font, CvScalar.MAGENTA);
            n++;
        }
        System.out.println("n="+n);
        if (newnTags < allTags.size()) {
            for (int i = 0; i < usadas.length; i++) {
                if (!usadas[i]) {
                    System.out.println("nao usado:"+i);
                    System.out.println(allTags.get(i).getNome());
                    allTags.remove(i);
                }
            }
        }
        //nTags=n;
        try {
            Thread.sleep(20);

        } catch (InterruptedException ex) {
            Logger.getLogger(TrackNTag.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;

    }
}
