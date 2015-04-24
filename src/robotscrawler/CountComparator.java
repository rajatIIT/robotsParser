/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotscrawler;

import java.util.Comparator;

/**
 *
 * @author rajatpawar
 */
public class CountComparator implements Comparator<String>{

    @Override
    public int compare(String o1, String o2) {
        return (Integer.parseInt(o1) - Integer.parseInt(o2));
    }

    
}
