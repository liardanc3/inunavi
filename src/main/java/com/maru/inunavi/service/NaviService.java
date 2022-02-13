package com.maru.inunavi.service;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.Navi;
import com.maru.inunavi.repository.AllLectureRepository;
import com.maru.inunavi.repository.NaviRepository;
import com.maru.inunavi.repository.UserInfoRepository;
import com.maru.inunavi.repository.UserLectureRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
@RequiredArgsConstructor
public class NaviService {

    private final NaviRepository _NaviRepository;
    private final UserInfoRepository _UserInfoRepository;
    private final UserLectureRepository _UserLectureRepository;
    private final AllLectureRepository _AllLectureRepository;

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

    public List<Navi> updateNavi() {

        _NaviRepository.deleteAll();
        _NaviRepository.deleteINCREMENT();

        List<Navi> NL = new ArrayList<Navi>();

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
                    _nearNode = _nearNode.substring(1,_nearNode.length()-2);

                // 좌표계 변환
                String _epsg3857 = csv.get(2).substring(1,csv.get(2).length()-2);
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
        return _NaviRepository.findAll();
    }


}
