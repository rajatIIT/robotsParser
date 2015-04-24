/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotscrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.archive.modules.net.Robotstxt;

/**
 *
 * @author rajatpawar
 */
public class RobotsCrawler {
    
    static ArrayList<String> URLList;
    static TreeMap<String,String> sortedStats;
    static ArrayList<String> alreadyCrawled;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException{
        // TODO code application logic here
        
        alreadyCrawled = new ArrayList<String>();
        String URLFilePath = args[0];
        String resultDirectoryPath = args[1];
        URLList = new ArrayList<String>();
        HashMap misbehavedCrawlers = new HashMap();
        ArrayList robotsNotExistsList = new ArrayList();
        ArrayList unableToConnectURLList = new ArrayList();
        int totalURLS=0, totalCrawled=0,totalNoPolicy=0, totalConnectionRefused=0;
        
        
        try {
            readURLS(URLFilePath);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RobotsCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        Iterator<String> URLFileIterator = URLList.iterator();
        String currentURL;
        while(URLFileIterator.hasNext())
        {
            
            currentURL = URLFileIterator.next();
            URL tempURL = new URL(currentURL);
            if (!(alreadyCrawled.contains(tempURL.getProtocol() + "://" +  tempURL.getHost()))){
            totalURLS++;
            alreadyCrawled.add(tempURL.getProtocol() + "://" +  tempURL.getHost());
            URL myURL = new URL(tempURL.getProtocol() + "://" +  tempURL.getHost() + "/robots.txt");
            URLConnection myConnection;
            try {
                myConnection = myURL.openConnection();
                System.out.println(currentURL);
                System.out.println(myConnection.getHeaderField(0));
            
            BufferedReader URLReader = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
            Robotstxt myParser = new Robotstxt(URLReader);
            List<String> userAgents = myParser.getNamedUserAgents();
            Iterator agentsIterator = userAgents.iterator();
            String nextUserAgent;
            
        
            totalCrawled++;
            while(agentsIterator.hasNext()) {
            // for each user agent
                nextUserAgent = (String)agentsIterator.next();
             //   System.out.println("writing " + nextUserAgent);
                if (misbehavedCrawlers.containsKey(nextUserAgent)) {
                    misbehavedCrawlers.put(nextUserAgent,((int)misbehavedCrawlers.get(nextUserAgent)) + 1);
                } else 
                    misbehavedCrawlers.put(nextUserAgent, 1);
            }
            
           } catch (FileNotFoundException fex) {
                totalNoPolicy++;
                    robotsNotExistsList.add(currentURL);
               //     Logger.getLogger(RobotsCrawler.class.getName()).log(Level.SEVERE, null, fex);
                } catch (IOException ex) {
                   totalConnectionRefused++;
                    unableToConnectURLList.add(currentURL);
                Logger.getLogger(RobotsCrawler.class.getName()).log(Level.SEVERE, null, ex);
            } 
         }  

        }
        
        try {
            // System.out.println(misbehavedCrawlers.toString());
            writeMisbehaved(misbehavedCrawlers,resultDirectoryPath);
            writeRobotsNotExistResults(robotsNotExistsList,resultDirectoryPath);
            writeConnectionRefusedResults(unableToConnectURLList,resultDirectoryPath);
            
            System.out.println("Total URLs Crawled " + totalURLS + ". No Robots: "+ totalNoPolicy + " . Total connection refused: " + totalConnectionRefused + ".");
         //   System.out.println("robots not exist : " + robotsNotExistsList.toString());
        //    System.out.println("unable to connect list : " + unableToConnectURLList.toString());
            
            
//
//        for(int i=0;i<100;i++) {
//        char temp = (char) URLReader.read();
//                
//        System.out.print(temp);
//        
//        }
        } catch (IOException ex) {
            Logger.getLogger(RobotsCrawler.class.getName()).log(Level.SEVERE, null, ex);
            
            
        }
        
        
    }

    private static void writeMisbehaved(HashMap misbehavedCrawlers,String resultDirectoryPath) throws IOException {
        
        File crawlerStats = new File(resultDirectoryPath + File.separator + "misbehavedAgentStats");
        crawlerStats.createNewFile();
        
        
       // System.out.println("Misbehaved Crawlers:" + misbehavedCrawlers.toString());
       // Iterator sortedIterator = getSortesdIterator(misbehavedCrawlers);
        
        
        Iterator mbcIterator = misbehavedCrawlers.keySet().iterator();
        
        
       // Iterator mbcIterator = sortedIterator;
        PrintWriter misheavedCrawlerStatsWriter = new PrintWriter(crawlerStats);
        
        String next,nextString;
        while(mbcIterator.hasNext()) {
           next = (String)mbcIterator.next();
           nextString = next + "\n" +   misbehavedCrawlers.get(next);
        //   System.out.println(nextString);
            misheavedCrawlerStatsWriter.println(nextString);
        }
        misheavedCrawlerStatsWriter.close();
        
    }

    private static void readURLS(String URLFilePath) throws FileNotFoundException {
        Scanner URLFileScanner = new Scanner(new File(URLFilePath));
        ArrayList<String> nonFunctionalSites = new ArrayList<String>();
        while(URLFileScanner.hasNextLine()) {
        URLList.add(URLFileScanner.nextLine());
        }
        URLFileScanner.close();
       
    }
    
    private static void writeRobotsNotExistResults(ArrayList robotsNotExistsList, String resultDirectoryPath) throws FileNotFoundException, IOException {
      File crawlerStats = new File(resultDirectoryPath + File.separator + "noRobots");
        crawlerStats.createNewFile();
        Iterator mbcIterator = robotsNotExistsList.iterator();
        PrintWriter noRobotsWriter = new PrintWriter(crawlerStats);
        while(mbcIterator.hasNext())
            noRobotsWriter.println(mbcIterator.next());
        
        noRobotsWriter.close();
    }

    private static void writeConnectionRefusedResults(ArrayList unableToConnectURLList, String resultDirectoryPath) throws FileNotFoundException, IOException {
          File crawlerStats = new File(resultDirectoryPath + File.separator + "connectionRefused");
        crawlerStats.createNewFile();
        Iterator mbcIterator = unableToConnectURLList.iterator();
        PrintWriter noRobotsWriter = new PrintWriter(crawlerStats);
        while(mbcIterator.hasNext())
            noRobotsWriter.println(mbcIterator.next());
        
        noRobotsWriter.close();
    }

    private static Iterator getSortesdIterator(HashMap misbehavedCrawlers) {
        
        System.out.println("Sorting.");
        HashMap<String,String> tempMap = new HashMap<String,String>();
        
        Iterator misbehavedCIt = misbehavedCrawlers.keySet().iterator();
        
        while(misbehavedCIt.hasNext()){
            
            String nextString = (String)misbehavedCIt.next();
            System.out.println("Processing "+ nextString+ ". ");
            int nextVal = (int)misbehavedCrawlers.get(nextString);
            System.out.println("Count is " + nextVal + ". ");
            tempMap.put(nextVal + "", nextString);
        }
        
        System.out.println("Temp map is "+ tempMap);
        
        CountComparator myCountComparator = new CountComparator();
        
        sortedStats = new TreeMap<String,String>(myCountComparator);
        
        sortedStats.putAll(tempMap);
        
        System.out.println("Sorted stats are " + sortedStats);
        return sortedStats.keySet().iterator();
    
    }

    
}
