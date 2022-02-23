package com.maru.inunavi.service;

import com.maru.inunavi.entity.*;
import com.maru.inunavi.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.graalvm.compiler.core.common.util.ReversedList;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import javax.persistence.criteria.CriteriaBuilder;
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

    // 임시로 캐시대신 사용
    private ArrayList<String> epsg4326List = new ArrayList<>();
    private ArrayList<String> placeCodeList = new ArrayList<>();

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

        nowTime+=intHourOfToday*2-1;
        nowTime+=intMinOfToday/30;

        String retLectureId = "";
        int minTimeGap = 19999;
        int token = 0;
        for(int i=0; i<userLectureList.size(); i++){
            String lectureId = userLectureList.get(i).getLectureId();
            String lectureTime = _AllLectureRepository.findByLectureID(lectureId).getClasstime();
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
            String classRoom = _AllLectureRepository.findByLectureID(retLectureId).getClassroom();
            String[] tokenedClassRoom = classRoom.split(",");
            String nextPlaceCode = tokenedClassRoom[token].substring(0,tokenedClassRoom[token].length()-3);

            Place nextPlace = _PlaceRepository.findByPlaceCode(nextPlaceCode);

            String nextPlaceLocation = nextPlace.getEpsg4326();
            String nextPlaceTitle = nextPlace.getTitle();
            retNextPlace.put("success","true");
            retNextPlace.put("nextPlaceCode",nextPlaceCode);
            retNextPlace.put("nextPlaceLocationString",nextPlaceLocation);
            retNextPlace.put("nextPlaceTitle",nextPlaceTitle);
        }
        return retNextPlace;
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

        for(int i=0; i<placeList.size(); i++){
            String _title = placeList.get(i).getTitle();
            _title = _title.replaceAll(" ","");
            String _sort = placeList.get(i).getSort();
            _sort = _sort.replaceAll(" ","");

            if(_title.toUpperCase().contains(searchKeyword.toUpperCase()) || _sort.toUpperCase().contains(searchKeyword.toUpperCase())){
                String _dstPlaceCode = placeList.get(i).getPlaceCode();
                ArrayList<Integer> _dstNodeNumList = new ArrayList<>();

                for(int j=0; j<naviList.size(); j++){
                    String _placeCode = naviList.get(j).getPlaceCode();
                    StringTokenizer st = new StringTokenizer(_placeCode);
                    while(st.hasMoreTokens()){
                        String s = st.nextToken(",");
                        if(s.equals(_dstPlaceCode)){
                            _dstNodeNumList.add(j+1);
                            break;
                        }
                    }
                }

                double dist = dijkstraPatial(nowNode,_dstNodeNumList).getDist();
                double distFromStart = distanceNode(myLocation,_NaviRepository.getById(nowNode).getEpsg4326());
                Place _Place = placeList.get(i);
                _Place.setDistance(Double.toString(dist+distFromStart));
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
    private NodePath dijkstraPatial(int src, ArrayList<Integer> dst){
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
        pq.add(new pair(0,src));
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
                    pq.add(new pair(dist+_dist,next));
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
        String _isArrived = dist<5 ? "true" : "false";
        NodePath _NodePath = new NodePath("",_isArrived,dist,path);
        return _NodePath;
    }

    // 폐기
    private boolean dijkstraOverall(ArrayList<ArrayList<Integer>> AL){
        try{
            System.out.println("start dijkstraOverall(~)");
            _NodePathRepository.deleteAll();
            _NodePathRepository.deleteINCREMENT();
            int len = (int)_NaviRepository.count();
            ArrayList<NodePath> _NAL = new ArrayList<>();
            for(int i=1; i<=len; i++) {
                for(int j=1; j<=len; j++){
                    if(i==j) continue;
                    //_NAL.add(dijkstraPatial(i,j,AL,len));
                    //_NodePathRepository.save(dijkstraPatial(i,j,AL,len));
                }
                // log
                if(i==1 || i==len || i%50==0)
                    System.out.println("Dijkstra from "+i+" is Done.");
            }
            //log
            System.out.println("Dijkstra all is Done and start SaveAll");
            _NodePathRepository.saveAll(_NAL);
            //log
            System.out.println("_NodePathRepository.saveAll is Done");
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
            NodePath _NodePath = dijkstraPatial(minNodeFromStart,dst);

            Double _Dist = _NodePath.getDist();
            _Dist += distanceNode(startLocation, epsg4326List.get(minNodeFromStart-1));
            _Dist += distanceNode(endLocation, epsg4326List.get(minNodeFromEnd-1));

            String _Path = startLocation+',';
            _Path += _NodePath.getRoute();
            _Path += ','+endLocation;

            String _isArrived = _Dist < 5 ? "true" : "false";
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

            NodePath _NodePath = dijkstraPatial(minNodeFromStart,targetNodeList);
            Double _Dist = _NodePath.getDist();
            _Dist += distanceNode(startLocation, epsg4326List.get(minNodeFromStart-1));

            String _Path = startLocation+',';
            _Path += _NodePath.getRoute();

            String _isArrived = _Dist < 5 ? "true" : "false";
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

            NodePath _NodePath = dijkstraPatial(minNodeFromEnd,targetNodeList);
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
            String _isArrived = _Dist < 5 ? "true" : "false";
            NodePath _retNodePath = new NodePath(Query,_isArrived,_Dist,_Path);
            _retList.add(_NodePath);
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
                NodePath targetNodePath = dijkstraPatial(startNodeList.get(i),endNodeList);
                Double targetDist = targetNodePath.getDist();
                if(targetDist < minDist){
                    minDist = targetDist;
                    _NodePath = targetNodePath;
                }
            }

            String _isArrived = _NodePath.getDist() < 5 ? "true" : "false";
            NodePath _retNodePath = new NodePath(Query,_isArrived,_NodePath.getDist(),_NodePath.getRoute());
            _NodePathRepository.save(_retNodePath);
            _retList.add(_retNodePath);
            retGetPath.put("response",_retList);
        }
        return retGetPath;
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
            String lectureID = userLectureList.get(i).getLectureId();
            Lecture _Lecture = _AllLectureRepository.findByLectureID(lectureID);

            int idx = -1;

            String classRoom = _Lecture.getClassroom();
            String classTime = _Lecture.getClasstime();

            StringTokenizer stRoom = new StringTokenizer(classRoom);
            StringTokenizer stTime = new StringTokenizer(classTime);
            while(stRoom.hasMoreTokens() && stTime.hasMoreTokens()){
                idx++;

                // SM
                String sRoom = stRoom.nextToken(",");
                sRoom = sRoom.substring(0,sRoom.length()-3);

                // 43
                int sTime = Integer.parseInt(stTime.nextToken(",").split("-")[0]);

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
                String timeString = "";
                String amORpm = "";
                amORpm = sTime >= 26 ? "PM" : "AM";
                if(amORpm.equals("PM"))
                    ssTime -= 24;
                int hourTmp = ssTime%48;
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
                correctTime.add(i);
            }
        }

        for(int i=0; i<correctTime.size()-1; i++){
            int nowTime = correctTime.get(i);
            int nextTime = correctTime.get(i+1);
            if(nextTime/48 != nowTime/48) continue;
            Map<String, String> retMap = new HashMap<>();
            if(nowTime%48==0){

                String nextPlaceCode = placeCodeArrByTime[correctTime.get(i+1)];
                List<Navi> naviOfPlaceCode = _NaviRepository.findAllByPlaceCode(nextPlaceCode);
                ArrayList<Integer> dstNodeNumList = new ArrayList<>();
                for(int j=0; j<naviOfPlaceCode.size(); j++)
                    dstNodeNumList.add(naviOfPlaceCode.get(j).getId());

                Double minDist = 1e9;
                NodePath minNodePath;

                NodePath _NodePath = dijkstraPatial(252,dstNodeNumList);
                if(_NodePath.getDist() < minDist){
                    minDist = _NodePath.getDist();
                    minNodePath=_NodePath;
                }
                _NodePath = dijkstraPatial(455,dstNodeNumList);
                if(_NodePath.getDist() < minDist){
                    minDist = _NodePath.getDist();
                    minNodePath=_NodePath;
                }
                _NodePath = dijkstraPatial(17,dstNodeNumList);
                if(_NodePath.getDist() < minDist){
                    minDist = _NodePath.getDist();
                    minNodePath=_NodePath;
                }

                String startLectureName="";
                String endLectureName = lectureNameArrByTime[nextTime];
                if(_NodePath.getId()==252)
                    startLectureName = "인천대학교 공과대학(버스정류장)";
                if(_NodePath.getId()==455)
                    startLectureName = "인천대학교 자연과학대학(버스정류장)";
                if(_NodePath.getId()==17)
                    startLectureName = "인천대학교 정문(버스정류장)";
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
                List<Navi> nowNaviList = _NaviRepository.findAllByPlaceCode(nowPlaceCode);
                List<Navi> nextNaviList = _NaviRepository.findAllByPlaceCode(nextPlaceCode);
                ArrayList<Integer> srcNodeNumList = new ArrayList<>();
                ArrayList<Integer> dstNodeNumList = new ArrayList<>();
                for(int j=0; j<nowNaviList.size(); j++)
                    srcNodeNumList.add(nowNaviList.get(j).getId());
                for(int j=0; j<nextNaviList.size(); j++)
                    dstNodeNumList.add(nextNaviList.get(j).getId());

                Double minDist = 1e9;
                NodePath minNodePath = null;
                for(int j=0; j<srcNodeNumList.size(); j++){
                    NodePath _NodePath = dijkstraPatial(srcNodeNumList.get(j),dstNodeNumList);
                    double dist = _NodePath.getDist();
                    if(dist < minDist){
                        minDist = dist;
                        minNodePath = _NodePath;
                    }
                }

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
