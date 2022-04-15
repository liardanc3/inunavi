package com.maru.inunavi.service;

import com.maru.inunavi.entity.*;
import com.maru.inunavi.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    // 임시로 캐시대신 사용
    private ArrayList<String> epsg4326List = new ArrayList<>();
    private ArrayList<String> placeCodeList = new ArrayList<>();


    class _pair implements Comparable<_pair>{
        private double dist;
        private int node;
        private ArrayList<Integer> dst;
        private double heuristics;
        _pair(double dist, int node, ArrayList<Integer> dst){
            this.dist=dist; this.node=node;
            this.dst =dst;

            double _heuristics = 10*distanceNode(epsg4326List.get(node),epsg4326List.get(dst.get(0)));

            this.heuristics = dist + _heuristics;
        }
        public double getDist(){ return dist; }
        public int getNode(){ return node; }

        @Override
        public int compareTo(final _pair p){
            return Double.compare(heuristics, p.heuristics);
        }
    }
    private NodePath Astar(int src, ArrayList<Integer> dst){
        List<Navi> tmp = new ArrayList<>();
        if(epsg4326List.isEmpty()){
            if(tmp.isEmpty()) tmp.addAll(_NaviRepository.findAll());
            for (Navi navi : tmp) epsg4326List.add(navi.getEpsg4326());
        }
        if(placeCodeList.isEmpty()){
            if(tmp.isEmpty()) tmp.addAll(_NaviRepository.findAll());
            for (Navi navi : tmp) placeCodeList.add(navi.getPlaceCode());
        }
        int len = (int) _NaviRepository.count();
        int idx = 0;
        int[] pathTrace = new int[len+2];
        int[] visited = new int[len+2];
        double[] distArray = new double[len+2];
        ArrayList<Navi> naviList = new ArrayList<>();
        naviList.addAll(_NaviRepository.findAll());
        for(int i=1; i<distArray.length; i++)
            distArray[i] = 1e9;
        distArray[src]=0.f;

        PriorityQueue<_pair> pq = new PriorityQueue<>();
        Stack<String> st = new Stack<>();

        pq.add(new _pair(0, src, dst));
        visited[src]=1;
        while(!pq.isEmpty()){
            double dist = pq.peek().getDist();
            int now = pq.peek().getNode();
            pq.remove();

            for(int i=0; i<dst.size(); i++){
                if(now==dst.get(i)){
                    idx = now;
                    pq.clear();
                    break;
                }
            }

            if(idx!=0) break;
            if(dist < distArray[now]) continue;

            String now_Epsg4326 = epsg4326List.get(now-1);
            now_Epsg4326 = now_Epsg4326.substring(1,now_Epsg4326.length()-1);

            String nearNode = naviList.get(now-1).getNearNode();
            StringTokenizer s = new StringTokenizer(nearNode);
            while(s.hasMoreTokens()){
                int next = Integer.parseInt(s.nextToken(","));
                if(next==0) continue;

                String next_Epsg4326 = epsg4326List.get(next-1);
                next_Epsg4326 = next_Epsg4326.substring(1,next_Epsg4326.length()-1);
                double _dist = distanceNode(now_Epsg4326,next_Epsg4326);
                if(dist+_dist < distArray[next] && visited[next]==0){
                    pathTrace[next]=now;
                    visited[next]=1;
                    distArray[next]=dist+_dist;
                    pq.add(new _pair(dist + _dist, next, dst));
                }
            }
        }

        Double dist = distArray[idx];
        String path = "";

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


        String _isArrived = dist < 15 ? "true" : "false";
        NodePath _NodePath = new NodePath("",_isArrived,dist,path);
        return _NodePath;
    }

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
    private NodePath dijkstraPartial(int src, ArrayList<Integer> dst){
        List<Navi> tmp = new ArrayList<>();
        if(epsg4326List.isEmpty()){
            if(tmp.isEmpty())
                tmp.addAll(_NaviRepository.findAll());
            for(int i=0; i<tmp.size(); i++)
                epsg4326List.add(tmp.get(i).getEpsg4326());
        }
        if(placeCodeList.isEmpty()){
            if(tmp.isEmpty())
                tmp.addAll(_NaviRepository.findAll());
            for(int i=0; i<tmp.size(); i++)
                placeCodeList.add(tmp.get(i).getPlaceCode());
        }
        int len = (int) _NaviRepository.count();
        int idx = 0;
        int[] pathTrace = new int[len+2];
        double[] distArray = new double[len+2];
        ArrayList<Navi> naviList = new ArrayList<>();
        naviList.addAll(_NaviRepository.findAll());
        for(int i=1; i<distArray.length; i++)
            distArray[i] = 1e9;
        distArray[src]=0.f;

        PriorityQueue<pair> pq = new PriorityQueue<>();
        Stack<String> st = new Stack<>();
        pq.add(new pair(0, src));
        while(!pq.isEmpty()){
            double dist = pq.peek().getDist();
            int now = pq.peek().getNode();
            pq.remove();

            for(int i=0; i<dst.size(); i++){
                if(now==dst.get(i)){
                    idx = now;
                    pq.clear();
                    break;
                }
            }

            if(idx!=0) break;
            if(dist < distArray[now]) continue;

            String now_Epsg4326 = epsg4326List.get(now-1);
            now_Epsg4326 = now_Epsg4326.substring(1,now_Epsg4326.length()-1);

            String nearNode = naviList.get(now-1).getNearNode();
            StringTokenizer s = new StringTokenizer(nearNode);
            while(s.hasMoreTokens()){
                int next = Integer.parseInt(s.nextToken(","));
                if(next==0) continue;

                String next_Epsg4326 = epsg4326List.get(next-1);
                next_Epsg4326 = next_Epsg4326.substring(1,next_Epsg4326.length()-1);
                double _dist = distanceNode(now_Epsg4326,next_Epsg4326);
                if(dist+_dist < distArray[next]){
                    pathTrace[next]=now;
                    distArray[next]=dist+_dist;
                    pq.add(new pair(dist + _dist, next));
                }
            }
        }

        Double dist = distArray[idx];
        String path = "";

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


        String _isArrived = dist < 15 ? "true" : "false";
        NodePath _NodePath = new NodePath("",_isArrived,dist,path);
        return _NodePath;
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
            double radius = 6371;
            double toRadian = Math.PI / 180;
            double DLa = Math.abs(lat2 - lat1) * toRadian;
            double DLo = Math.abs(lng2 - lng1) * toRadian;
            double sinDLa = Math.sin(DLa/2);
            double sinDLo = Math.sin(DLo/2);
            double squareRoot = Math.sqrt(sinDLa * sinDLa +
                    Math.cos(lat1 * toRadian) * Math.cos(lat2 * toRadian) * sinDLo * sinDLo);

            return 2000 * radius * Math.asin(squareRoot);
        }catch (Exception e){
            return -1;
        }
    }
    public List<Place> updatePlace(){

        _PlaceRepository.deleteAll();
        _PlaceRepository.deleteINCREMENT();

        List<Place> PL = new ArrayList<Place>();

        try {
            InputStream inputStream = new ClassPathResource("_PLACE2.txt").getInputStream();
            File file =File.createTempFile("_PLACE2",".txt");
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
                    System.out.println(tmp);
                    if(tmp.charAt(0)=='"')
                        tmp = tmp.substring(1,tmp.length()-1);
                    csv.add(tmp);
                }

                Place _Place = new Place(csv);
                PL.add(_Place);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        _PlaceRepository.saveAll(PL);
        return _PlaceRepository.findAll();
    }
    public List<Navi> updateNavi() {

        _NaviRepository.deleteAll();
        _NaviRepository.deleteINCREMENT();
        _NodePathRepository.deleteAll();
        _NodePathRepository.deleteINCREMENT();

        int visited[][] = new int[550][550];

        List<String> epsg3857 = new ArrayList<>();
        List<String> epsg4326 = new ArrayList<>();
        List<String> placeCode = new ArrayList<>();

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
                StringTokenizer tmp = new StringTokenizer(_nearNode);
                while(tmp.hasMoreTokens()){
                    String _tmp = tmp.nextToken(",");
                    int next = Integer.parseInt(_tmp);
                    visited[placeCode.size()+1][next] = 1;
                    visited[next][placeCode.size()+1] = 1;
                }

                // 좌표계 변환
                String _epsg3857 = csv.get(2).substring(1,csv.get(2).length()-1);
                String _epsg4326 = epsg3857_to_epsg4326(_epsg3857);
                epsg3857.add(_epsg3857);
                epsg4326.add(_epsg4326);

                // placeCode
                String _placeCode = csv.get(3);
                if(_placeCode.charAt(0)=='"')
                    _placeCode = _placeCode.substring(1,_placeCode.length()-1);
                placeCode.add(_placeCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Navi> naviList = new ArrayList<>();
        int len = placeCode.size();
        for(int i=1; i<=len; i++){
            String _nearNode = "";
            for(int j=1; j<=len; j++){
                if(visited[i][j]==1)
                    _nearNode+=j+",";
            }
            if(_nearNode.charAt(_nearNode.length()-1)==',')
                _nearNode=_nearNode.substring(0,_nearNode.length()-1);
            Navi _Navi = new Navi(_nearNode,epsg3857.get(i-1),epsg4326.get(i-1),placeCode.get(i-1));
            naviList.add(_Navi);
        }

        _NaviRepository.saveAll(naviList);
        return _NaviRepository.findAll();
    }

    public List<Navi> updateNavi2() {

        _NaviRepository.deleteAll();
        _NaviRepository.deleteINCREMENT();
        _NodePathRepository.deleteAll();
        _NodePathRepository.deleteINCREMENT();

        int visited[][] = new int[1000][1000];

        List<String> epsg3857 = new ArrayList<>();
        List<String> epsg4326 = new ArrayList<>();
        List<String> placeCode = new ArrayList<>();

        try {
            InputStream inputStream = new ClassPathResource("_ALLNODE2.txt").getInputStream();
            File file =File.createTempFile("_ALLNODE2",".txt");
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

                int idx = placeCode.size() + 1;

                List<String> csv = new ArrayList<>();
                StringTokenizer s = new StringTokenizer(line);

                // NULL, NULL, NULL, nearNode, NULL, placeCode, Lo, La, NULL
                for (int i = 0; i <= 8; i++) {
                    if(i==3 || i==5 || i==6 || i==7) {
                        String tmp = s.nextToken("\t");
                        csv.add(tmp);
                    }
                }

                // nearNode
                String _nearNode = csv.get(0);
                if(_nearNode.charAt(0)=='"')
                    _nearNode=_nearNode.substring(1,_nearNode.length()-1);
                StringTokenizer st = new StringTokenizer(_nearNode);
                while(st.hasMoreTokens()){
                    String token = st.nextToken(",");
                    int nearNodeNum = Integer.parseInt(token);
                    visited[idx][nearNodeNum]=1;
                    visited[nearNodeNum][idx]=1;
                }

                // placeCode
                String _placeCode = csv.get(1);
                if(_placeCode.charAt(0)=='"')
                    _placeCode = _placeCode.substring(1,_placeCode.length()-1);
                placeCode.add(_placeCode);

                // La Lo
                String La = csv.get(2);
                String Lo = csv.get(3);
                String _epsg4326 = La + " " + Lo;
                epsg4326.add(_epsg4326);
                epsg3857.add("-");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Navi> retNaviList = new ArrayList<>();

        for(int i=1; i<=placeCode.size(); i++){
            // nearNode
            String _nearNode = "";
            for(int j=1; j<=placeCode.size(); j++) {
                if(visited[i][j]==1) _nearNode += Integer.toString(j) + ",";
            } _nearNode = _nearNode.substring(0,_nearNode.length()-1);

            Navi _Navi = new Navi(_nearNode,"-",epsg4326.get(i-1),placeCode.get(i-1));
            retNaviList.add(_Navi);
        }

        _NaviRepository.saveAll(retNaviList);
        return _NaviRepository.findAll();
    }

    public Map<String, List<Map<String,String>>> placeSearchList(String searchKeyword,  String myLocation) {
        searchKeyword = searchKeyword.substring(1,searchKeyword.length()-1);
        myLocation = myLocation.substring(1,myLocation.length()-1);
        searchKeyword = searchKeyword.replaceAll(" ","");
        List<Place> placeList = _PlaceRepository.findAll();
        List<Navi> naviList = _NaviRepository.findAll();
        int nowNode = 0;
        double minDist = 1e9;
        for(int i=0; i<naviList.size(); i++){
            double _dist = distanceNode(myLocation,naviList.get(i).getEpsg4326());
            if(_dist<minDist){
                nowNode = i+1;
                minDist = _dist;
            }
        }
        List<Place> requestPlaceList = new ArrayList<>();

        for (Place place : placeList) {
            String _title = place.getTitle();
            _title = _title.replaceAll(" ", "");
            String _sort = place.getSort();
            _sort = _sort.replaceAll(" ", "");

            if (_title.toUpperCase().contains(searchKeyword.toUpperCase()) || _sort.toUpperCase().contains(searchKeyword.toUpperCase())) {
                String _dstPlaceCode = place.getPlaceCode();
                ArrayList<Integer> _dstNodeNumList = new ArrayList<>();

                for (int j = 0; j < naviList.size(); j++) {
                    String _placeCode = naviList.get(j).getPlaceCode();
                    StringTokenizer st = new StringTokenizer(_placeCode);
                    while (st.hasMoreTokens()) {
                        String s = st.nextToken(",");
                        if (s.equals(_dstPlaceCode)) {
                            _dstNodeNumList.add(j + 1);
                            break;
                        }
                    }
                }

                double dist = dijkstraPartial(nowNode, _dstNodeNumList).getDist();
                double distFromStart = distanceNode(myLocation, _NaviRepository.getById(nowNode).getEpsg4326());
                Place _Place = place;
                _Place.setDistance(Double.toString(dist + distFromStart));
                requestPlaceList.add(_Place);
            }
        }

        requestPlaceList.sort(Place::compareTo);

        Map<String, List<Map<String, String>>> retMap = new HashMap<>();
        List<Map<String,String>> retList = new ArrayList<>();
        for(int i=0; i<requestPlaceList.size(); i++){
            Map<String, String> retMapPartial = new HashMap<>();
            Place retPlacePartial = requestPlaceList.get(i);
            retMapPartial.put("placeCode",retPlacePartial.getPlaceCode());
            retMapPartial.put("title",retPlacePartial.getTitle());
            retMapPartial.put("sort",retPlacePartial.getSort());

            String _Distance = retPlacePartial.getDistance();
            StringTokenizer st = new StringTokenizer(_Distance);
            String s = st.nextToken(".");
            retMapPartial.put("distance",s);

            retMapPartial.put("location",retPlacePartial.getEpsg4326());
            retMapPartial.put("time",retPlacePartial.getTime());
            retMapPartial.put("callNum",retPlacePartial.getCallNum());
            retList.add(retMapPartial);
        }

        retMap.put("response",retList);
        return retMap;
    }

    public Map<String, List<NodePath>> getRootLive(String startPlaceCode, String endPlaceCode, String startLocation, String endLocation){

        Map<String, List<NodePath>> retGetPath = new HashMap<>();
        List<NodePath> _retList = new ArrayList<>();
        startPlaceCode = startPlaceCode.substring(1,startPlaceCode.length()-1);
        endPlaceCode = endPlaceCode.substring(1,endPlaceCode.length()-1);
        startLocation = startLocation.substring(1,startLocation.length()-1);
        endLocation = endLocation.substring(1,endLocation.length()-1);
        String Query = startPlaceCode+","+startLocation+","+endPlaceCode+","+endLocation;

        boolean startWithLocation = startPlaceCode.equals("LOCATION");
        boolean endWithLocation = endPlaceCode.equals("LOCATION");

        int len = (int)_NaviRepository.count();
        List<Navi> tmp = new ArrayList<>();
        if(epsg4326List.isEmpty()){
            if(tmp.isEmpty())
                tmp.addAll(_NaviRepository.findAll());
            for(int i=0; i<tmp.size(); i++)
                epsg4326List.add(tmp.get(i).getEpsg4326());
        }
        if(placeCodeList.isEmpty()){
            if(tmp.isEmpty())
                tmp.addAll(_NaviRepository.findAll());
            for(int i=0; i<tmp.size(); i++)
                placeCodeList.add(tmp.get(i).getPlaceCode());
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
            ArrayList<Integer> dst = new ArrayList<>();
            dst.add(minNodeFromEnd);
            NodePath _NodePath = dijkstraPartial(minNodeFromStart,dst);

            Double _Dist = _NodePath.getDist();
            _Dist += distanceNode(startLocation, epsg4326List.get(minNodeFromStart-1));
            _Dist += distanceNode(endLocation, epsg4326List.get(minNodeFromEnd-1));

            String _Path = startLocation+',';
            _Path += _NodePath.getRoute();
            _Path += ','+endLocation;

            String _isArrived = _Dist < 15 ? "true" : "false";
            NodePath _retNodePath = new NodePath(Query,_isArrived,_Dist,_Path);
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

            NodePath _NodePath = dijkstraPartial(minNodeFromStart,targetNodeList);
            Double _Dist = _NodePath.getDist();
            _Dist += distanceNode(startLocation, epsg4326List.get(minNodeFromStart-1));

            String _Path = startLocation+',';
            _Path += _NodePath.getRoute();

            String _isArrived = _Dist < 15 ? "true" : "false";
            NodePath _retNodePath = new NodePath(Query,_isArrived,_Dist,_Path);
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

            NodePath _NodePath = dijkstraPartial(minNodeFromEnd,targetNodeList);
            Double _Dist = _NodePath.getDist();
            _Dist += distanceNode(endLocation, epsg4326List.get(minNodeFromEnd-1));

            String _Path = _NodePath.getRoute();
            _Path += ","+startLocation;
            Stack<String> _Stack = new Stack<>();
            StringTokenizer st = new StringTokenizer(_Path);
            while(st.hasMoreTokens()){
                String locationTmp = st.nextToken(",");
                _Stack.push(locationTmp);
            }
            _Path = "";
            while(!_Stack.isEmpty()){
                String _126 = _Stack.peek();
                _Stack.pop();
                String _37 = _Stack.peek();
                _Stack.pop();
                _Path+=_37+","+_126+",";
            }
            _Path = _Path.substring(0,_Path.length()-1);
            String _isArrived = _Dist < 15 ? "true" : "false";
            NodePath _retNodePath = new NodePath(Query,_isArrived,_Dist,_Path);
            _retList.add(_retNodePath);
            retGetPath.put("response",_retList);
        }

        // placeCode to placeCode
        if(!startWithLocation && !endWithLocation){
            NodePath _alreadyDone = _NodePathRepository.findByQuery(Query);
            if(_alreadyDone!=null){
                _retList.add(_alreadyDone);
                retGetPath.put("response",_retList);
                return retGetPath;
            }

            ArrayList<Integer> startNodeList = new ArrayList<>();
            ArrayList<Integer> endNodeList = new ArrayList<>();

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

            Double minDist = 1e9;
            NodePath _NodePath = null;
            for(int i=0; i<startNodeList.size(); i++){
                NodePath targetNodePath = dijkstraPartial(startNodeList.get(i),endNodeList);
                Double targetDist = targetNodePath.getDist();
                if(targetDist < minDist){
                    minDist = targetDist;
                    _NodePath = targetNodePath;
                }
            }

            String _isArrived = _NodePath.getDist() < 15 ? "true" : "false";
            NodePath _retNodePath = new NodePath(Query,_isArrived,_NodePath.getDist(),_NodePath.getRoute());
            _NodePathRepository.save(_retNodePath);
            _retList.add(_retNodePath);
            retGetPath.put("response",_retList);
        }
        return retGetPath;
    }

    public Map<String, List<NodePath>> AstarGetRootLive(String startPlaceCode, String endPlaceCode, String startLocation, String endLocation){
        Map<String, List<NodePath>> retGetPath = new HashMap<>();
        List<NodePath> _retList = new ArrayList<>();
        startPlaceCode = startPlaceCode.substring(1,startPlaceCode.length()-1);
        endPlaceCode = endPlaceCode.substring(1,endPlaceCode.length()-1);
        startLocation = startLocation.substring(1,startLocation.length()-1);
        endLocation = endLocation.substring(1,endLocation.length()-1);
        String Query = startPlaceCode+","+startLocation+","+endPlaceCode+","+endLocation;

        boolean startWithLocation = startPlaceCode.equals("LOCATION");
        boolean endWithLocation = endPlaceCode.equals("LOCATION");

        int len = (int)_NaviRepository.count();
        List<Navi> tmp = new ArrayList<>();
        if(epsg4326List.isEmpty()){
            if(tmp.isEmpty())
                tmp.addAll(_NaviRepository.findAll());
            for(int i=0; i<tmp.size(); i++)
                epsg4326List.add(tmp.get(i).getEpsg4326());
        }
        if(placeCodeList.isEmpty()){
            if(tmp.isEmpty())
                tmp.addAll(_NaviRepository.findAll());
            for(int i=0; i<tmp.size(); i++)
                placeCodeList.add(tmp.get(i).getPlaceCode());
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
            ArrayList<Integer> dst = new ArrayList<>();
            dst.add(minNodeFromEnd);
            NodePath _NodePath = Astar(minNodeFromStart,dst);

            Double _Dist = _NodePath.getDist();
            _Dist += distanceNode(startLocation, epsg4326List.get(minNodeFromStart-1));
            _Dist += distanceNode(endLocation, epsg4326List.get(minNodeFromEnd-1));

            String _Path = startLocation+',';
            _Path += _NodePath.getRoute();
            _Path += ','+endLocation;

            String _isArrived = _Dist < 15 ? "true" : "false";
            NodePath _retNodePath = new NodePath(Query,_isArrived,_Dist,_Path);
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

            NodePath _NodePath = Astar(minNodeFromStart,targetNodeList);
            Double _Dist = _NodePath.getDist();
            _Dist += distanceNode(startLocation, epsg4326List.get(minNodeFromStart-1));

            String _Path = startLocation+',';
            _Path += _NodePath.getRoute();

            String _isArrived = _Dist < 15 ? "true" : "false";
            NodePath _retNodePath = new NodePath(Query,_isArrived,_Dist,_Path);
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

            NodePath _NodePath = Astar(minNodeFromEnd,targetNodeList);
            Double _Dist = _NodePath.getDist();
            _Dist += distanceNode(endLocation, epsg4326List.get(minNodeFromEnd-1));

            String _Path = _NodePath.getRoute();
            _Path += ","+startLocation;
            Stack<String> _Stack = new Stack<>();
            StringTokenizer st = new StringTokenizer(_Path);
            while(st.hasMoreTokens()){
                String locationTmp = st.nextToken(",");
                _Stack.push(locationTmp);
            }
            _Path = "";
            while(!_Stack.isEmpty()){
                String _126 = _Stack.peek();
                _Stack.pop();
                String _37 = _Stack.peek();
                _Stack.pop();
                _Path+=_37+","+_126+",";
            }
            _Path = _Path.substring(0,_Path.length()-1);
            String _isArrived = _Dist < 15 ? "true" : "false";
            NodePath _retNodePath = new NodePath(Query,_isArrived,_Dist,_Path);
            _retList.add(_retNodePath);
            retGetPath.put("response",_retList);
        }

        // placeCode to placeCode
        if(!startWithLocation && !endWithLocation){
            NodePath _alreadyDone = _NodePathRepository.findByQuery(Query);
            if(_alreadyDone!=null){
                _retList.add(_alreadyDone);
                retGetPath.put("response",_retList);
                return retGetPath;
            }

            ArrayList<Integer> startNodeList = new ArrayList<>();
            ArrayList<Integer> endNodeList = new ArrayList<>();

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

            Double minDist = 1e9;
            NodePath _NodePath = null;
            for(int i=0; i<startNodeList.size(); i++){
                NodePath targetNodePath = Astar(startNodeList.get(i),endNodeList);
                Double targetDist = targetNodePath.getDist();
                if(targetDist < minDist){
                    minDist = targetDist;
                    _NodePath = targetNodePath;
                }
            }

            String _isArrived = _NodePath.getDist() < 15 ? "true" : "false";
            NodePath _retNodePath = new NodePath(Query,_isArrived,_NodePath.getDist(),_NodePath.getRoute());
            _NodePathRepository.save(_retNodePath);
            _retList.add(_retNodePath);
            retGetPath.put("response",_retList);
        }
        return retGetPath;
    }

    public Map<String, String> getNextPlace(String email) {
        List<UserLecture> userLectureList = _UserLectureRepository.findAllByEmail(email);

        Date today = new Date();
        SimpleDateFormat dayOfWeek = new SimpleDateFormat("E");
        SimpleDateFormat hourOfToday = new SimpleDateFormat("HH");
        SimpleDateFormat minOfToday = new SimpleDateFormat("mm");

        char charDayOfWeek = dayOfWeek.format(today).charAt(0);
        int intHourOfToday = Integer.parseInt(hourOfToday.format(today));
        int intMinOfToday = Integer.parseInt(minOfToday.format(today));

        int nowTime = 0;
        switch(charDayOfWeek){
            case '화': nowTime+=48; break;
            case '수': nowTime+=96; break;
            case '목': nowTime+=144; break;
            case '금': nowTime+=192; break;
            case '토': nowTime+=240; break;
            default: break;
        }

        nowTime+=intHourOfToday*2;
        nowTime+=intMinOfToday/30;

        String retLectureId = "";
        int minTimeGap = 19999;
        int token = 0;

        for(int i=0; i<userLectureList.size(); i++){
            String lectureId = _AllLectureRepository.getById(userLectureList.get(i).getLectureIdx()).getNumber();
            String lectureTime = _AllLectureRepository.findByLectureId(lectureId).getClasstime();
            if(lectureTime.equals("-")) continue;
            StringTokenizer st = new StringTokenizer(lectureTime);

            int idx = -1;
            while(st.hasMoreTokens()){
                idx++;
                String s = st.nextToken(",");
                String[] timeRange = s.split("-");

                int start = Integer.parseInt(timeRange[0]);

                if(nowTime <= start && start-nowTime < minTimeGap && start/48 == nowTime/48){
                    minTimeGap = start-nowTime;
                    retLectureId = lectureId;
                    token = idx;
                }
            }
        }

        Map<String, String> retNextPlace = new HashMap<>();

        if(retLectureId.equals("")){
            retNextPlace.put("success","false");
            retNextPlace.put("nextPlaceCode","NONE");
            retNextPlace.put("nextPlaceLocationString","0.0");
            retNextPlace.put("nextPlaceTitle","NONE");
        }
        else{
            String classRoom = _AllLectureRepository.findByLectureId(retLectureId).getClassroom();
            String[] tokenedClassRoom = classRoom.split(",");
            String nextPlaceCode = "";
            try{
                nextPlaceCode = tokenedClassRoom[token].substring(0,tokenedClassRoom[token].length()-3);
            }catch (Exception e){
                nextPlaceCode = "ZZ";
            }
            if(nextPlaceCode.equals("SXB"))
                nextPlaceCode = "SX";
            retNextPlace.put("success","true");
            retNextPlace.put("nextPlaceCode",nextPlaceCode);

            Place nextPlace = _PlaceRepository.findByPlaceCode(nextPlaceCode);
            if(nextPlace==null){
                retNextPlace.put("nextPlaceLocationString","NONE");
                retNextPlace.put("nextPlaceTitle","NONE");

                return retNextPlace;
            }

            String nextPlaceLocation = nextPlace.getEpsg4326();
            String nextPlaceTitle = nextPlace.getTitle();

            retNextPlace.put("nextPlaceLocationString",nextPlaceLocation);
            retNextPlace.put("nextPlaceTitle",nextPlaceTitle);
        }
        return retNextPlace;
    }

    public Map<String, String> getAnalysisResult(String email) {
        Map<String, List<Map<String, String>>> userOverviewRoot = getOverviewRoot(email);
        double totalDistance = 0.0;
        for(int i=0; i<userOverviewRoot.get("response").size(); i++){
            String distance = "";
            try{
                distance = userOverviewRoot.get("response").get(i).get("distance");
                totalDistance += Double.parseDouble(distance);
            }catch (Exception e){
                continue;
            }
        }
        int tightnessPercentage = 0;
        int distancePercentage = (int)totalDistance/50;
        int[] timeArr = new int[336];
        List<UserLecture> userLectureList = _UserLectureRepository.findAllByEmail(email);
        Map<String, String> retMap = new HashMap<>();
        if(userLectureList.isEmpty()){
            retMap.put("success","false");
            retMap.put("distancePercentage","0");
            retMap.put("tightnessPercentage","0");
            retMap.put("totalDistance","0");
            return retMap;
        }
        List<Lecture> lectureList = new ArrayList<>();
        for(int i=0; i<userLectureList.size(); i++){
            int lectureIdx = userLectureList.get(i).getLectureIdx();
            Lecture _Lecture = _AllLectureRepository.getById(lectureIdx);
            String classTime = _Lecture.getClasstime();
            if(classTime.equals("-") || _Lecture.getClassroom()==null) continue;

            // 가상건물 제외
            String placeCode = _Lecture.getClassroom().substring(0,3);
            if(!placeCode.equals("SY1") && !placeCode.equals("SY2") && !placeCode.equals("SY3"))
                placeCode = placeCode.substring(0,2);
            if(placeCode.equals("ZZ")) continue;

            StringTokenizer st = new StringTokenizer(classTime);
            while(st.hasMoreTokens()) {
                String start2end = st.nextToken(",");
                String[] timeTmp = start2end.split("-");
                int start = Integer.parseInt(timeTmp[0]);
                int end = Integer.parseInt(timeTmp[1]);
                for (int j = start; j < end; j++)
                    timeArr[j] = 1;
            }
        }
        for(int i=1; i<335; i++){
            if(timeArr[i-1]==1 && timeArr[i+1]==1 && timeArr[i]==0)
                tightnessPercentage+=15;
        }
        retMap.put("success","true");
        retMap.put("distancePercentage",Integer.toString(distancePercentage));
        retMap.put("tightnessPercentage",Integer.toString(tightnessPercentage));
        retMap.put("totalDistance",Double.toString(totalDistance));
        return retMap;
    }

    public Map<String, List<Map<String, String>>> getOverviewRoot(String email) {
        Map<String, List<Map<String,String>>> retOverview = new HashMap<>();
        List<Map<String,String>> retList = new ArrayList<>();
        // 유저 수업정보 저장
        List<UserLecture> userLectureList = _UserLectureRepository.findAllByEmail(email);
        String[] placeCodeArrByTime = new String[336];
        String[] lectureNameArrByTime = new String[336];
        String[] lectureTimeArrByTime = new String[336];

        int[] dayCheck = new int[7];
        for(int i=0; i<userLectureList.size(); i++){
            int lectureIdx = userLectureList.get(i).getLectureIdx();
            Lecture _Lecture = _AllLectureRepository.getById(lectureIdx);

            int idx = -1;

            String classRoom = _Lecture.getClassroom();
            String classTime = _Lecture.getClasstime();

            if(classTime.equals("-") || classRoom==null || classRoom.equals("")) continue;

            StringTokenizer stRoom = new StringTokenizer(classRoom);
            StringTokenizer stTime = new StringTokenizer(classTime);
            while(stRoom.hasMoreTokens() && stTime.hasMoreTokens()){
                idx++;

                // SM
                String sRoom = stRoom.nextToken(",");
                if(sRoom.contains("SY1")) sRoom="SY1";
                else if(sRoom.contains("SY2")) sRoom="SY2";
                else if(sRoom.contains("SY3")) sRoom="SY3";
                else sRoom = sRoom.substring(0,2);
                if(sRoom.equals("NC") || sRoom.equals("ZZ"))
                    continue;

                // 43
                StringTokenizer st = new StringTokenizer(stTime.nextToken(","));
                int sTime = Integer.parseInt(st.nextToken("-"));

                // 요일푸시
                dayCheck[sTime/48] = 1;
                // placeCode 푸시
                placeCodeArrByTime[sTime] = sRoom;
                // lectureName 푸시
                lectureNameArrByTime[sTime] = _Lecture.getLecturename();

                // lectureTime 푸시
                String dayOfWeek = "";
                switch(sTime/48){
                    case 0: dayOfWeek = "월요일"; break;
                    case 1: dayOfWeek = "화요일"; break;
                    case 2: dayOfWeek = "수요일"; break;
                    case 3: dayOfWeek = "목요일"; break;
                    case 4: dayOfWeek = "금요일"; break;
                    case 5: dayOfWeek = "토요일"; break;
                    case 6: dayOfWeek = "일요일"; break;
                    default: break;
                }
                int ssTime = sTime;
                ssTime%=48;
                String timeString = "";
                String amORpm = "";
                amORpm = ssTime >= 26 ? "PM" : "AM";
                if(amORpm.equals("PM"))
                    ssTime -= 24;
                int hourTmp = ssTime;
                if(hourTmp <= 19) timeString+="0";
                timeString += Integer.toString(ssTime/2);
                String minString = "";
                minString = ssTime%2==0 ? ":00" : ":30";
                String lectureTime = dayOfWeek + " " + timeString+minString + " " + amORpm;
                lectureTimeArrByTime[sTime] = lectureTime;
            }
        }

        // 유저 수업정보 시간순 정렬(분리해서)

        ArrayList<Integer> correctTime = new ArrayList<>();
        for(int i=0; i<336; i++){
            int dayOfWeek = i/48;
            int dayOfWeekStart = i%48;
            if(dayOfWeekStart==0){
                // 해당요일 수업없으면 스킵
                if(dayCheck[dayOfWeek]==0){
                    i+=47;
                    continue;
                }
                correctTime.add(i);
            }
            else{
                if(placeCodeArrByTime[i]!=null)
                    correctTime.add(i);
            }
        }

        for(int i=0; i<correctTime.size()-1; i++){
            int nowTime = correctTime.get(i);
            int nextTime = correctTime.get(i+1);

            if(nextTime/48 != nowTime/48) continue;
            Map<String, String> retMap = new HashMap<>();
            if(nowTime%48==0){

                String nextPlaceCode = placeCodeArrByTime[nextTime];

                List<Navi> naviOfPlaceCode = _NaviRepository.findAll();
                ArrayList<Integer> dstNodeNumList = new ArrayList<>();
                for(int j=0; j<naviOfPlaceCode.size(); j++){
                    Navi _Navi = naviOfPlaceCode.get(j);
                    String naviPlaceCode = _Navi.getPlaceCode();
                    if(naviPlaceCode.equals("-")) continue;
                    StringTokenizer st = new StringTokenizer(naviPlaceCode);
                    while(st.hasMoreTokens()){
                        String tmp = st.nextToken(",");
                        if(tmp.equals(nextPlaceCode)){
                            dstNodeNumList.add(_Navi.getId());
                            break;
                        }
                    }
                }

                Double minDist = 1e9;
                int minNodeNum = -1;

                NodePath _NodePath = dijkstraPartial(252,dstNodeNumList);
                if(_NodePath.getDist() < minDist){
                    minDist = _NodePath.getDist();
                    minNodeNum = 252;
                }
                _NodePath = dijkstraPartial(455,dstNodeNumList);
                if(_NodePath.getDist() < minDist){
                    minDist = _NodePath.getDist();
                    minNodeNum = 455;
                }
                _NodePath = dijkstraPartial(17,dstNodeNumList);
                if(_NodePath.getDist() < minDist){
                    minDist = _NodePath.getDist();
                    minNodeNum = 17;
                }

                _NodePath = dijkstraPartial(minNodeNum, dstNodeNumList);
                String startLectureName="";
                String endLectureName = lectureNameArrByTime[nextTime];

                if(minNodeNum==252)
                    startLectureName = "공과대학(버스정류장)";
                if(minNodeNum==455)
                    startLectureName = "자연과학대학(버스정류장)";
                if(minNodeNum==17)
                    startLectureName = "정문(버스정류장)";

                String endLectureTime = lectureTimeArrByTime[nextTime];
                int totalTime = _NodePath.getTime();
                double distance = _NodePath.getDist();
                String directionString = _NodePath.getRoute();

                retMap.put("startLectureName",startLectureName);
                retMap.put("endLectureName",endLectureName);
                retMap.put("endLectureTime",endLectureTime);
                retMap.put("totalTime",Integer.toString(totalTime));
                retMap.put("distance",Double.toString(distance));
                retMap.put("directionString",directionString);

                retList.add(retMap);
            }
            else{
                String nowPlaceCode = placeCodeArrByTime[nowTime];
                String nextPlaceCode = placeCodeArrByTime[nextTime];

                List<Navi> nowNaviList = new ArrayList<>();
                List<Navi> nextNaviList = new ArrayList<>();
                List<Navi> naviList = _NaviRepository.findAll();
                for(int idx=0; idx<naviList.size(); idx++){
                    Navi now = naviList.get(idx);
                    String _placeCode = now.getPlaceCode();
                    if(_placeCode.equals("-")) continue;
                    StringTokenizer st = new StringTokenizer(_placeCode);
                    while(st.hasMoreTokens()){
                        String tmp = st.nextToken(",");
                        if(tmp.equals(nowPlaceCode))
                            nowNaviList.add(now);
                        if(tmp.equals(nextPlaceCode))
                            nextNaviList.add(now);
                    }
                }

                ArrayList<Integer> srcNodeNumList = new ArrayList<>();
                ArrayList<Integer> dstNodeNumList = new ArrayList<>();
                for(int j=0; j<nowNaviList.size(); j++)
                    srcNodeNumList.add(nowNaviList.get(j).getId());
                for(int j=0; j<nextNaviList.size(); j++)
                    dstNodeNumList.add(nextNaviList.get(j).getId());

                Double minDist = 1e9;
                int minNodeNum = -1;

                for(int j=0; j<srcNodeNumList.size(); j++){
                    NodePath _NodePath = dijkstraPartial(srcNodeNumList.get(j),dstNodeNumList);
                    double dist = _NodePath.getDist();
                    if(dist < minDist){
                        minDist = dist;
                        minNodeNum = srcNodeNumList.get(j);
                    }
                }

                NodePath minNodePath = dijkstraPartial(minNodeNum, dstNodeNumList);

                String startLectureName=lectureNameArrByTime[nowTime];
                String endLectureName = lectureNameArrByTime[nextTime];
                String endLectureTime = lectureTimeArrByTime[nextTime];
                int totalTime = minNodePath.getTime();
                double distance = minNodePath.getDist();
                String directionString = minNodePath.getRoute();

                retMap.put("startLectureName",startLectureName);
                retMap.put("endLectureName",endLectureName);
                retMap.put("endLectureTime",endLectureTime);
                retMap.put("totalTime",Integer.toString(totalTime));
                retMap.put("distance",Double.toString(distance));
                retMap.put("directionString",directionString);

                retList.add(retMap);
            }
        }
        retOverview.put("response",retList);
        return retOverview;
    }
}
