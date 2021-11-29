// OOSimL v 1.1 File: Car.java, Wed Feb 27 22:33:55 2019
 
import java.util.*; 
import psimjava.*; 
/** 
description
  The model of the multi-server multi-queue car-wash system
  OOSIMl version,  J Garrido, Updated Nov 2016

  This class defines behavior of car objects
  After a car object is created it joins the shortest queue to 
  wait for service. If it finds a server object idle,
   it reactivates the server object.
*/
 public  class Car  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
private int myprio;
//customer priority
 private int  customerNum[]; 
  // customer number 
 private double  arrivalTime; 
  // arrival time of customer 
 private double  service_dur; 
  // cust service interval 
 // Check Arrivals.java line 69. ctype = cprio
 public Car(String  cname, int cprio, double  servdur) { 
super(cname); 
customerNum =  Mqcarwash.num_arrived;
 arrivalTime = StaticSync.get_clock();
service_dur =  servdur;
 // display cname, " arrives at time ", arrivalTime     
 } // end initializer 

 public int  get_priority(   ) { 
return myprio; 
 }  // end get_priority 
 
 public double  get_arrivalT(   ) { 
return arrivalTime; 
 }  // end get_arrivalT 
 // 
 public double  get_serv_dur(   ) { 
return service_dur; 
 }  // end get_serv_dur 
 // 
 public void  Main_body(   ) { 
 double  simclock = StaticSync.get_clock();
  String  cname = get_name(); 
  double  sojourn; 
  String  servname; 
  int  mincust = 1500; 
  // number of cust in queue 
 int  cqueue = -(1); 
  // for selecting shortest queue 
 int  qlength; 
  int  j; 
   Server wmach; 
 myprio = this.get_prio(); 
 
 // display cname, " requests service at time ", simclock 
 tracedisp(cname+ " priority "+ myprio+ " arrives");
tracedisp(cname+ " requests service");
 // check for  the shortest queue 
 for (j = 0 ; j <= (Mqcarwash.num_serv) - (1); j++) { 
qlength = Mqcarwash.car_queue [j].length(); 
 // display "length of queue", j, " is: ", qlength 
 if ( !Mqcarwash.car_queue [j].full()) { 
 // queue not full 
qlength = Mqcarwash.car_queue [j].length(); 
 if ( qlength < mincust) { 
mincust =  qlength;
cqueue =  j;
 // shortest queue so far 
 } // endif 
 } // endif 
 } // endfor 
 // cqueue is index to shortest queue 
 if ( cqueue !=  - (1)) { 
 // display cname, " joins queue ", cqueue, " at ", simclock 
 // tracewrite cname, " joins queue ", cqueue 
 // join the shortest queue 
Mqcarwash.car_queue [cqueue].into(this );
 // display cname, " joined queue", cqueue 
 // 
tracedisp(cname+ " joined queue"+ cqueue);
 // now check if server idle  
 if ( Mqcarwash.wash_machine [cqueue].idle()) { 
servname =  Mqcarwash.wash_machine [cqueue].get_name();
 // display cname, " reactivating ", servname, " at ", simclock 
tracedisp(cname+ " reactivating "+ Mqcarwash.wash_machine [cqueue].get_name());
 reactivate(Mqcarwash.wash_machine [cqueue]);
 }
 else {
 // display "Machine", cqueue, " is active" 
 // 
 } // endif 
 // display cname, " to suspend" 
 // to wait for service 
 // 
 // display cname, " reactivated"         
 deactivate(this);
 // service completed, do final computation 
 // customers serviced 
 Mqcarwash.num_serviced[myprio]++;
 Mqcarwash.carServiced++;
 
 // total time in the system: custSojournTime 
 simclock = StaticSync.get_clock();
 sojourn =  ( (simclock) - (arrivalTime)) ;
 Mqcarwash.totalSojourn += sojourn;
 // display cname, " terminates at ", simclock 
  terminate();
 // terminates itself       
 }
 else {
 // queue full, abandon hope                                
 Mqcarwash.num_rejected[myprio]++;
 // display cname, " rejected, queue full " 
tracedisp(cname+ " rejected, all queues full");
  terminate();
 // terminate this object 
 } // endif 
 }  // end Main_body 
} // end class/interf Car 
