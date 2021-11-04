package com.maru.inunavi.service;

import com.maru.inunavi.entity.lecture;
import com.maru.inunavi.repository.lectureRepository;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class lectureService {
    private final lectureRepository _lectureRepository;

    public lectureService(lectureRepository _lectureRepository){
        this._lectureRepository = _lectureRepository;
    }

    public List<lecture> allLecture(){
        return _lectureRepository.findAll();
    }

    public List<lecture> updateLecture(){
        _lectureRepository.deleteAll();
        _lectureRepository.deleteINCREMENT();

        updateA();
        updateB();
        updateC();
        updateD();
        return _lectureRepository.findAll();
    }



    // updateA = 교양선택
    public void updateA(){
        try{
            File file = new File("src/main/resources/hwp2string/A_교양선택.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = null, tmp= null;
            while((line = bufReader.readLine()) != null){
                //System.out.println(line);
                List<String> csv = new ArrayList<>();
                StringTokenizer s = new StringTokenizer(line);
                while(s.hasMoreTokens()){
                    tmp = s.nextToken(",");
                    System.out.println(tmp);
                    csv.add(tmp);
                }

                String _age = csv.get(0);
                String _category = csv.get(1);
                String _number = csv.get(2);
                String _lectureName = csv.get(3);
                String _point = csv.get(4);
                String _professor = csv.get(5);
                String _daytime1 = "-";
                String _daytime2 = "-";
                String _daytime3 = "-";
                String _position1 = "-";
                String _position2 = "-";
                String _position3 = "-";
                String _how = csv.get(7);
                String _who = "-";
                String _etc = "-";

                if(!csv.get(6).equals("-")){
                    StringTokenizer ss = new StringTokenizer(csv.get(6));
                    tmp = ss.nextToken("(");
                    _daytime1 = tmp;
                    tmp = ss.nextToken(")");
                    _position1 = tmp.substring(1,tmp.length());
                }

                lecture _lecture = new lecture(_age,_category,_number,_lectureName,_point,
                        _professor,_daytime1,_daytime2,_daytime3,_position1,_position2,_position3,_who,_how,_etc);
                _lectureRepository.save(_lecture);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // updateB = 교양필수
    public void updateB(){
    }


    // updateC = 학과개설
    public void updateC(){

    }

    // updateD = 일반선택
    public void updateD(){
        try{
            File file = new File("src/main/resources/hwp2string/D_일반선택.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = null, tmp= null;
            while((line = bufReader.readLine()) != null){
                List<String> csv = new ArrayList<>();
                StringTokenizer s = new StringTokenizer(line);
                while(s.hasMoreTokens()){
                    tmp = s.nextToken(",");
                    System.out.println(tmp);
                    csv.add(tmp);
                }
                String _age = csv.get(0);
                String _category = csv.get(1);
                String _number = csv.get(2);
                String _lectureName = csv.get(3);
                String _point = csv.get(4);
                String _professor = csv.get(5);
                String _daytime1 = "-";
                String _daytime2 = "-";
                String _daytime3 = "-";
                String _position1 = "-";
                String _position2 = "-";
                String _position3 = "-";
                String _how = csv.get(7);
                String _who = "-";
                String _etc = "-";

                if(!csv.get(6).equals("-")){
                    StringTokenizer ss = new StringTokenizer(csv.get(6));
                    tmp = ss.nextToken("(");
                    _daytime1 = tmp;
                    tmp = ss.nextToken(")");
                    _position1 = tmp.substring(1,tmp.length());
                }

                lecture _lecture = new lecture(_age,_category,_number,_lectureName,_point,
                        _professor,_daytime1,_daytime2,_daytime3,_position1,_position2,_position3,_who,_how,_etc);
                _lectureRepository.save(_lecture);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
