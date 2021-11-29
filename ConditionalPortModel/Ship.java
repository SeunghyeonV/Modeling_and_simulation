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
 public  class Ship  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private double  startw; 
  // ship start time of wait 
 private double  unload; 
  // unload interval 
 private double  simclock; 
  // current simulation time 
 private String  shipname; 
   private  static Tide ctide; 
 // ship number 
 public int  shipnum; 
  // 
 public Ship(  String  shname, double  unloadper) { 
super(shname); 
unload =  unloadper;
shipnum =  Cport.numarrivals;
 // ship number 
shipname =  shname;
 } // end initializer 
 // 
 public void  Main_body(   ) { 
 double  wait_per; 
  // wait interval for this ship 
 double  doc_per; 
  // dock interval 
 int  tugsavail; 
  // tug boats available 
 boolean  mcond =  false ; 
  // condition for tide 
 simclock = StaticSync.get_clock();
System.out.println(shipname+ " arrives at: "+ simclock);
tracedisp(shipname+ " arrives ");
 // constant dock period 
doc_per =  22.5;
 // assign simulation clock to simclock 
 // start time to wait 
startw =  simclock;
 // 
System.out.println(shipname+ " requests pier at: "+ simclock);
 // 
tracedisp(shipname+ " requests pier");
 // attempt to acquire pier 
 Cport.piers.acquire(1);
 // 
 simclock = StaticSync.get_clock();
System.out.println(shipname+ " acquired pier at time: "+ simclock);
 // wait for high tide and 2 tugboats 
 // display shipname, " waits until condition true at: ", simclock 
 // tracewrite shipname, " waits until condition true" 
tracedisp(shipname+ " acquired pier");
 //          
mcond =   false ;
 while ( mcond !=  true  ) { 
mcond =  Cport.tugs_low_tide();
 // evaluate condition 
System.out.println(shipname+ " Testing condition: "+ mcond+ " at: "+ simclock);
tracedisp(shipname+ " Testing condition: "+ mcond);
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
 System.out.println("Number of available tugboats: " + tugsavail);
 // 
 // attempt to acquire tugboats 
 Cport.tugs.acquire(2);
 // 
 simclock = StaticSync.get_clock();
System.out.println(shipname+ " acquires 2 tugboats at: "+ simclock);
tracedisp(shipname+ " acquires 2 tugboats");
 // 
wait_per =  (simclock) - (startw);
 // wait interval 
 // accumulate wait 
 //  
 Cport.acc_wait += wait_per;
 // ship now will dock 
 // dock interval 
  delay(doc_per);
 // 
 Cport.tugs.release(2);
 // 
 simclock = StaticSync.get_clock();
System.out.println(shipname+ " has docked releases tugs, signaling dockq at: "+ simclock);
tracedisp(shipname+ " has docked, releases tugs");
 //    
 // 
  Cport.dockq.signal();
 // ship will now unload 
System.out.println(shipname+ " starts to unload for: "+ unload);
tracedisp(shipname+ " starts to unload for: "+ unload);
 // 
 // take time to unload 
  delay(unload);
 // 
 simclock = StaticSync.get_clock();
System.out.println(shipname+ " finished unloading, requesting tugboat at: "+ simclock);
tracedisp(shipname+ " finished unloading, requesting tugboat");
 //    
 // start to wait again 
startw =  simclock;
 // 
 Cport.tugs.acquire(1);
 // 
simclock =  get_clock();
System.out.println(shipname+ " acquires 1 tugboat at "+ simclock);tracedisp(shipname+ " acquires 1 tugboat to undock");
 // 
wait_per =  (simclock) - (startw);
 // 
 Cport.acc_wait += wait_per;
 // undock 
 // time ship takes to undock 
  delay(( (0.65) * (doc_per)) );
 // 
 // release the tugboat 
 Cport.tugs.release(1);
 // 
simclock =  get_clock();
System.out.println(shipname+ " releases tugboat at: "+ 
simclock);
tracedisp(shipname+ " releases tugboat");
 // 
 // release the pier 
 Cport.piers.release(1);
 // 
 // signal condition object 
  Cport.dockq.signal();
 // 
System.out.println(shipname+ " departing at: "+ simclock);
tracedisp(shipname+ " departing");
  terminate();
 // terminate this ship process 
 }  // end Main_body 
} // end class/interf Ship 
