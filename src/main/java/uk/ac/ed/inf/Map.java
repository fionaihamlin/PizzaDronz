package uk.ac.ed.inf;


import com.mapbox.geojson.*;


import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

import java.util.List;

public class Map {
    private  List<Point> restaurantCoords;
    private List<Point> centralArea;
    private  List<Polygon> noFlyZones;
    private static final Point APPLETON = Point.fromLngLat(-3.186874, 55.944494 );


    private static final String NAME_PROP = "name";
    private static final String SYMBOL_PROP = "marker-symbol";
    private static final String COLOR_PROP = "marker-color";
    private static List<Feature> featureList;


    public Map(Restaurant[] restaurants, Area[] noFly,  CentralArea centralArea) throws IOException {
        this.restaurantCoords = new ArrayList<>();
        this.centralArea = new ArrayList<>();
        this.noFlyZones = new ArrayList<>();
        this.featureList = new ArrayList<>();
        this.configure(restaurants, noFly, centralArea);
    }


    public List<Polygon> getNoFlyZones() {
        return this.noFlyZones;
    }



    public void configure(Restaurant[] restaurants, Area[] noFly, CentralArea centralArea) {

        var appleton = APPLETON;
        var appletonFt = Feature.fromGeometry(appleton);
        appletonFt.addStringProperty(NAME_PROP, "Appleton Tower");
        appletonFt.addStringProperty(SYMBOL_PROP, "building");
        appletonFt.addStringProperty(COLOR_PROP, "#fff00");
        featureList.add(appletonFt);

        var centralFt = Feature.fromGeometry(asPolygon(centralArea.getCentralAreaPoints()));
        centralFt.addStringProperty(NAME_PROP, "Central Area");
        centralFt.addStringProperty("fill", "none");
        featureList.add(centralFt);

        for(Restaurant restaurant : restaurants){
            var coord = restaurant.getPoint();
            this.restaurantCoords.add(coord);
            var ft = Feature.fromGeometry(coord);
            ft.addStringProperty(NAME_PROP, restaurant.getName());
            ft.addStringProperty(COLOR_PROP, Restaurant.color);
            ft.addStringProperty(SYMBOL_PROP, Restaurant.symbol);
            featureList.add(ft);
        }

        for(Area zone: noFly){
            var asPoints = zone.getAreaAsPoints();
            var asPoly = asPolygon(asPoints);
            this.noFlyZones.add(asPoly);
            var ft = Feature.fromGeometry(asPoly);
            ft.addStringProperty(NAME_PROP, zone.name());
            ft.addStringProperty("fill", NoFlyZones.fill);
            featureList.add(ft);
        }

    }




    public String droneMap(List<Point> flightpath){
        LineString asLineString = LineString.fromLngLats(flightpath);
        Feature dronePath = Feature.fromGeometry(asLineString);
        featureList.add(dronePath);
        return FeatureCollection.fromFeature(dronePath).toJson();
    }

    public static void writeMap() throws IOException {
        String path = "map-" +".geojson";
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(FeatureCollection.fromFeatures(featureList).toJson());
        fileWriter.close();
    }


    public Polygon asPolygon(List<Point> points){
        List<List<Point>> pointsList = new ArrayList<>();
        pointsList.add(points);
        return Polygon.fromLngLats(pointsList);
    }





}

