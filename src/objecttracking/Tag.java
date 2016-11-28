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

import com.googlecode.javacv.cpp.opencv_core.CvPoint;


public class Tag {
    private CvPoint centroide;
    private String nome;
    private int area;

    public Tag(CvPoint centroide, String nome, int area) {
        this.centroide = centroide;
        this.nome = nome;
        this.area = area;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public CvPoint getCentroide() {
        return centroide;
    }

    public void setCentroide(CvPoint centroide) {
        this.centroide = centroide;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    

}
