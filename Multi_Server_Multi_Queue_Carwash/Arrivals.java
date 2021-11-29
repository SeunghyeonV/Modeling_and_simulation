// OOSimL v 1.1 File: Arrivals.java, Wed Feb 27 22:33:44 2019
 
import java.util.*; 
import psimjava.*; 
/** 
description
  The model of the multi-server multi-queue Car-wash 
  OOSimL version J Garrido, updated Nov 2016

  This class defines the behavior of the environment
  that generates car arrivals, randomly.
  The object of this class creates and starts the car objects
  at specific instants called arrival events.
  An negative exponential distribution is used to generate 
  random numbers that correspond to the inter-arrival period.
  The service periods for car objects are random
  variables from a Uniform distribution.
  */
 public  class Arrivals  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private int  ctype;  

// car inter-arrival random period 
  private NegExp arrivalPeriod; 
 // car service period random number  
  private Uniform servicePeriod; 
 // 
 public Arrivals(  String  arrname, int  carprio, double  Mean_arr, double  min_serv, double  max_serv) { 
 // 
 // create random number generator using mean inter-arrival period 
super(arrname); 
ctype =  carprio; 
// exponential distribution 
 
 // create random number generator using max and min service times 
arrivalPeriod = new NegExp("Inter-arrival type " + ctype, Mean_arr);
 // Uniform distribution 
servicePeriod = new Uniform("Service period type " + ctype, min_serv, max_serv);
 // display arrname, " created" 
 } // end initializer 
 // 
 public void  Main_body(   ) { 
 // inter-arrival period for car object 
 double  inter_arr; 
  // service period   
 double  serv_per; 
  double  simclock; 
  String  carname; 
   Car carobj; 
 // display "Arrivals starting main body at: ", simclock 
 simclock = StaticSync.get_clock();
 // 
 while ( simclock < Mqcarwash.close_arrival ) { 
 // display "Arrivals to generate random values" 
 // generate inter-arr 
 inter_arr = arrivalPeriod.fdraw(); 
  delay(inter_arr);
 // wait for inter-arr 
 // 
 Mqcarwash.num_arrived[ctype]++;
 Mqcarwash.numcar++;
 // generate service interval 
 serv_per = servicePeriod.fdraw(); 
simclock =  get_clock();
 // display "Arrivals creating Car at: ", simclock 
carname =  "Car" + Mqcarwash.numcar;
 // 
carobj = new Car(carname, ctype, serv_per);
 carobj.set_prio(ctype);
 carobj.start();
 // display "Arrivals created car", numcar 
 } // endwhile 
 // display "Arrivals terminates" 
  terminate();
 }  // end Main_body 
} // end class/interf Arrivals 
