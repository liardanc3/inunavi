package com.maru.inunavi.service;

import com.maru.inunavi.entity.Navi;
import com.maru.inunavi.entity.NodePath;
import com.maru.inunavi.entity.Place;
import com.maru.inunavi.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NaviService {

    private final NaviRepository _NaviRepository;
    private final UserInfoRepository _UserInfoRepository;
    private final UserLectureRepository _UserLectureRepository;
    private final AllLectureRepository _AllLectureRepository;
    private final NodePathRepository _NodePathRepository;
    private final PlaceRepository _PlaceRepository;

    private ArrayList<String> epsg4326List = new ArrayList<>();
    private ArrayList<String> placeCodeList = new ArrayList<>();

    class pair implements Comparable<pair> {
        private double dist;
        private int node;
        pair(double dist, int node){ this.dist=dist; this.node=node;}
        public double getDist(){ return dist; }
        public int getNode(){ return node; }

        @Override
        public int compareTo(final pair p){
            if(dist == p.dist) return 1;
            return Double.compare(dist, p.dist);
        }
    }

    private String epsg3857_to_epsg4326(String _epsg3857){
        StringTokenizer s = new StringTokenizer(_epsg3857);

        String sX = s.nextToken(",");
        String sY = s.nextToken(",");

        double X = Double.parseDouble(sX);
        double Y = Double.parseDouble(sY);

        double E = 2.7182818284;
        double PARAM = 20037508.34;

        double LO = (X * 180) / PARAM;
        double LA = Y / (PARAM / 180);

        double EXPONENT = (Math.PI / 180) * LA;

        LA = Math.atan(Math.pow(E,EXPONENT));
        LA = LA / (Math.PI / 360);
        LA = LA - 90;

        String sLA = Double.toString(LA);
        String SLO = Double.toString(LO);

        return sLA+", "+SLO;
    }

    private double distanceNode(String _src, String _dst){
        try{
            StringTokenizer s = new StringTokenizer(_src);
            StringTokenizer d = new StringTokenizer(_dst);
            double lat1 = Double.parseDouble(s.nextToken(","));
            double lng1 = Double.parseDouble(s.nextToken(","));
            double lat2 = Double.parseDouble(d.nextToken(","));
            double lng2 = Double.parseDouble(d.nextToken(","));
            double radius = 6371.8;
            double toRadian = Math.PI / 180;
            double DLa = (lat2 - lat1) * toRadian;
            double DLo = (lng2 - lng1) * toRadian;
            double a = Math.sin(DLa/2) * Math.sin(DLa/2) + Math.cos(lat1*toRadian) * Math.cos(lat2*toRadian) * Math.sin(DLo/2) * Math.sin(DLo/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            return (radius * c) * 1000;
        }catch (Exception e){
            return -1;
        }
    }

    private NodePath dijkstraPatial(int src, int dst, ArrayList<ArrayList<Integer>> AL, int len){
        int[] pathTrace = new int[len+2];
        double[] distArray = new double[len+2];
        for(int i=1; i<distArray.length; i++)
            distArray[i] = 1e9;
        distArray[src]=0.f;
        PriorityQueue<pair> pq = new PriorityQueue<>();
        Stack<String> st = new Stack<>();
        pq.add(new pair(0,src));
        while(!pq.isEmpty()){
            double dist = pq.peek().getDist();
            int now = pq.peek().getNode();
            pq.remove();

            if(dist < distArray[now]) continue;

            String now_Epsg4326 = epsg4326List.get(now-1);
            now_Epsg4326 = now_Epsg4326.substring(1,now_Epsg4326.length()-1);

            for(int i=0; i<AL.get(now).size(); i++) {
                int next = AL.get(now).get(i);
                if(next==0) break;

                String next_Epsg4326 = epsg4326List.get(next-1);
                next_Epsg4326 = next_Epsg4326.substring(1,next_Epsg4326.length()-1);
                double _dist = distanceNode(now_Epsg4326,next_Epsg4326);
                if(dist+_dist < distArray[next]){
                    pathTrace[next]=now;
                    distArray[next]=dist+_dist;
                    pq.add(new pair(dist+_dist,next));
                    if(next==dst){
                        i=1000000;
                        pq.clear();
                    }
                }
            }
        }
        String src2dst = Integer.toString(src)+"to"+Integer.toString(dst);
        Double dist = distArray[dst];
        String path = "";
        int idx = dst;
        while(idx!=0){
            String epsg4326 = epsg4326List.get(idx-1);
            st.push(epsg4326);
            idx = pathTrace[idx];
        }
        while(!st.isEmpty()){
            path += st.peek()+",";
            st.pop();
        }
        path = path.substring(0,path.length()-1);
        NodePath _NodePath = new NodePath(src2dst,dist,path);
        return _NodePath;
    }

    private boolean dijkstraOverall(ArrayList<ArrayList<Integer>> AL){
        try{
            _NodePathRepository.deleteAll();
            _NodePathRepository.deleteINCREMENT();
            int len = (int)_NaviRepository.count();
            ArrayList<NodePath> _NAL = new ArrayList<>();
            for(int i=1; i<=len; i++) {
                for(int j=1; j<=len; j++){
                    if(i==j) continue;
                    _NAL.add(dijkstraPatial(i,j,AL,len));
                    //_NodePathRepository.save(dijkstraPatial(i,j,AL,len));
                }
                // log
                if(i==1 || i==len || i%50==0)
                    System.out.println("Dijkstra from "+i+" is Done.");
            }
            _NodePathRepository.saveAll(_NAL);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Place> updatePlace(){

        _PlaceRepository.deleteAll();
        _PlaceRepository.deleteINCREMENT();

        List<Place> PL = new ArrayList<Place>();

        try {
            InputStream inputStream = new ClassPathResource("_PLACE.txt").getInputStream();
            File file =File.createTempFile("_PLACE",".txt");
            try {
                FileUtils.copyInputStreamToFile(inputStream, file);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = "";
            line = bufReader.readLine();
            while ((line = bufReader.readLine()) != null) {

                List<String> csv = new ArrayList<>();
                StringTokenizer s = new StringTokenizer(line);

                // placeCode title sort distance location time callNum 비고
                for (int i = 0; i <= 7; i++) {
                    String tmp = s.nextToken("\t");
                    if(tmp.charAt(0)=='"')
                        tmp = tmp.substring(1,tmp.length()-1);
                    csv.add(tmp);
                }

                Place _Place = new Place(csv);
                PL.add(_Place);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        _PlaceRepository.saveAll(PL);
        return _PlaceRepository.findAll();
    }

    public List<Navi> updateNavi() {

        _NaviRepository.deleteAll();
        _NaviRepository.deleteINCREMENT();

        List<Navi> NL = new ArrayList<Navi>();
        ArrayList<ArrayList<Integer>> AL = new ArrayList<>();
        for(int i=1; i<=550; i++)
            AL.add(new ArrayList<Integer>());

        try {
            InputStream inputStream = new ClassPathResource("_ALLNODE.txt").getInputStream();
            File file =File.createTempFile("_ALLNODE",".txt");
            try {
                FileUtils.copyInputStreamToFile(inputStream, file);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = "";
            line = bufReader.readLine();
            while ((line = bufReader.readLine()) != null) {

                List<String> csv = new ArrayList<>();
                StringTokenizer s = new StringTokenizer(line);

                // nodeNum, nearNode, placeCode
                for (int i = 0; i <= 3; i++) {
                    String tmp = s.nextToken("\t");
                    csv.add(tmp);
                }

                // nearNode
                String _nearNode = csv.get(1);
                if(_nearNode.charAt(0)=='"')
                    _nearNode = _nearNode.substring(1,_nearNode.length()-1);
                if(_nearNode!="0"){
                    StringTokenizer nearTmp = new StringTokenizer(_nearNode);
                    while(nearTmp.hasMoreTokens()){
                        int next = Integer.parseInt(nearTmp.nextToken(","));
                        AL.get(NL.size()+1).add(next);
                        AL.get(next).add(NL.size()+1);
                    }
                }

                // 좌표계 변환
                String _epsg3857 = csv.get(2).substring(1,csv.get(2).length()-1);
                String _epsg4326 = epsg3857_to_epsg4326(_epsg3857);
                epsg4326List.add(_epsg4326);

                // placeCode
                String _placeCode = csv.get(3);
                if(_placeCode.charAt(0)=='"')
                    _placeCode = _placeCode.substring(1,_placeCode.length()-1);
                placeCodeList.add(_placeCode);

                Navi _Navi = new Navi(_nearNode,csv.get(2),_epsg4326,_placeCode);
                NL.add(_Navi);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        _NaviRepository.saveAll(NL);
        dijkstraOverall(AL);
        return _NaviRepository.findAll();
    }

    public Map<String, List<NodePath>> getRoot(String startPlaceCode, String endPlaceCode, String startLocation, String endLocation){
        startPlaceCode = startPlaceCode.substring(1,startPlaceCode.length()-1);
        endPlaceCode = endPlaceCode.substring(1,endPlaceCode.length()-1);
        startLocation = startLocation.substring(1,startLocation.length()-1);
        endLocation = endLocation.substring(1,endLocation.length()-1);
        Map<String, List<NodePath>> retGetPath = new HashMap<>();
        boolean startWithLocation = startPlaceCode.equals("LOCATION");
        boolean endWithLocation = endPlaceCode.equals("LOCATION");
        int len = (int)_NaviRepository.count();
        if(epsg4326List.isEmpty()){
            for(int i=1; i<= len; i++)
                epsg4326List.add(_NaviRepository.getById(i).getEpsg4326());
        }
        if(placeCodeList.isEmpty()){
            for(int i=1; i<=len; i++)
                placeCodeList.add(_NaviRepository.getById(i).getPlaceCode());
        }

        // Location to Location
        if(startWithLocation && endWithLocation){
            Double minDistFromStart = 1e9;
            Double minDistFromEnd = 1e9;
            int minNodeFromStart = -1;
            int minNodeFromEnd = -1;
            for(int i=0; i<len; i++){
                Double distFromStart = distanceNode(startLocation, epsg4326List.get(i));
                Double distFromEnd = distanceNode(endLocation, epsg4326List.get(i));
                if(distFromStart < minDistFromStart){
                    minDistFromStart = distFromStart;
                    minNodeFromStart = i+1;
                }
                if(distFromEnd < minDistFromEnd){
                    minDistFromEnd = distFromEnd;
                    minNodeFromEnd = i+1;
                }
            }

            NodePath _NodePath = _NodePathRepository.findBySrc2dst(minNodeFromStart+"to"+minNodeFromEnd);

            Double _Dist = _NodePath.getDist();
            _Dist += distanceNode(startLocation, epsg4326List.get(minNodeFromStart-1));
            _Dist += distanceNode(endLocation, epsg4326List.get(minNodeFromEnd-1));

            String _Path = startLocation+',';
            _Path += _NodePath.getRoute();
            _Path += ','+endLocation;

            NodePath _retNodePath = new NodePath("",_Dist,_Path);
            List<NodePath> _retList = new ArrayList<>();
            _retList.add(_retNodePath);
            retGetPath.put("response",_retList);
        }

        // Location to placeCode
        if(startWithLocation && !endWithLocation){
            Double minDistFromStart = 1e9;
            int minNodeFromStart = -1;

            for(int i=0; i<len; i++){
                Double distFromStart = distanceNode(startLocation, epsg4326List.get(i));

                if(distFromStart < minDistFromStart){
                    minDistFromStart = distFromStart;
                    minNodeFromStart = i+1;
                }
            }

            ArrayList<Integer> targetNodeList = new ArrayList<>();
            for(int i=0; i<placeCodeList.size(); i++){
                StringTokenizer st = new StringTokenizer(placeCodeList.get(i));
                while(st.hasMoreTokens()){
                    if(st.nextToken(",").equals(endPlaceCode)) {
                        targetNodeList.add(i + 1);
                        break;
                    }
                }
            }

            Double minDist = 1e9;
            String minSrc2dst = "";
            for(int i=0; i<targetNodeList.size(); i++){
                String targetSrc2dst = minNodeFromStart+"to"+targetNodeList.get(i);
                Double targetDist = _NodePathRepository.findBySrc2dst(targetSrc2dst).getDist();
                if(targetDist < minDist){
                    minSrc2dst = targetSrc2dst;
                    minDist = targetDist;
                }
            }

            NodePath _NodePath = _NodePathRepository.findBySrc2dst(minSrc2dst);
            Double _Dist = _NodePath.getDist();
            _Dist += distanceNode(startLocation, epsg4326List.get(minNodeFromStart-1));

            String _Path = startLocation+',';
            _Path += _NodePath.getRoute();

            NodePath _retNodePath = new NodePath("",_Dist,_Path);
            List<NodePath> _retList = new ArrayList<>();
            _retList.add(_retNodePath);
            retGetPath.put("response",_retList);
        }

        // placeCode to Location
        if(!startWithLocation && endWithLocation){
            Double minDistFromEnd = 1e9;
            int minNodeFromEnd = -1;

            for(int i=0; i<len; i++){
                Double distFromEnd = distanceNode(endLocation, epsg4326List.get(i));

                if(distFromEnd < minDistFromEnd){
                    minDistFromEnd = distFromEnd;
                    minNodeFromEnd = i+1;
                }
            }

            ArrayList<Integer> targetNodeList = new ArrayList<>();
            for(int i=0; i<placeCodeList.size(); i++){
                StringTokenizer st = new StringTokenizer(placeCodeList.get(i));
                while(st.hasMoreTokens()){
                    if(st.nextToken(",").equals(startPlaceCode)) {
                        targetNodeList.add(i + 1);
                        break;
                    }
                }
            }

            Double minDist = 1e9;
            String minSrc2dst = "";
            for(int i=0; i<targetNodeList.size(); i++){
                String targetSrc2dst = minNodeFromEnd+"to"+targetNodeList.get(i);
                Double targetDist = _NodePathRepository.findBySrc2dst(targetSrc2dst).getDist();
                if(targetDist < minDist){
                    minSrc2dst = targetSrc2dst;
                    minDist = targetDist;
                }
            }

            NodePath _NodePath = _NodePathRepository.findBySrc2dst(minSrc2dst);
            Double _Dist = _NodePath.getDist();
            _Dist += distanceNode(endLocation, epsg4326List.get(minNodeFromEnd-1));

            String _Path = _NodePath.getRoute();
            _Path += ","+endLocation;

            NodePath _retNodePath = new NodePath("",_Dist,_Path);
            List<NodePath> _retList = new ArrayList<>();
            _retList.add(_retNodePath);
            retGetPath.put("response",_retList);
        }

        // placeCode to placeCode
        if(!startWithLocation && !endWithLocation){
            ArrayList<Integer> startNodeList = new ArrayList<>();
            ArrayList<Integer> endNodeList = new ArrayList<>();

            ArrayList<Integer> targetNodeList = new ArrayList<>();
            for(int i=0; i<placeCodeList.size(); i++){
                StringTokenizer st = new StringTokenizer(placeCodeList.get(i));
                while(st.hasMoreTokens()){
                    String now = st.nextToken(",");
                    if(now.equals(startPlaceCode))
                        startNodeList.add(i+1);
                    if(now.equals(endPlaceCode))
                        endNodeList.add(i + 1);
                }
            }

            String _src2dst = "";
            Double minDist = 1e9;
            for(int i=0; i<startNodeList.size(); i++){
                for(int j=0; j<endNodeList.size(); j++){
                    int _src = startNodeList.get(i);
                    int _dst = endNodeList.get(j);
                    System.out.println(_src+"to"+_dst);
                    double targetDist = _NodePathRepository.findBySrc2dst(_src+"to"+_dst).getDist();
                    if(targetDist < minDist){
                        minDist = targetDist;
                        _src2dst = _src+"to"+_dst;
                    }
                }
            }

            NodePath _retNodePath = _NodePathRepository.findBySrc2dst(_src2dst);
            List<NodePath> _retList = new ArrayList<>();
            _retList.add(_retNodePath);
            retGetPath.put("response",_retList);
        }

        return retGetPath;
    }


}
