/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester;

import ModbusTester.parameter.TmpParam;
import java.util.ArrayList;

/**
 *
 * @author s.bikov
 */
public class PackageItem {

    String name;
    public ArrayList<TmpParam> paramArray;

    public PackageItem(String name) {
        this.name = name;
        paramArray = new ArrayList<>();
    }
}
