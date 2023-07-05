package com.maru.inunavi.navi.service;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.repository.LectureRepository;
import com.maru.inunavi.navi.domain.dto.*;
import com.maru.inunavi.navi.domain.entity.Node;
import com.maru.inunavi.navi.domain.entity.Path;
import com.maru.inunavi.navi.domain.entity.Place;
import com.maru.inunavi.navi.repository.node.NodeRepository;
import com.maru.inunavi.navi.repository.path.PathRepository;
import com.maru.inunavi.navi.repository.place.PlaceRepository;
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

                    nearNodeList.set(nearNodeIdx, nearNodeList.get(nearNodeIdx) + nodeId + ",");
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
                            .location(lineToken.nextToken("\t").replaceAll("\"", ""))
                            .time(lineToken.nextToken("\t").replaceAll("\"", ""))
                            .callNum(lineToken.nextToken("\t"))
                            .etc(lineToken.nextToken("\t"))
                            .build()
            );
        }
    }

    /**
     * Retrieves route in real time
     * @param routeInfo
     * @return PathDto
     */
    @Transactional
    public PathDto getRouteLive(RouteInfo routeInfo){
        return pathRepository.findByRouteInfoQuery(routeInfo.getQuery())
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
     * @return Path
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
        Double dist = distArray[dstIdx];

        // ---- startLocation -> nearNode ---- //
        if(routeInfo.getStartPlaceCode().equals("LOCATION")){
            Double lat1 = Double.parseDouble(routeInfo.getStartLocation().split(",")[0]);
            Double lng1 = Double.parseDouble(routeInfo.getStartLocation().split(",")[1]);

            Node srcNode = nodeRepository.findById(srcId).get();

            Double lat2 = Double.parseDouble(srcNode.getLat4326());
            Double lng2 = Double.parseDouble(srcNode.getLng4326());

            dist += distanceBetween(lat1, lng1, lat2, lng2);

            route = routeInfo.getStartLocation() + "," + route;
        }

        return pathRepository.save(
                Path.builder()
                        .query(query)
                        .isArrived(dist < 15 ? "true" : "false")
                        .dist(dist)
                        .route(route)
                        .dstId(dstIdx)
                        .build()
        );
    }

    /**
     * Retrieves distance between src and dst
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

    /**
     * Retrieves the next class location based on the user's timetable
     * @param email
     * @return NextPlaceDto
     */
    @SneakyThrows
    public NextPlaceDto getNextPlace(String email) {
        return userRepository.findNextLecture(email, convertCurrentTimeToInt())
                .map(lecture -> {

                    String nextPlaceCode = getNextPlaceCode(lecture);

                    return placeRepository.findByPlaceCode(nextPlaceCode)
                            .map(nextPlace ->
                                    NextPlaceDto.builder()
                                            .success("true")
                                            .nextPlaceCode(nextPlace.getPlaceCode())
                                            .nextPlaceTitle(nextPlace.getTitle())
                                            .nextPlaceLocationString(nextPlace.getLocation())
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

    /**
     * Retrieves place based on filter
     * @param placeSearchFilter
     * @return A list of PlaceDto
     */
    public List<PlaceDto> searchPlace(PlaceSearchFilter placeSearchFilter) {
        return placeRepository.findByTitleOrSort(placeSearchFilter.getSearchKeyword())
                .map(placeList ->
                        placeList.stream()
                                .map(place -> {

                                    RouteInfo routeInfo = RouteInfo.builder()
                                            .startPlaceCode("LOCATION")
                                            .startLocation(placeSearchFilter.getMyLocation())
                                            .endPlaceCode(place.getPlaceCode())
                                            .endLocation("LOCATION")
                                            .build();

                                    Integer srcId = nodeRepository.findSrcNodeIdByRouteInfo(routeInfo);
                                    List<Integer> dstIdList = nodeRepository.findDstNodeIdByRouteInfo(routeInfo);

                                    Path path = aStarWithManhattan(srcId, dstIdList, routeInfo);

                                    // NotNull
                                    String dstPlaceCode = nodeRepository.findById(path.getDstId()).get().getPlaceCode();

                                    return PlaceDto.builder()
                                            .placeCode(dstPlaceCode)
                                            .title(place.getTitle())
                                            .sort(place.getSort())
                                            .distance(path.getDist())
                                            .location(place.getLocation())
                                            .time(place.getTime())
                                            .callNum(place.getCallNum())
                                            .build();
                                })
                )
                .orElseGet(Stream::empty)
                .collect(Collectors.toList());
    }

    /**
     Retrieves the overview of routes for a given email.
     <p>The overview includes information about the shortest routes between different lecture locations.
     @param email
     @return A list of RouteOverViewDto.
     */
    public List<RouteOverviewDto> getOverview(String email) {
        return userRepository.findOfflineLecturesByEmail(email)
                .map(lectureList -> {
                    List<RouteOverviewDto> routeOverviewDtoList = new ArrayList<>();
                    boolean[] isClassDay = new boolean[7];

                    List<String> lectureInfoList = lectureList.stream()
                            .flatMap(lecture -> {
                                String[] classTimeList = lecture.getClassTime().split(",");
                                String[] placeCodeList = lecture.getPlaceCode().split(",");

                                Arrays.stream(classTimeList)
                                        .mapToInt(time -> Integer.parseInt(time.split("-")[0]) / 48)
                                        .forEach(day -> isClassDay[day] = true);

                                return IntStream.range(0, classTimeList.length)
                                        .mapToObj(i -> classTimeList[i] + "," + placeCodeList[i] + "," + lecture.getLectureName());
                            })
                            .collect(Collectors.toList());

                    IntStream.range(0, 7)
                            .filter(i -> isClassDay[i])
                            .forEach(i -> lectureInfoList.add((i * 48) + "-?,?,?"));

                    lectureInfoList.sort(Comparator.naturalOrder());

                    IntStream.range(0, lectureInfoList.size() - 1)
                            .forEach(i -> {
                                String[] startInfo = lectureInfoList.get(i).split(",");
                                String[] endInfo = lectureInfoList.get(i + 1).split(",");

                                String startTime = startInfo[0];
                                String endTime = endInfo[0];

                                String endPlaceCode = endInfo[1];

                                String startLectureName = startInfo[2];
                                String endLectureName = endInfo[2];

                                Path shortestPath = null;

                                if (startTime.contains("?")) {
                                    shortestPath = getShortestPath(lectureInfoList.get(i + 1), List.of(22, 60, 67));
                                    startLectureName = Map.of(
                                                    22, "정문(버스정류장)",
                                                    60, "자연과학대학(버스정류장)",
                                                    67, "공과대학(버스정류장)"
                                            )
                                            .get(shortestPath.getSrcId());
                                } else if (!endTime.contains("?")) {
                                    shortestPath = getShortestPath(lectureInfoList.get(i + 1), nodeRepository.findIdByPlaceCode(endPlaceCode));
                                }

                                String formattedTime = timeFormatting(Integer.parseInt(endTime.split("-")[1]));
                                String totalTime = String.valueOf(((shortestPath.getDist() * 1.65) / 100));
                                String distance = shortestPath.getDist().toString();
                                String directionString = shortestPath.getRoute();

                                routeOverviewDtoList.add(
                                        RouteOverviewDto.builder()
                                                .startLectureName(startLectureName)
                                                .endLectureName(endLectureName)
                                                .formattedTime(formattedTime)
                                                .totalTime(totalTime)
                                                .distance(distance)
                                                .directionString(directionString)
                                                .build()
                                );
                            });

                    return routeOverviewDtoList;
                })
                .orElseGet(() -> List.of(RouteOverviewDto.builder().build()));
    }


    /**
     * Returns the analysis result based on a weekly lecture schedule.
     * @param email
     * @return AnalysisDto
     */
    public AnalysisDto getAnalysisResult(String email) {
        return userRepository.findOfflineLecturesByEmail(email)
                .map(lectureList -> {

                    boolean[] isClassTime = new boolean[336];

                    lectureList.stream()
                            .forEach(lecture -> {
                                String[] classTimeList = lecture.getClassTime().split(",");

                                for (String classTime : classTimeList) {
                                    int start = Integer.parseInt(classTime.split("-")[0]);
                                    int end = Integer.parseInt(classTime.split("-")[1]);

                                    for(int i = start; i <= end; i++){
                                        isClassTime[i] = true;
                                    }
                                }
                            });

                    List<RouteOverviewDto> overviewList = getOverview(email);

                    double totalDist = overviewList.stream()
                            .mapToDouble(overview -> Double.parseDouble(overview.getDistance()))
                            .sum();

                    String distancePercentage = String.valueOf((int) totalDist / 50);
                    String tightnessPercentage = String.valueOf(
                            IntStream.range(0, isClassTime.length - 1)
                                    .map(i -> (isClassTime[i-1] && !isClassTime[i] && isClassTime[i+1]) ? 15 : 0)
                                    .sum()
                    );
                    String totalDistance = String.valueOf(totalDist);

                    return AnalysisDto.builder()
                            .success("true")
                            .distancePercentage(distancePercentage)
                            .tightnessPercentage(tightnessPercentage)
                            .totalDistance(totalDistance)
                            .build();
                })
                .orElseGet(() ->
                        AnalysisDto.builder()
                                .success("false")
                                .distancePercentage("0")
                                .tightnessPercentage("0")
                                .totalDistance("0")
                                .build()
                );
    }

    private int convertCurrentTimeToInt() {
        Date today = new Date();

        SimpleDateFormat dayOfWeek = new SimpleDateFormat("E");
        SimpleDateFormat hourOfToday = new SimpleDateFormat("HH");
        SimpleDateFormat minOfToday = new SimpleDateFormat("mm");

        int intHourOfToday = Integer.parseInt(hourOfToday.format(today));
        int intMinOfToday = Integer.parseInt(minOfToday.format(today));

        int currentTime = intHourOfToday * 2 + intMinOfToday / 30;

        switch(dayOfWeek.format(today).charAt(0)){
            case '화': currentTime += 48; break;
            case '수': currentTime += 96; break;
            case '목': currentTime += 144; break;
            case '금': currentTime += 192; break;
            case '토': currentTime += 240; break;
            default : break;
        }

        return currentTime;
    }

    private String timeFormatting(int classTime) {

        String[] dayOfWeeks = {"월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};

        int timeOfDay = classTime % 48;

        String meridiem = (timeOfDay >= 26) ? "PM" : "AM";
        if (meridiem.equals("PM")) {
            timeOfDay -= 24;
        }

        String hour = String.format("%02d", timeOfDay / 2);
        String minute = (timeOfDay % 2 == 0) ? ":00" : ":30";

        return dayOfWeeks[classTime / 48] + " " + hour + minute + " " + meridiem;
    }

    private String getNextPlaceCode(Lecture lecture) {

        // NotNull
        List<Integer> startTimeList = lecture.getStartTimeList();

        int nowTime = convertCurrentTimeToInt();
        int idx = -1;
        for (int i = 0; i < startTimeList.size() && idx == -1; i++) {
            if(startTimeList.get(i) / 48 == nowTime / 48){
                idx = i;
            }
        }

        String classRoomRaw = lecture.getClassRoomRaw().split(",")[idx];
        String detailClassRoom = classRoomRaw.split("-")[1].split(" ")[0];

        return lecture.getClassRoom().split(",")[idx].split(detailClassRoom)[0];
    }

    private Path getShortestPath(String nextTimeAndIdPlaceCode, List<Integer> srcIdList) {

        Path shortestPath = null;
        Double shortestDist = 1e9;

        for (Integer srcId : srcIdList) {

            Node node = nodeRepository.findById(srcId).get();

            RouteInfo nodeToNextClassInfo = RouteInfo.builder()
                    .startPlaceCode("")
                    .startLocation(node.getLat4326() + "," + node.getLng4326())
                    .endPlaceCode(nextTimeAndIdPlaceCode.split(",")[2])
                    .endLocation("")
                    .build();

            Path path = aStarWithManhattan(srcId, nodeRepository.findDstNodeIdByRouteInfo(nodeToNextClassInfo), nodeToNextClassInfo);

            if(path.getDist() < shortestDist){
                shortestPath = path;
                shortestDist = path.getDist();
            }
        }

        return shortestPath;
    }
}
