// OOSimL v 1.1 File: Btruck.java, Mon Mar  4 18:31:25 2019
 
import java.util.*; 
import psimjava.*; 
import java.util.*; 
/** 
description
   An OOSimL model of a busy warehouse system
   Small and big trucks constantly arrive to a warehouse to unload
   goods. Each truck needs an unloading bay to dock and start
   unloading. Big trucks need three workers and a coantant time interval
    for docking and undocking. Trucks may need to wait for an
   available unloading bay and/or wait for available workers.
   
   Both types of trucks have different mean arrival rates and
   different mean unloading periods.
   The warehouse has 3 unloading bays and 7 workers.
   This class defines the behavior of big trucks
   
   (C) J. Garrido, September 2012. Updated Nov. 2016
   Department of Computer Science, Kennesaw State University
   File:    Btruck.osl
*/
 public  class Btruck  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 final private double  DOCK_INTERV = 1.45; 
  final private double  UNDOCK_INTERV = 1.15; 
  private int  truckNum; 
  // Truck number 
 private double  arrivalTime; 
  // arrival time  of truck 
 private double  service_dur; 
  // truck unloading interval 
 private String  bname; 
  // 
 public Btruck(  String  btname, double  unload_dur) { 
super(btname); 
truckNum =  Wareh.get_bnumArr();
 arrivalTime = StaticSync.get_clock();
service_dur =  unload_dur;
 // unloading interval 
bname =  btname;
 } // end initializer 
 // 
 public double  get_arrivalT(   ) { 
return arrivalTime; 
 }  // end get_arrivalT 
 // 
 public double  get_serv_dur(   ) { 
return service_dur; 
 }  // end get_serv_dur 
 // 
 public void  Main_body(   ) { 
 double  start_wait; 
  // time truck starts to wait 
 double  wait_p; 
  // wait period for truck 
 double  bsojourn; 
  // truck total interval in warehouse 
 double  simclock; 
  // setb the start wait time for this truck 
 // 
 start_wait = StaticSync.get_clock();
 // display bname, " arrives, requesting unloading bay at ", start_wait 
 // process will wait for an unload bay  
tracedisp(bname+ " arrives, requesting unloading bay ");
 // 
 // 1 unloading bay 
 Wareh.bays.acquire(1);
 // 
 simclock = StaticSync.get_clock();
 // display bname, " acquired unloading bay at: ", simclock 
 // 
tracedisp(bname+ " acquired unloading bay");
 // docking 
 // 
  delay(DOCK_INTERV);
 // display bname, " requesting 4 workers at ", simclock 
 // 
 // process will wait until 4 workers become available 
tracedisp(bname+ " requesting 4 workers");
 // 
 // 3 workers 
 Wareh.workers.acquire(4);
 // 
 simclock = StaticSync.get_clock();
 // display bname, " acquired workers at: ", simclock 
 // 
tracedisp(bname+ " acquired workers, starts unloading");
 // wait period for this truck 
wait_p =  (get_clock()) - (start_wait);
 // accumulate truck wait interval 
 // accumulate wait period 
 // 
Wareh.accumBWait(wait_p); 
 // now the unloading service of the truck 
 // unloading interval 
 //       
 // service completed, release workers 
  delay(service_dur);
 // 
 // 
 Wareh.workers.release(4);
 // undock 
 // 
  delay(UNDOCK_INTERV);
 // ok, now release the bay 
 // 
 Wareh.bays.release(1);
 // increment num big trucks serviced 
Wareh.incr_bigTserv(); 
 // total time in warehouse: bSojourn 
bsojourn =  (get_clock()) - (arrivalTime);
 // accumulate sojourn time 
Wareh.accum_Bsojourn(bsojourn); 
 // 
 simclock = StaticSync.get_clock();
tracedisp(bname+ " finished unloading, departs");
 // display bname, " finished unloading, departs at ", simclock 
  terminate();
 //terminates itself 
 }  // end Main_body 
} // end class/interf Btruck 
