package com.maru.inunavi.service;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.Navi;
import com.maru.inunavi.entity.NodePath;
import com.maru.inunavi.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NaviService {

    private final NaviRepository _NaviRepository;
    private final UserInfoRepository _UserInfoRepository;
    private final UserLectureRepository _UserLectureRepository;
    private final AllLectureRepository _AllLectureRepository;
    private final NodePathRepository _NodePathRepository;

    //pair<double,int>
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

    public double distanceNode(String _src, String _dst){
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
    }

    public NodePath dijkstraPatial(int src, int dst, ArrayList<ArrayList<Integer>> AL){
        int[] pathTrace = new int[(int)_NaviRepository.count()+2];
        double[] distArray = new double[(int) _NaviRepository.count()+2];
        for(int i=1; i<distArray.length; i++)
            distArray[i] = 1e9;
        distArray[src]=0.f;
        PriorityQueue<pair> pq = new PriorityQueue<>();
        Stack<String> st = new Stack<>();
        pq.add(new pair(0,src));
        while(!pq.isEmpty()){
            System.out.println("pq.size() == "+pq.size());
            double dist = pq.peek().getDist();
            int now = pq.peek().getNode();
            System.out.println("now = "+now);
            pq.remove();

            if(dist < distArray[now]) continue;

            String now_Epsg4326 = _NaviRepository.getById(now).getEpsg4326();
            now_Epsg4326 = now_Epsg4326.substring(1,now_Epsg4326.length()-1);

            for(int i=0; i<AL.get(now).size(); i++) {
                int next = AL.get(now).get(i);
                System.out.println(next);
                if(next==0) break;
                System.out.println("next = "+next);

                String next_Epsg4326 = _NaviRepository.getById(next).getEpsg4326();
                next_Epsg4326 = next_Epsg4326.substring(1,next_Epsg4326.length()-1);

                double _dist = distanceNode(now_Epsg4326,next_Epsg4326);
                System.out.println("dist = "+_dist);
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

        for(int i=1; i<=500; i++){
            System.out.println(i+">"+pathTrace[i]);
        }
        System.out.println("탈출");
        String src2dst = Integer.toString(src)+"to"+Integer.toString(dst);
        Double dist = distArray[dst];
        String path = "";
        int idx = dst;
        while(idx!=0){
            System.out.print(idx + ", ");
            String epsg4326 = _NaviRepository.getById(idx).getEpsg4326();
            st.push(epsg4326);
            idx = pathTrace[idx];
        }
        while(!st.isEmpty()){
            path += st.peek()+",";
            st.pop();
        }
        path = path.substring(0,path.length()-1);

        NodePath _NodePath = new NodePath(src2dst,dist,path);
        System.out.println(_NodePath.getSrc2dst() + " " + _NodePath.getPath() + "\n" + _NodePath.getDist()+"m");
        return _NodePath;
    }

    public boolean dijkstraOverall(ArrayList<ArrayList<Integer>> AL){
        try{
            int len = (int)_NaviRepository.count();
            ArrayList<NodePath> _NAL = new ArrayList<>();
            for(int i=1; i<2; i++) for(int j=i+1; j<=3; j++)
                _NAL.add(dijkstraPatial(i,j,AL));
            _NodePathRepository.saveAll(_NAL);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

                // nodeNum, nearNode, placeCode, node, title, sort
                for (int i = 0; i <= 6; i++) {
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

                Navi _Navi = new Navi(csv, _epsg4326);
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
}
