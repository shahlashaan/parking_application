package com.example.smartpark;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SortDistances {

    Node root;
    ArrayList<String>locations= new ArrayList<String>();
    class Node{
        double key;
        String location;
        Node leftChild;
        Node rightChild;

        public Node(double key,String location){
            this.key = key;
            this.location=location;
        }
    }
    public SortDistances(){

    }
    public void addNode(double key,String location){
        Node newNode = new Node(key,location);

        if(root==null){
            root=newNode;
        }else{
            Node focusNode = root;
            Node parent;
            while(true){
                parent = focusNode;
                if(key<focusNode.key){
                    focusNode=focusNode.leftChild;
                    if(focusNode== null){
                        parent.leftChild=newNode;
                        return;
                    }
                }else{
                    focusNode=focusNode.rightChild;
                    if(focusNode==null){
                        parent.rightChild=newNode;
                        return;
                    }
                }

            }
        }
    }
    public void inOrder(Node focusNode){
        if(focusNode != null){
            inOrder(focusNode.leftChild);
            locations.add(focusNode.location);
            inOrder(focusNode.rightChild);
        }
    }
    public ArrayList sortLocations(List<MapPoints> locationsWithDistances){
        for(int i=0;i < locationsWithDistances.size();i++){
            MapPoints point = locationsWithDistances.get(i);
            addNode(point.distanceValue,point.strLocation);
        }
        inOrder(root);

        return locations;
    }
}
