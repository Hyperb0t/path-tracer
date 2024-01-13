package org.example;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.Triangulator;
//import org.poly2tri.Poly2Tri;
//import org.poly2tri.geometry.polygon.Polygon;
//import org.poly2tri.geometry.polygon.PolygonPoint;
//import org.poly2tri.triangulation.TriangulationPoint;
//import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.PointArray;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3f;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TriangulationExperiments {
    public static void main(String[] args) {
        // Prepare input data
//        List<PolygonPoint> l = Arrays.asList(new PolygonPoint(0, 0, 0),
//                new PolygonPoint(10, 0, 1),new PolygonPoint(10, 10, 2),new PolygonPoint(0, 10, 3));
//        Polygon polygon = new Polygon(l);
//        // Launch tessellation
//        Poly2Tri.triangulate(polygon);
//        // Gather triangles
//        List<DelaunayTriangle> triangles = polygon.getTriangles();
//        for(DelaunayTriangle t : triangles) {
//            for(TriangulationPoint p : t.points) {
//                System.out.print(p.getX() + " " + p.getY() + " " + p.getZ());
//                System.out.print("    index: " + t.index(p));
//                if(!l.contains(new PolygonPoint(p.getX(), p.getY(), p.getZ()))) {
//                    System.out.println("point is not in the list!");
//                }
//                System.out.println();
//            }
//            System.out.println();
//        }

        System.out.println("---------------------------------");

        Triangulator triangulator = new Triangulator();
        GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        Point3f[] points = new Point3f[]
                {
                        new Point3f(0,0,0),
                        new Point3f(10,0,1),
                        new Point3f(10,10,2),
                        new Point3f(0,10,3)
                };
        geometryInfo.setCoordinates(points);
        geometryInfo.setStripCounts(new int[]{points.length});
        triangulator.triangulate(geometryInfo);
        TriangleArray triangleArray = (TriangleArray)geometryInfo.getGeometryArray();
        for(int i = 0 ; i < triangleArray.getVertexCount(); i++) {
            if(i % 3 == 0) {
                System.out.println();
            }
            Point3f p = new Point3f();
            triangleArray.getCoordinate(i, p);
            System.out.println(p + "   index: " + Arrays.asList(points).indexOf(p));
        }


    }
}