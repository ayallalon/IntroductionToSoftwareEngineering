package renderer;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;
import geometries.Intersectable.GeoPoint;

import java.util.MissingResourceException;
import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * camera protracting rays thought a view plane
 */
public class Camera {

    private final Point p0;//camera eye
    private final Vector vTo; //vector pointing towards the scene
    private final Vector vUp; //vector pointing upwards
    private final Vector vRight; //vector pointing towards the right

    private double distance;
    private double width; // width for view plane
    private double height;// height for view plane

    private ImageWriter imageWriter;
    private RayTracer rayTracer;

    /**
     * constructor
     * @param p0 center of the camera
     * @param vTo dir camera
     * @param vUp Camera location
     */
    public Camera(Point p0, Vector vTo, Vector vUp) {
        if (!isZero(vUp.dotProduct(vTo))) {
            throw new IllegalArgumentException("vTo and vUp should be orthogonal.");
        }
        this.p0 = p0;
        this.vTo = vTo.normalize();
        this.vUp = vUp.normalize();

        vRight = this.vTo.crossProduct(this.vUp);
    }

    /**
     * set view plane Distance
     * @param distance
     * @return the distance
     */
    public Camera setVPDistance(double distance) {
        this.distance = distance;
        return this;
    }

    /**
     * set view plane Size
     * @param width for view plane
     * @param height for view plane
     * @return width and height
     */
    public Camera setVPSize(double width, double height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Constructing a ray through a pixel.
     * @param Nx pixels of the width in the view plane
     * @param Ny pixels of the height in the view plane
     * @return ray from the camera to pixel[i, j]
     */
    public Ray constructRay(int Nx, int Ny, int j, int i) {
        //image center
        Point Pc = p0.add(vTo.scale(distance));

        //Ratio (pixel width and height)
        double Ry = alignZero(height / Ny);
        double Rx = alignZero(width / Nx);

        //pixel [i, j] center
        Point Pij = Pc;

        //delta values for going to pixel[i,j] from pc.

        double yI = alignZero(-(i - (Ny - 1) / 2d) * Ry);
        double xJ = alignZero((j - (Nx - 1) / 2d) * Rx);

        if (!isZero(xJ)) {
            Pij = Pij.add(vRight.scale(xJ));
        }
        if (!isZero(yI)) {
            Pij = Pij.add(vUp.scale(yI));
        }

        return new Ray(p0, Pij.subtract(p0));
    }

    /**
     * set ImageWriter
     * @param imageWriter
     * @return imageWriter
     */
    public Camera setImageWriter(ImageWriter imageWriter) {
        this.imageWriter = imageWriter;
        return this;
    }

    /**
     * set RayTracer
     * @param rayTracer
     * @return RayTracer
     */
    public Camera setRayTracer(RayTracer rayTracer) {
        this.rayTracer = rayTracer;
        return this;
    }

    /**
     * Function writeToImage produces unoptimized png file of the image according to
     * pixel color matrix in the directory of the project
     */
    public void writeToImage() {
        if (imageWriter == null) {
            throw new MissingResourceException("missing imagewriter", "Camera", "in print Grid");
        }
        imageWriter.writeToImage();
    }

    /**
     * paints the image as a grid according to the wanted interval and color of grid lines
     * @param interval length of wanted interval
     * @param color wanted color for grid lines
     */
    public void printGrid(int interval, Color color) {

//        if (imageWriter == null) {
//            throw new MissingResourceException("missing imagewriter", "Camera", "in print Grid");
//        }
//
//        for (int row = 0; row < imageWriter.getNx(); row++) {
//            for (int col = 0; col < imageWriter.getNy(); col++) {
//                if (row % interval == 0 || col % interval == 0) {
//                    imageWriter.writePixel(row, col, color);
//                }
//            }
//            imageWriter.writeToImage();
//        }
        if (imageWriter == null) {
            throw new MissingResourceException("missing imagewriter", "Camera", "in print Grid");
        }

        for (int i = 0; i < imageWriter.getNx(); i++) {
            for (int j = 0; j < imageWriter.getNy(); j++) {
                if (i % interval == 0 || j % interval == 0) {
                    imageWriter.writePixel(i, j, color);
                }
            }
            imageWriter.writeToImage();
        }
    }


    /**
     * The actual rendering function , according to data received from the ray tracer - colours each
     * pixel appropriately thus
     * rendering the image
     */
    public void renderImage() {
        try {
            if (imageWriter == null) {
                throw new MissingResourceException("missing resource", ImageWriter.class.getName(),
                        "");
            }
            if (rayTracer == null) {
                throw new MissingResourceException("missing resource", RayTracer.class.getName(),
                        "");
            }

            //rendering the image
            int Nx = imageWriter.getNx();
            int Ny = imageWriter.getNy();

            for (int row = 0; row < Nx; row++) {
                for (int col = 0; col < Ny; col++) {
                    castRay(Nx,Ny,row,col);//check the parameter
                }
            }
        } catch (MissingResourceException e) {
            throw new UnsupportedOperationException("Not implemented yet" + e.getClassName());
        }
    }

    /**
     *
     * @param Nx
     * @param Ny
     * @param row
     * @param col
     */
    private void castRay(int Nx, int Ny, int row , int col) {
        Ray ray = constructRay(Nx, Ny,row, col);
        Color color = rayTracer.traceRay(ray);
        imageWriter.writePixel( row,col, color);
    }
}
