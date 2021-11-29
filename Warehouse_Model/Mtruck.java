// OOSimL v 1.1 File: Struck.java, Mon Mar  4 18:30:55 2019
 
import java.util.*; 
import psimjava.*; 
import java.util.*; 
/** 
description
   An OOSimL model of a busy warehouse system
   Small and big trucks constantly arrive to a warehouse to unload
   goods. Each truck needs an unloading bay to dock and start
   unloading. Small trucks need two workers for unloading;; big
   trucks need three workers. Trucks may need to wait for an
   available unloading bay then wait for available workers.
   
   Both types of trucks have different mean arrival rates and
   different mean unloading periods.
   The warehouse has 2 unloading bays and 5 workers.
   This class defines the behavior of small trucks.
   
   (C) J. Garrido, September 2012, updated Nov. 2016.
   Department of Computer Science, Kennesaw State University
   File:    Struck.osl
*/
 public  class Mtruck  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private int  truckNum; 
  // Truck number 
 private double  arrivalTime; 
  // arrival time  of truck 
 // truck unloading interval 
 private double  service_dur; 
  // 
 public Mtruck(  String  mtname, double  unload_dur) { 
super(mtname); 
truckNum =  Wareh.get_mnumArr();
arrivalTime = StaticSync.get_clock();
service_dur =  unload_dur;
 // unloading interval 
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
 double  msojourn; 
  // truck total interval in warehouse 
 double  simclock; 
  String  pname; 
  // start wait time  
 start_wait = StaticSync.get_clock();
 // 
pname =  get_name();
 // display pname, " arrives, requesting unloading bay at ", start_wait 
 // process will wait for an unload bay  
tracedisp(pname+ " arrives, requesting unloading bay");
 // 
 // 1 unloading bay 
 Wareh.bays.acquire(1);
 // 
 simclock = StaticSync.get_clock();
 // display pname, " acquired unloading bay at: ", simclock 
 // 
tracedisp(pname+ " acquired unloading bay");
 // display pname, " requesting 3 workers at ", simclock 
 // 
 // process will wait until 3 workers become available 
tracedisp(pname+ " requesting 3 workers at ");
 // 
 // 3 workers 
 Wareh.workers.acquire(3);
 // 
 simclock = StaticSync.get_clock();
 // display pname, " acquired workers at: ", simclock 
 // 
tracedisp(pname+ " acquired workers at: ");
 // calculate wait interval 
wait_p =  (get_clock()) - (start_wait);
 // accumulate truck wait interval 
 // 
Wareh.accumMWait(wait_p); 
 // now the unloading service of the truck 
tracedisp(pname+ " starts to unload");
 // unloading interval 
 // 
 // service completed, release resources 
  delay(service_dur);
 // 
 Wareh.workers.release(3);
 // 
 Wareh.bays.release(1);
 // increment number of small trucks serviced 
 // 
Wareh.incr_medTserv(); 
 // total time in warehouse: sSojourn 
msojourn =  (get_clock()) - (arrivalTime);
 // accumulate sojourn time 
Wareh.accum_Msojourn(msojourn); 
 // 
 simclock = StaticSync.get_clock();
 // display pname, " departs at ", simclock 
tracedisp(pname+ " finished unloading, departs");
  terminate();
 }  // end Main_body 
} // end class/interf Struck 
