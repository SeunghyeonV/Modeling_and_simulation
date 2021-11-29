// OOSimL v 1.1 File: Cport.java, Wed Apr 10 19:48:56 2019

import java.util.*; 
import java.io.*; 
import java.text.DecimalFormat; 
import psimjava.*; 
/** 
 description
 OOSimL model of a Port System with conditional waiting.
 Ships arrive at a port and unload their cargo. Ships request
 2 tugboats and a pier (harbor deck). To leave the ships 
 request 1 tugboat.
 The activities of a ship, ddocking, unloading, and leaving, 
 have a finite period of duration.
 If the resources are not available, ships have to wait.
 
 Condition: Ships are only allowed to dock if 2 tugboats are
 available and the tide is not low. 
 The tide changes every 13 hours. The low tide lasts
 for 4 hours. The tide is modeled as a process.
 This system is similar to an operating system in which
 processes have conditional waiting, using conditional
 variables in monitors.
 
 (C) J M Garrido, Updated Nov. 2014.
 Dept of Computer Science, Kennesaw State University
 
 Main class: Cport, File: Cport.osl
 */
public  class Cport  extends psimjava.Process     {
  static Scanner scan = new Scanner (System.in);
  private static double  arr_mean; 
  private static double big_arr_mean;
  // mean inter-arrival period
  
  private  static double  unloadmean; 
  private static double big_unloadmean;
  // mean unload interval  
  
  private  static double  unloadstdev; 
  private  static double  big_unloadstdev;
  // unload standard dev 
  
  // global variables 
  public  static int  numarrivals = 0; 
  public  static int bignumarrivals = 0;
  // number of arriving ships 
  // accum wait period 
  public  static double  acc_wait = 0.0; 
  public  static double  big_acc_wait = 0.0;
  // 
  public  static double  simperiod; 
  // simulation time period 
  public  static double  close_arrival; 
  // close time for arrival 
  public  static Condq dockq; 
  // conditional wait 
  public  static Res tugs; 
  // tugboats 
  // jetties 
  public  static Res piers; 
  // 
  public  static Simulation run; 
  // 
  public  static Arrivals carrivals; 
  public  static Tide cur_tide; 
  public  static Cport port; 
  
  public  static PrintWriter statf; 
  // file for statistics 
  public  static PrintWriter tracef; 
  // file for trace 
  public Cport(String  portname) { 
    super(portname); 
  } // end initializer 
  // 
  // function to evaluate condition 
  // there must be atleast two tugboats available and 
  // the tide must not be low 
  static  public boolean  tugs_low_tide(   ) { 
    if ( (( tugs.num_avail() >= 2) ) && (( cur_tide.get_lowtide() !=  true ) )) { 
      return  true ; 
    }
    else {
      return  false ; 
    } // endif 
  }  // end tugs_low_tide 
  // 
  static  public boolean  btugs_low_tide(   ) { 
    if ( (( tugs.num_avail() >= 3) ) && (( cur_tide.get_lowtide() !=  true ) )) { 
      return  true ; 
    }
    else {
      return  false ; 
    } // endif 
  } 
  
  
  public static void main(String[] args) { 
    simperiod =  3760.5;
    // simulation period 
    // time to close ship arrivals 
    close_arrival =  2073.5;
    // 
    arr_mean =  46.45;
    big_arr_mean = 56.45;
    // mean inter-arrival period for ships 
    
    unloadmean =  24.5;
    big_unloadmean = 30.5;
    // mean unload interval  
    unloadstdev =  3.85;
    big_unloadstdev = 5.25;
    // unload standard dev 
    // 
    run = new Simulation( "Port System with Conditions" );
    // setup files for reporting 
    Simulation.set_tracef("cporttrace.txt");
    tracef = Simulation.get_tracefile();
    // 
    Simulation.set_statf("cportstatf.txt");
    statf = Simulation.get_statfile();
    // create standard resource objects 
    tugs = new Res("Tugs", 4);
    // Originally 3, increased to 4
    piers = new Res("Piers", 2);
    // Conditional wait object 
    // 
    dockq = new Condq("Dock cond queue");
    // main active object of model 
    port = new Cport("Port with Conditions");
    port.start();
  }  // end main 
  // 
  public void  Main_body(   ) { 
    double  var1; 
    String  fmtout1; 
    // format object round output to three decimal positions 
    DecimalFormat dfmt; 
    dfmt = new DecimalFormat("0.###");
    statf =  Simulation.get_statfile();
    // access statistics file 
    // access trace file     
    tracef =  Simulation.get_tracefile();
    // 
    System.out.println("Initiating Arrivals with mean inter-arrival: "+ (arr_mean));
     System.out.println("Initiating Arrivals with mean inter-arrival: "+ (big_arr_mean));
    carrivals = new Arrivals("Arrivals ", "Big arrivals", arr_mean, big_arr_mean, unloadmean, big_unloadmean, 
                             unloadstdev, big_unloadstdev);
    carrivals.start();
    // 
    cur_tide = new Tide("Tide");
    cur_tide.start();
    // 
    System.out.println("Starting simulation ...");
    run.start_sim( simperiod );
    // 
    statf.println("Total number of ships that arrived: "+ (numarrivals+bignumarrivals));
    statf.flush();
    System.out.println("Total number of ships that arrived: "+ (numarrivals+bignumarrivals));
    var1 =  (acc_wait + big_acc_wait)/(numarrivals + bignumarrivals);
    fmtout1 =  dfmt.format(var1);
    statf.println("Average wait period: "+ fmtout1);
    statf.flush();
    System.out.println("Average wait period: "+ fmtout1);
    // 
//System.exit(1); 
  }  // end Main_body 
} // end class/interf Cport 
