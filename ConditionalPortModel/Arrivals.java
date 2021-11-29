// OOSimL v 1.1 File: Arrivals.java, Wed Apr 10 19:48:45 2019
 
import java.util.*; 
import psimjava.*; 
/** 
description
 OOSimL model of a Port System with conditional waiting.
Ships arrive at a port and unload their cargo. Ships request
2 tugboats and a pier (harbor deck). To leave the ships request 1 tugboat.
The activities of a ship, ddocking, unloading, and leaving, 
have a finite period of duration.
If the resources are not available, ships have to wait.
Condition: Ships are only allowed to dock if 2 tugboats are
available and the tide is not low. 
The tide changes every 13 time units. The low tide lasts
 for 4 time units. The tide is modeled as a process.
This system is similar to an operating system in which
processes have conditional waiting, using conditional
variables in monitors.
An object of this class creates ship objects randomly using a negative 
exponential distribution (NegExp), given the mean inter-arrival period.

 (C) J M Garrido June 2011. Updated Nov. 2014.
 Department of Computer Science, Kennesaw State University
 
 Main class: Cport  File: Arrivals.osl 
*/
 public  class Arrivals  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private String  arrname; 
 private String bigarrname;

  private double  unloadmean; 
  private double  bigunloadmean;
  
  private double  unloadstd;
  private double bigunloadstd;
  
   private NegExp next; 
   private NegExp bignext;
 // Random number generator 
  private Normal unload; 
  private Normal bigunload;
 // Random number generator 
 public Arrivals(String  arrname, String biglarrname, double  arr_mean, double bigarr_mean, 
                 double  umean, double bigumean, double  unlstd, double bigunlstd) { 
   
super(arrname); 
 // generator of random numbers for inter-arrival times  
arrname =  arrname;
bigarrname = biglarrname;
 // Negative exponential probability distribution 
System.out.println(arrname+ " arr_mean: "+ arr_mean+ " umean: "+ umean+ " unlstd: "+ unlstd);
System.out.println(bigarrname+ " big_arr_mean: "+ bigarr_mean+ " umean: "+ bigumean+ " unlstd: "+ bigunlstd);
next = new NegExp("Ship inter-arrival", arr_mean);
bignext = new NegExp("Big Ship inter-arrival", bigarr_mean);
 //  
unloadmean =  umean;
bigunloadmean = bigumean;
 // unload mean interval (Normal dist)  
 // unload standard deviation  
unloadstd =  unlstd;
bigunloadstd  = bigunlstd;
 // random unload interval 
unload = new Normal("Ship Unload interval", unloadmean, unloadstd);
bigunload = new Normal("Big Ship Unload interval", bigunloadmean, bigunloadstd);
 } // end initializer 
 // 
 public void  Main_body(   ) { 
 double  simclock; 
  double  inter_arr; 
  double big_inter_arr;
  // inter-arrival interval 
 double  unloadper; 
 double big_unloadper;
  // unload period 
 int  boatnum; 
 int  bigboatnum;
   Ship lboat; 
   BigShip llboat;
 simclock = StaticSync.get_clock();
 while ( simclock <= Cport.close_arrival ) { 
 // generate random interarrival interval 
 inter_arr = next.fdraw(); 
 big_inter_arr = next.fdraw();
System.out.println(arrname+ "Ship Inter-arrival interval: "+ (inter_arr));
System.out.println(bigarrname+ " Big Ship Inter-arrival interval: "+ (big_inter_arr));
 // 
  delay(inter_arr + big_inter_arr);
 // wait for ship arrival 
 Cport.numarrivals++;
 Cport.bignumarrivals++;
 // increment ship counter 
 // ship number 
 // 
boatnum =  Cport.numarrivals;
bigboatnum = Cport.bignumarrivals;
 // random unload interval 
 unloadper = unload.fdraw(); 
 big_unloadper = bigunload.fdraw();

lboat = new Ship("Ship" + boatnum, unloadper);
 lboat.start();
 simclock = StaticSync.get_clock();
 
 llboat = new BigShip("BigShip" + bigboatnum, unloadper);
 llboat.start();
 simclock = StaticSync.get_clock();
 } // endwhile 
System.out.println(arrname+ " terminates at: "+ simclock);
  terminate();
 }  // end Main_body 
} // end class/interf Arrivals 
