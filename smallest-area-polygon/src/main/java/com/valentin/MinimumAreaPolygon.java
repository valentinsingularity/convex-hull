package com.valentin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;


class Point {
	
	double x;
	double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

}

class PointComparatorByRef implements Comparator<Point> {
	//compare points by their polar angle (or radial distance in case of equal angles) with respect to the reference point
	Point ref;
	
	public int compare(Point a, Point b) {
		if ((a.y - ref.y)/(a.x - ref.x) < (b.y - ref.y)/(b.x - ref.x)) return -1;
		else if((a.y - ref.y)/(a.x - ref.x) == (b.y - ref.y)/(b.x - ref.x) && a.x < b.x) return -1;
		     else return 1;
	}
	
	public PointComparatorByRef(Point ref) {
		this.ref = ref;
	}
	
}

public class MinimumAreaPolygon {
	
	public static double getCrossProduct(Point p1, Point p2, Point p3) {
		return (p2.x - p1.x)*(p3.y - p1.y) - (p3.x - p1.x)*(p2.y - p1.y); //cross product of vectors p1p2 and p1p3 (determinant formula)
	}
	
	public static double computeTriangleArea(Point p1, Point p2, Point p3) {
		double a = Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
		double b = Math.sqrt(Math.pow(p3.x - p1.x, 2) + Math.pow(p3.y - p1.y, 2));
		double c = Math.sqrt(Math.pow(p3.x - p2.x, 2) + Math.pow(p3.y - p2.y, 2));
		double s = 0.5*(a + b + c);
		return Math.sqrt(s*(s-a)*(s-b)*(s-c));  //Heron's formula for the area of a triangle
	}
	
	public static void main(String[] args) throws JsonSyntaxException, IOException {
		
	    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
	    String line;
	    while((line = input.readLine()) != null) {
	    	
			List<Point> listOfPoints = new ArrayList<>();
			Gson gson = new Gson();
			Type collectionType = new TypeToken<ArrayList<Point>>(){}.getType();
			listOfPoints = gson.fromJson(line, collectionType);
			
			int refPos = 0;
			for(int i=0; i<listOfPoints.size(); i++)  //determine point with minimum x coordinate (reference point)
			{
				if(listOfPoints.get(i).x < listOfPoints.get(refPos).x  || (listOfPoints.get(i).x == listOfPoints.get(refPos).x 
						&& listOfPoints.get(i).y < listOfPoints.get(refPos).y ) ) {
					refPos = i ;
				}
			}
		
			Point referencePoint = listOfPoints.get(refPos);
			Collections.swap(listOfPoints, 0, refPos);
			Collections.sort(listOfPoints, new PointComparatorByRef(referencePoint));

			Stack<Point> polygonPoints = new Stack<Point>();
			polygonPoints.push(listOfPoints.get(0));
			polygonPoints.push(listOfPoints.get(1));
			for(int i=2; i<listOfPoints.size(); i++) {  //determine the smallest area polygon
				while(polygonPoints.size() > 2) { //backtracking loop
					int stackSize = polygonPoints.size();
					double crossProduct = getCrossProduct(polygonPoints.get(stackSize-2),polygonPoints.get(stackSize-1),listOfPoints.get(i));
					if(crossProduct < 0) {  //eliminate all points that lead to a right turn while traversing the polygon counterclockwise
						polygonPoints.pop();
					} else break;
				}
				polygonPoints.push(listOfPoints.get(i));
			}
			
			double polygonArea = 0;  //compute the area of the polygon
			for(int i=2; i<polygonPoints.size(); i++) {
				Point vertex1 = polygonPoints.get(0);
				Point vertex2 = polygonPoints.get(i-1);
				Point vertex3 = polygonPoints.get(i);
				polygonArea += computeTriangleArea(vertex1, vertex2, vertex3);
			}
			
			System.out.println(polygonArea);
			//System.err.println(polygonArea);
	
			/*
			for(int i=0; i<polygonPoints.size(); i++) 
				System.out.println(polygonPoints.get(i).x + "  " + polygonPoints.get(i).y);
            */
			
			// [ { "x": 5.2, "y": 0 }, { "x": 1.3, "y": 2.7 }, { "x": 1000000, "y": -20 } ]
		    // [ { "x": 8, "y": 3 }, { "x": 12, "y": 10 }, { "x": 4, "y": 3 }, { "x": 9, "y": 7 }, { "x": 1, "y": 1 }, { "x": 5, "y": 9 } ]
		}
	}
}
