/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU;

import java.util.ArrayList;

/**
 *
 * @author s.bikov
 */
public class ParserCSV {
    private ArrayList<ArrayList<String>> myArray;
    private ArrayList<Parameter> parameterArray;

    public ArrayList<Parameter> getParameterArray() {
        return parameterArray;
    }

    public ParserCSV(String s) {
        this.myArray = parse(s);
        this.parameterArray = arrayToParameters(this.myArray);
    }

    public ArrayList<ArrayList<String>> parse(String s){
        ArrayList<ArrayList<String>> yArrayList = new ArrayList<>();

        String [] arrayOfStrings = s.split("\n");

        for (int i = 0; i< arrayOfStrings.length; i++){
            String [] arrayOfColumns = arrayOfStrings[i].split(";");
            ArrayList<String> xArrayList = new ArrayList<>();


            for (int j = 0; j< arrayOfColumns.length; j++){
                xArrayList.add(arrayOfColumns[j]);
            }
            yArrayList.add(xArrayList);
        }

        return yArrayList;
    }

    public void printArray(){
        for(ArrayList<String> a: this.myArray){
            for(String s: a){
                System.out.print(s + "");
            }
            System.out.println();
        }
    }

    public static void printArray(ArrayList<ArrayList<String>> array){
        for(ArrayList<String> a: array){
            for(String s: a){
                System.out.print(s + "");
            }
            System.out.println();
        }
    }

    public String arrayToString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < this.myArray.size(); i++){
            for(int j = 0; j < this.myArray.get(i).size(); j++){
                if (j!=0){
                    stringBuilder.append(",");
                }
                stringBuilder.append(this.myArray.get(i).get(j));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public String getCell(int x, int y){
        return null;
    }

    public void setCell(int x, int y, String string){
        this.myArray.get(y).set(x, string);
    }

    public void restoreState(ArrayList<ArrayList<String>> array){
        this.myArray = array;
    }

    public ArrayList<ArrayList<String>> getMyArray() {
        return myArray;
    }

    public boolean isCorrectCoords(int x, int y){
        if (x < 0 || y < 0 || (y > this.myArray.size()-1) || (x > this.myArray.get(y).size()-1)){
            System.out.println("you are wrong with your coords!");
            return false;
        }
        return true;
    }
    
    private ArrayList<Parameter> arrayToParameters(ArrayList<ArrayList<String>> myArray){
        ArrayList<Parameter> parametersArrayList = new ArrayList();
        for(int i= 8; i< myArray.size(); i++){
            Parameter param = new Parameter.ParameterBuilder()
                    .name(myArray.get(i).get(0))
                    .comment(myArray.get(i).get(1))
                    .address(Integer.parseInt(myArray.get(i).get(2)))
                    .numOfRegs(Short.parseShort(myArray.get(i).get(4)))
//                    .funcToRead(Short.parseShort(myArray.get(i).get(5)))
//                    .funcToWrite(Short.parseShort(myArray.get(i).get(6)))
                    .dataType(myArray.get(i).get(7))
                    .build();
            if (myArray.get(i).get(5).equals("-")){
                param.funcToRead = 0;
            }else{
                param.funcToRead = Short.parseShort(myArray.get(i).get(5));
            }
            if (myArray.get(i).get(6).equals("-")){
                param.funcToWrite = 0;
            }else{
                param.funcToWrite = Short.parseShort(myArray.get(i).get(6));
            }
            parametersArrayList.add(param);
        }
        return parametersArrayList;
    }
    
    public void printParameterArray(){
        for(Parameter param: parameterArray){
            System.out.println(param.toString());
        }
    }
}
