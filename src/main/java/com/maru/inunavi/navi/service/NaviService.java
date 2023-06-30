package com.maru.inunavi.navi.service;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.repository.LectureRepository;
import com.maru.inunavi.navi.domain.dto.NextPlaceDto;
import com.maru.inunavi.navi.domain.dto.PathDto;
import com.maru.inunavi.navi.domain.dto.RouteInfo;
import com.maru.inunavi.navi.domain.entity.Node;
import com.maru.inunavi.navi.domain.entity.Path;
import com.maru.inunavi.navi.domain.entity.Place;
import com.maru.inunavi.navi.repository.NodeRepository;
import com.maru.inunavi.navi.repository.PathRepository;
import com.maru.inunavi.navi.repository.PlaceRepository;
import com.maru.inunavi.user.domain.entity.UserLectureTable;
import com.maru.inunavi.user.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NaviService {

    private final NodeRepository nodeRepository;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final PathRepository pathRepository;
    private final PlaceRepository placeRepository;

    // TODO - check nearnode
    /**
     * Update navi info
     */
    @CacheEvict({"lngList", "latList"})
    @Transactional
    @SneakyThrows
    public void updateNavi() {

        nodeRepository.deleteAll();
        nodeRepository.deleteINCREMENT();

        pathRepository.deleteAll();
        pathRepository.deleteINCREMENT();

        List<String> nearNodeList = new ArrayList<>();
        List<String> lng4326List = new ArrayList<>();
        List<String> lat4326List = new ArrayList<>();
        List<String> placeCodes = new ArrayList<>();

        InputStream inputStream = new ClassPathResource("node.txt").getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
        
        String line = "";
        while ((line = buffer.readLine()) != null) {

            int nodeId = placeCodes.size() + 1;

            StringTokenizer lineToken = new StringTokenizer(line);

            lineToken.nextToken("\t");
            lineToken.nextToken("\t");
            lineToken.nextToken("\t");

            // Set nearNode
            String nearNodes = lineToken.nextToken("\t").replaceAll("\"", "");
            nearNodeList.add(nearNodes);

            StringTokenizer nearNodeToken = new StringTokenizer(nearNodes);
            while(nearNodeToken.hasMoreTokens()){
                String nearNodeIdxToken = nearNodeToken.nextToken(",").replaceAll(" ", "");

                if(nodeId > 1){
                    int nearNodeIdx = Integer.parseInt(nearNodeIdxToken) - 1;

                    String nearNode = nearNodeList.get(nearNodeIdx);
                    nearNode += nodeId + ",";
                }
            }

            lineToken.nextToken("\t");

            // Set placeCode
            String placeCode = lineToken.nextToken("\t").replaceAll("\"", "");
            placeCodes.add(placeCode);

            lineToken.nextToken("\t");

            // Set position
            StringTokenizer lngToken = new StringTokenizer(lineToken.nextToken("\t"));
            String lng4326 = lngToken.nextToken(".") + lngToken.nextToken(",").substring(0, 4);
            lng4326List.add(lng4326);

            StringTokenizer latToken = new StringTokenizer(lineToken.nextToken("\t"));
            String lat4326 = latToken.nextToken(".") + latToken.nextToken(",").substring(0, 4);
            lat4326List.add(lat4326);
        }

        // Save
        IntStream.range(0, placeCodes.size())
                .forEach(
                        idx -> nodeRepository.save(
                                Node.builder()
                                        .lng4326(lng4326List.get(idx))
                                        .lat4326(lat4326List.get(idx))
                                        .nearNode(nearNodeList.get(idx))
                                        .placeCode(placeCodes.get(idx))
                                        .build()
                ));
    }

    /**
     * Update place info
     */
    @Transactional
    @SneakyThrows
    public void updatePlace(){

        placeRepository.deleteAll();
        placeRepository.deleteINCREMENT();

        InputStream inputStream = new ClassPathResource("place.txt").getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));

        String line = "";
        while ((line = buffer.readLine()) != null) {

            StringTokenizer lineToken = new StringTokenizer(line);

            placeRepository.save(
                    Place.builder()
                            .placeCode(lineToken.nextToken("\t").replaceAll("\"", ""))
                            .title(lineToken.nextToken("\t"))
                            .sort(lineToken.nextToken("\t"))
                            .distance(lineToken.nextToken("\t"))
                            .epsg4326(lineToken.nextToken("\t").replaceAll("\"", ""))
                            .time(lineToken.nextToken("\t").replaceAll("\"", ""))
                            .callNum(lineToken.nextToken("\t"))
                            .etc(lineToken.nextToken("\t"))
                            .build()
            );
        }
    }

    /**
     * Provide route in real time
     * @param routeInfo
     * @return {@code List<Path>}
     */
    @Transactional
    public PathDto getRouteLive(RouteInfo routeInfo){
        return pathRepository.findByRouteInfo(routeInfo.getQuery())
                .orElseGet(() -> {
                    Integer srcId = nodeRepository.findSrcNodeIdByRouteInfo(routeInfo);
                    List<Integer> dstIdList = nodeRepository.findDstNodeIdByRouteInfo(routeInfo);

                    return new PathDto(aStarWithManhattan(srcId, dstIdList, routeInfo));
                });
    }

    /**
     * Find approximately the shortest path
     * @param srcId
     * @param dstIdList
     * @param routeInfo
     * @return {@code Path}
     */
    private Path aStarWithManhattan(Integer srcId, List<Integer> dstIdList, RouteInfo routeInfo){

        List<Double> latList = nodeRepository.findAllOfLat();
        List<Double> lngList = nodeRepository.findAllOfLng();

        @Data
        class NodeInfo implements Comparable<NodeInfo>{

            private double dist;
            private int id;
            private int dstId;
            private double heuristics;

            NodeInfo(double dist, int id, int dstId){

                this.dist = dist;
                this.id = id;
                this.dstId = dstId;

                Double srcLat = latList.get(id -1);
                Double srcLng = lngList.get(id -1);

                Double dstLat = latList.get(dstId-1);
                Double dstLng = lngList.get(dstId-1);

                Double manhattanDist = 1.2 * (distanceBetween(srcLat, srcLng, srcLat, dstLng) + distanceBetween(srcLat, dstLng, dstLat, dstLng));

                this.heuristics = dist + manhattanDist;
            }

            @Override
            public int compareTo(NodeInfo p){
                return Double.compare(heuristics, p.heuristics);
            }
        }

        // ---- Set basic info to find approximately the shortest path ---- //
        int len = (int) nodeRepository.count();
        int[] pathTrace = new int[len + 2];
        double[] distArray = new double[len + 2];

        Arrays.fill(distArray, 1e9);
        distArray[srcId] = 0;

        PriorityQueue<NodeInfo> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new NodeInfo(0.0, srcId, dstIdList.get(0)));

        // ---- Find path ---- //
        while (!priorityQueue.isEmpty()) {
            NodeInfo nodeInfo = priorityQueue.poll();
            
            double dist = nodeInfo.getDist();
            int now = nodeInfo.getId();

            if (dstIdList.contains(now)) {
                break;
            }

            if (dist < distArray[now]) {
                continue;
            }

            Double nowLat = latList.get(now - 1);
            Double nowLng = lngList.get(now - 1);

            String nearNodes = nodeRepository.findById(now - 1).get().getNearNode();
            StringTokenizer nearNodesToken = new StringTokenizer(nearNodes);
            while (nearNodesToken.hasMoreTokens()) {
                int next = Integer.parseInt(nearNodesToken.nextToken(","));

                if (next == 0) {
                    continue;
                }

                Double nextLat = latList.get(next - 1);
                Double nextLng = lngList.get(next - 1);

                double deltaDist = distanceBetween(nowLat, nowLng, nextLat, nextLng);
                if (dist + deltaDist < distArray[next]) {
                    pathTrace[next] = now;
                    distArray[next] = dist + deltaDist;
                    priorityQueue.add(new NodeInfo(dist + deltaDist, next, dstIdList.get(0)));
                }
            }
        }

        // ---- Find idx that saving dist ---- //
        int dstIdx = dstIdList.stream()
                .filter(dstId -> distArray[dstId] != 1e9)
                .findFirst()
                .orElse(0);

        // ---- Trace back and set the route ---- //
        String route = Stream
                .iterate(dstIdx, i -> i != 0, i -> pathTrace[i])
                .map(i -> latList.get(i - 1) + "," + lngList.get(i - 1))
                .collect(Collectors.joining(","));

        // ---- Set other info ---- //
        String query = routeInfo.getQuery();
        String isArrived = distArray[dstIdx] < 15 ? "true" : "false";
        Double dist = distArray[dstIdx];

        return pathRepository.save(
                Path.builder()
                        .query(query)
                        .isArrived(isArrived)
                        .dist(dist)
                        .route(route)
                        .build()
        );
    }

    /**
     * Provide distance between src and dst
     * @return distance
     */
    private double distanceBetween(Double lat1, Double lng1, Double lat2, Double lng2){
        double radius = 6371;
        double toRadian = Math.PI / 180;

        double dLat = Math.abs(lat2 - lat1) * toRadian;
        double dLng = Math.abs(lng2 - lng1) * toRadian;

        double sinDLat = Math.sin(dLat / 2);
        double sinDLng = Math.sin(dLng / 2);

        double squareRoot = Math.sqrt(sinDLat * sinDLat + Math.cos(lat1 * toRadian) * Math.cos(lat2 * toRadian) * sinDLng * sinDLng);

        return 2000 * radius * Math.asin(squareRoot);
    }

    @SneakyThrows
    public NextPlaceDto getNextPlace(String email) {
        return userRepository.findNextLecture(email, convertTimeToInt())
                .map(lecture -> {

                    // NotNull
                    List<Integer> startTimeList = lecture.getStartTimeList();

                    int nowTime = convertTimeToInt();
                    int idx = -1;
                    for (int i = 0; i < startTimeList.size() && idx == -1; i++) {
                        if(startTimeList.get(i) / 48 == nowTime / 48){
                            idx = i;
                        }
                    }

                    String classRoomRaw = lecture.getClassRoomRaw().split(",")[idx];
                    String detailClassRoom = classRoomRaw.split("-")[1].split(" ")[0];
                    String nextPlaceCode = lecture.getClassRoom().split(detailClassRoom)[0];

                    return placeRepository.findByPlaceCode(nextPlaceCode)
                            .map(nextPlace ->
                                    NextPlaceDto.builder()
                                            .success("true")
                                            .nextPlaceCode(nextPlace.getPlaceCode())
                                            .nextPlaceTitle(nextPlace.getTitle())
                                            .nextPlaceLocationString(nextPlace.getEpsg4326())
                                            .build()
                            )
                            .orElseGet(() ->
                                    NextPlaceDto.builder()
                                            .success("true")
                                            .nextPlaceCode(nextPlaceCode)
                                            .nextPlaceTitle("NONE")
                                            .nextPlaceLocationString("NONE")
                                            .build()
                            );
                })
                .orElseGet(() ->
                        NextPlaceDto.builder()
                                .success("false")
                                .nextPlaceCode("NONE")
                                .nextPlaceTitle("NONE")
                                .nextPlaceLocationString("0.0")
                                .build()
                );
    }




    public Map<String, List<Map<String,String>>> placeSearchList(String searchKeyword,  String myLocation) {
        searchKeyword = searchKeyword.substring(1,searchKeyword.length()-1);
        myLocation = myLocation.substring(1,myLocation.length()-1);
        searchKeyword = searchKeyword.replaceAll(" ","");
        List<Place> placeList = placeRepository.findAll();
        List<Node> nodeList = nodeRepository.findAll();
        int nowNode = 0;
        double minDist = 1e9;
        for(int i = 0; i< nodeList.size(); i++){
            double _dist = distanceBetween(myLocation, nodeList.get(i).getEpsg4326());
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

                for (int j = 0; j < nodeList.size(); j++) {
                    String _placeCode = nodeList.get(j).getPlaceCode();
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

                double distFromStart = distanceBetween(myLocation, nodeRepository.getById(nowNode).getEpsg4326());
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
        List<UserLectureTable> userLectureTableList = userLectureTableRepository.findAllByEmail(email);
        Map<String, String> retMap = new HashMap<>();
        if(userLectureTableList.isEmpty()){
            retMap.put("success","false");
            retMap.put("distancePercentage","0");
            retMap.put("tightnessPercentage","0");
            retMap.put("totalDistance","0");
            return retMap;
        }
        List<Lecture> lectureList = new ArrayList<>();
        for(int i = 0; i< userLectureTableList.size(); i++){
            int lectureIdx = userLectureTableList.get(i).getLectureIdx();
            Lecture lecture = lectureRepository.getById(lectureIdx);
            String classTime = lecture.getClassTime();
            if(classTime.equals("-") || lecture.getClassRoom()==null) continue;

            // 가상건물 제외
            String placeCode = lecture.getClassRoom().substring(0,3);
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
        List<UserLectureTable> userLectureTableList = userLectureTableRepository.findAllByEmail(email);
        String[] placeCodeArrByTime = new String[336];
        String[] lectureNameArrByTime = new String[336];
        String[] lectureTimeArrByTime = new String[336];

        int[] dayCheck = new int[7];
        for(int i = 0; i< userLectureTableList.size(); i++){
            int lectureIdx = userLectureTableList.get(i).getLectureIdx();
            Lecture lecture = lectureRepository.getById(lectureIdx);

            int idx = -1;

            String classRoom = lecture.getClassRoom();
            String classTime = lecture.getClassTime();

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
                lectureNameArrByTime[sTime] = lecture.getLectureName();

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

                List<Node> nodeOfPlaceCode = nodeRepository.findAll();
                ArrayList<Integer> dstNodeNumList = new ArrayList<>();
                for(int j = 0; j< nodeOfPlaceCode.size(); j++){
                    Node node = nodeOfPlaceCode.get(j);
                    String naviPlaceCode = node.getPlaceCode();
                    if(naviPlaceCode.equals("-")) continue;
                    StringTokenizer st = new StringTokenizer(naviPlaceCode);
                    while(st.hasMoreTokens()){
                        String tmp = st.nextToken(",");
                        if(tmp.equals(nextPlaceCode)){
                            dstNodeNumList.add(Math.toIntExact(node.getId()));
                            break;
                        }
                    }
                }

                Double minDist = 1e9;
                int minNodeNum = -1;

                Path Path = dijkstraPartial(67,dstNodeNumList);
                if(Path.getDist() < minDist){
                    minDist = Path.getDist();
                    minNodeNum = 67;
                }
                Path = dijkstraPartial(60,dstNodeNumList);
                if(Path.getDist() < minDist){
                    minDist = Path.getDist();
                    minNodeNum = 60;
                }
                Path = dijkstraPartial(22,dstNodeNumList);
                if(Path.getDist() < minDist){
                    minDist = Path.getDist();
                    minNodeNum = 22;
                }

                Path = dijkstraPartial(minNodeNum, dstNodeNumList);
                String startLectureName="";
                String endLectureName = lectureNameArrByTime[nextTime];

                if(minNodeNum==67)
                    startLectureName = "공과대학(버스정류장)";
                if(minNodeNum==60)
                    startLectureName = "자연과학대학(버스정류장)";
                if(minNodeNum==22)
                    startLectureName = "정문(버스정류장)";

                String endLectureTime = lectureTimeArrByTime[nextTime];
                int totalTime = Path.getTime();
                double distance = Path.getDist();
                String directionString = Path.getRoute();

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

                List<Node> nowNodeList = new ArrayList<>();
                List<Node> nextNodeList = new ArrayList<>();
                List<Node> nodeList = nodeRepository.findAll();
                for(int idx = 0; idx< nodeList.size(); idx++){
                    Node now = nodeList.get(idx);
                    String _placeCode = now.getPlaceCode();
                    if(_placeCode.equals("-")) continue;
                    StringTokenizer st = new StringTokenizer(_placeCode);
                    while(st.hasMoreTokens()){
                        String tmp = st.nextToken(",");
                        if(tmp.equals(nowPlaceCode))
                            nowNodeList.add(now);
                        if(tmp.equals(nextPlaceCode))
                            nextNodeList.add(now);
                    }
                }

                ArrayList<Integer> srcNodeNumList = new ArrayList<>();
                ArrayList<Integer> dstNodeNumList = new ArrayList<>();
                for(int j = 0; j< nowNodeList.size(); j++)
                    srcNodeNumList.add(Math.toIntExact(nowNodeList.get(j).getId()));
                for(int j = 0; j< nextNodeList.size(); j++)
                    dstNodeNumList.add(Math.toIntExact(nextNodeList.get(j).getId()));

                Double minDist = 1e9;
                int minNodeNum = -1;

                for(int j=0; j<srcNodeNumList.size(); j++){
                    Path Path = dijkstraPartial(srcNodeNumList.get(j),dstNodeNumList);
                    double dist = Path.getDist();
                    if(dist < minDist){
                        minDist = dist;
                        minNodeNum = srcNodeNumList.get(j);
                    }
                }

                Path minPath = dijkstraPartial(minNodeNum, dstNodeNumList);

                String startLectureName=lectureNameArrByTime[nowTime];
                String endLectureName = lectureNameArrByTime[nextTime];
                String endLectureTime = lectureTimeArrByTime[nextTime];
                int totalTime = minPath.getTime();
                double distance = minPath.getDist();
                String directionString = minPath.getRoute();

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

    private int convertTimeToInt() {
        Date today = new Date();

        SimpleDateFormat dayOfWeek = new SimpleDateFormat("E");
        SimpleDateFormat hourOfToday = new SimpleDateFormat("HH");
        SimpleDateFormat minOfToday = new SimpleDateFormat("mm");

        int intHourOfToday = Integer.parseInt(hourOfToday.format(today));
        int intMinOfToday = Integer.parseInt(minOfToday.format(today));

        int nowTime = intHourOfToday * 2 + intMinOfToday / 30;

        switch(dayOfWeek.format(today).charAt(0)){
            case '화': nowTime += 48; break;
            case '수': nowTime += 96; break;
            case '목': nowTime += 144; break;
            case '금': nowTime += 192; break;
            case '토': nowTime += 240; break;
            default : break;
        }

        return nowTime;
    }
}
