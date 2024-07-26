package sample.krankenhaussystem;

import java.io.Serializable;
import java.util.ArrayList;

public class ListOfPatients implements Serializable {
    private ArrayList<Patient> list;
    public ListOfPatients(){
        list = new ArrayList<>();
    }
    public void add(Patient p){
        if (list.size()<1000){
            list.add(0,p);
        }else {
            list.remove(list.size()-1);
            list.add(0,p);
        }
    }
    public Patient get(int i){
        return list.get(i);
    }
    public ArrayList<Patient> getList(){
        return list;
    }
    public int getSize(){
        return list.size();
    }
}
