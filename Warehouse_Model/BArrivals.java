// OOSimL v 1.1 File: BArrivals.java, Mon Mar  4 18:31:15 2019
 
import java.util.*; 
import psimjava.*; 
import java.util.*; 
/** 
description
   An OOSimL model of a busy warehouse system
   Small and big trucks periodically arrive to a warehouse to unload
   goods. Each truck needs an unloading bay to dock and start
   unloading. Small trucks need two workers for unloading;; big
   trucks need three workers. Trucks may need to wait for an
   available unloading bay then wait for available workers.
   
   Both types of trucks have different mean arrival rates and
   different mean unloading periods.
   The warehouse has 2 unloading bays and 5 workers.
   
   This class defines the behavior of the environment
   The object of this class creates and starts the big truck
   objects at specific instants called arrival events.
   An exponential distribution is used to generate random numbers
   for the inter-arrival period.
   A uniform distribution is used to generate random numbers for the
   service period for the truck object.
   
   (C) J Garrido. Updated Nov. 2016.
   Department of Computer Science, Kennesaw State University
   File: BArrivals.osl
*/
 public  class BArrivals  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private String  aname; 
   private NegExp arrivalPeriod; 
 // random num gen 
  private Uniform servicePeriod; 
 // random number gen 
 public BArrivals(String  pname, double  mean_arr, double  low_serv, double  high_serv) { 
 // 
 // random number generator for inter-arrival times 
super(pname); 
 // using a negative exppnential distribution 
 // 
 // random number generator for service intervals 
arrivalPeriod = new NegExp("Big truck Inter-arr", mean_arr);
 // using a uniform distribution 
servicePeriod = new Uniform("Big truck service", low_serv, high_serv);
aname =  pname;
 // display aname, " created" 
 } // end initializer 
 // 
 public void  Main_body(   ) { 
 double  inter_arr; 
  // inter-arrival period 
 double  serv_per; 
  // service period 
 double  simclk; 
  int  numBigTruck; 
  double  closeTime; 
   Btruck btruckobj; 
 simclk = StaticSync.get_clock();
closeTime =  Wareh.close_arrival;
 // generate random inter-arrival interval 
 inter_arr = arrivalPeriod.fdraw(); 
 // repeat until close arrival time 
 while ( (simclk) + (inter_arr) < closeTime ) { 
 // wait for next arrival 
  delay(inter_arr);
 // increment number of big trucks that arrived 
Wareh.incr_bigTArr(); 
 // 
numBigTruck =  Wareh.get_bnumArr();
 // generate random service interval 
 serv_per = servicePeriod.fdraw(); 
 // 
btruckobj = new Btruck("Btruck" + numBigTruck, serv_per);
 btruckobj.start();
 // generate random inter-arrival interval 
 inter_arr = arrivalPeriod.fdraw(); 
 simclk = StaticSync.get_clock();
 } // endwhile 
 // display aname, "terminates at: ", simclk 
  terminate();
 }  // end Main_body 
} // end class/interf BArrivals 
