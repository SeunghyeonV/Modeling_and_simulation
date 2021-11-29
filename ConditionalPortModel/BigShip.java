// OOSimL v 1.1 File: Ship.java, Sun Dec 28 20:26:01 2014
 
import java.util.*; 
import psimjava.*; 
/** 
description
 OOSimL model of a Port System with conditional waiting 
 Ships arrive at a port and unload their cargo. Ships request
 2 tugboats and a pier. To leave the ships request 1 tugboat.
 The activities of a ship, docking, unloading, and leaving, 
 have a finite period of duration.
 If the resources are not available, ships have to wait.

 Condition: Ships are only allowed to dock if 2 tugboats are
 available and the tide is not low. 
 The tide changes every 12 hours. The low tide lasts
  for 4 hours. The tide is modeled as a process.

 This system is similar to an operating system in which
 processes have conditional waiting, using conditional
 variables in monitors.

 Activity of a ship object:
 1. conditional wait for tugboats and low tide
 2. dock
 3. unload
 4. undock
 5. leave (terminate)

 Assumptions: 
 1. dock interval is constant
 2. undock interval takes about 65% of dock interval
 (C) J M Garrido June 2011. Updated Nov. 2014.
 Department of Computer Science, Kennesaw State University
 File: Ship.osl
*/
 public  class BigShip  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private double  bigstartw; 
  // ship start time of wait 
 private double  bigunload; 
  // unload interval 
 private double  simclock; 
  // current simulation time 
 private String  bigshipname; 
   private  static Tide ctide; 
 // ship number 
 public int  bigshipnum; 
  // 
 public BigShip(String  bshname, double  bigunloadper) { 
super(bshname); 
bigunload =  bigunloadper;
bigshipnum =  Cport.bignumarrivals;
 // ship number 
bigshipname =  bshname;
 } // end initializer 
 // 
 public void  Main_body(   ) { 
 double  big_wait_per; 
  // wait interval for this ship 
 double  big_doc_per; 
  // dock interval 
 int  tugsavail; 
  // tug boats available 
 boolean  mcond =  false ; 
  // condition for tide 
 simclock = StaticSync.get_clock();
System.out.println(bigshipname+ " arrives at: "+ simclock);
tracedisp(bigshipname+ " arrives ");
 // constant dock period 
big_doc_per =  30.5;
 // assign simulation clock to simclock 
 // start time to wait 
bigstartw =  simclock;
 // 
System.out.println(bigshipname+ " requests pier at: "+ simclock);
 // 
tracedisp(bigshipname+ " requests pier");
 // attempt to acquire pier 
 Cport.piers.acquire(1);
 // 
 simclock = StaticSync.get_clock();
System.out.println(bigshipname+ " acquired pier at time: "+ simclock);
 // wait for high tide and 2 tugboats 
 // display shipname, " waits until condition true at: ", simclock 
 // tracewrite shipname, " waits until condition true" 
tracedisp(bigshipname+ " acquired pier");
 //          
mcond =   false ;
 while ( mcond !=  true  ) { 
mcond =  Cport.btugs_low_tide();
 // evaluate condition 
System.out.println(bigshipname+ " Testing condition: "+ mcond+ " at: "+ simclock);
tracedisp(bigshipname+ " Testing condition: "+ mcond);
 //    
 Cport.dockq.waituntil(mcond);
 // 
 simclock = StaticSync.get_clock();
 // 
 // display shipname, " continues at: ", simclock 
 // tracewrite shipname, " continues" 
 // 
 // 
 } // endwhile 
 // wait for available tugboats 
 // display "Number of available tugboats: ", tugsavail 
 tugsavail = Cport.tugs.num_avail(); 
 // 
 // attempt to acquire tugboats 
 Cport.tugs.acquire(3);
 // 
 simclock = StaticSync.get_clock();
System.out.println(bigshipname+ " acquires 3 tugboats at: "+ simclock);
tracedisp(bigshipname+ " acquires 3 tugboats");
 // 
big_wait_per =  (simclock) - (bigstartw);
 // wait interval 
 // accumulate wait 
 //  
 Cport.acc_wait += big_wait_per;
 // ship now will dock 
 // dock interval 
  delay(big_doc_per);
 // 
 Cport.tugs.release(3);
 // 
 simclock = StaticSync.get_clock();
System.out.println(bigshipname+ " has docked releases tugs, signaling dockq at: "+ simclock);
tracedisp(bigshipname+ " has docked, releases tugs");
 //    
 // 
  Cport.dockq.signal();
 // ship will now unload 
System.out.println(bigshipname+ " starts to unload for: "+ bigunload);
tracedisp(bigshipname+ " starts to unload for: "+ bigunload);
 // 
 // take time to unload 
  delay(bigunload);
 // 
 simclock = StaticSync.get_clock();
System.out.println(bigshipname+ " finished unloading, requesting tugboat at: "+ simclock);
tracedisp(bigshipname+ " finished unloading, requesting tugboat");
 //    
 // start to wait again 
bigstartw =  simclock;
 // 
 Cport.tugs.acquire(1);
 // 
simclock =  get_clock();
System.out.println(bigshipname+ " acquires 1 tugboat at "+ simclock);tracedisp(bigshipname+ " acquires 1 tugboat to undock");
 // 
big_wait_per =  (simclock) - (bigstartw);
 // 
 Cport.big_acc_wait += big_wait_per;
 // undock 
 // time ship takes to undock 
  delay(( (0.8) * (big_doc_per)) );
 // 
 // release the tugboat 
 Cport.tugs.release(1);
 // 
simclock =  get_clock();
System.out.println(bigshipname+ " releases tugboat at: "+ simclock);
tracedisp(bigshipname+ " releases tugboat");
 // 
 // release the pier 
 Cport.piers.release(1);
 // 
 // signal condition object 
  Cport.dockq.signal();
 // 
System.out.println(bigshipname+ " departing at: "+ simclock);
tracedisp(bigshipname+ " departing");
  terminate();
 // terminate this ship process 
 }  // end Main_body 
} // end class/interf Ship 
