// OOSimL v 1.1 File: Arrivals.java, Tue Jan 24 16:18:01 2017
 
import java.util.*; 
import psimjava.*; 
/** 
description
  The model of the Car-wash system
  OOSimL version J Garrido, updated Jan 2017.
  This class defines the behavior of the environment
  that generates car arrivals randomly.
  The object of this class creates and starts the car objects
  at specific instants called arrival events.
  An negative exponential distribution is used to generate 
  random numbers that correspond to the inter-arrival period.
  The service periods for car objects are random
  variables from a Normal distribution.
  */
 public  class Arrivals  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private int  carnum = 0; 
  // car inter-arrival random period 
 // 
  private NegExp arrivalPeriod; 
 // car service period random number  
  private Normal servicePeriod; 
 public Arrivals(  String  arrname, double  Mean_arr, double  Mean_serv, double  stdserv) { 
 // 
 // create random number generator using mean inter-arrival period 
super(arrname); 
 // exponential distribution 
 // create random number generator using mean service period 
arrivalPeriod = new NegExp("Inter-arrival", Mean_arr);
 // Normal distribution 
servicePeriod = new Normal("Service Period", Mean_serv, stdserv);
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
 // 
 simclock = StaticSync.get_clock();
 // repeat until time to close arrivals 
 while ( simclock < Carwash.close_arrival ) { 
 // generate inter-arr 
 inter_arr = arrivalPeriod.fdraw(); 
  delay(inter_arr);
 // wait for inter-arr 
 // 
 carnum++;
 // generate service interval 
 serv_per = servicePeriod.fdraw(); 
 // display "Arrivals creating Car at: ", simclock 
simclock =  get_clock();
 // tracewrite "Arrivals creating Car" 
carname =  "Car" + carnum;
carobj = new Car(carname, carnum, serv_per);
 carobj.start();
 } // endwhile 
Carwash.set_num_arrived(carnum); 
 // display "Arrivals terminates" 
  terminate();
 }  // end Main_body 
} // end class/interf Arrivals 
