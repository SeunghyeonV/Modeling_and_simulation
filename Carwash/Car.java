// OOSimL v 1.1 File: Car.java, Tue Jan 24 16:18:01 2017
 
import java.util.*; 
import psimjava.*; 
/** 
description
  OOSimL model of a car-wash system
  This class defines behavior of car objects
  After a car object is created it joins the queue to wait for
  service. It it finds the server object idle, 
  it reactivates the server object.
  (C) J Garrido. Jan 2012, updated Nov. 2014.
  File: Car.osl
*/
 public  class Car  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private int  customerNum; 
  // customer number 
 private double  arrivalTime; 
  // arrival time of customer 
 private double  service_dur; 
  // cust service interval 
 private double  sojourntime; 
  // 
 public Car(  String  cname, int  car_num, double  dur) { 
super(cname); 
customerNum =  car_num;
 arrivalTime = StaticSync.get_clock();
service_dur =  dur;
 // display cname, " arrives at time ", arrivalTime 
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
 double  simclock; 
  String  pname; 
  simclock = StaticSync.get_clock();
pname =  get_name();
 // check if there is still space available in the carwash shop 
 if ( !Carwash.car_queue.full()) { 
 // queue not full 
 // display pname, " joins queue at time ", simclock 
tracedisp(pname+ 
" joins car queue ");
Carwash.car_queue.into(this );
 // enqueue this car 
 if ( Carwash.wash_machine.idle()) { 
 // display "Activating server at time ", simclock 
 reactivate(Carwash.wash_machine);
 } // endif 
 // 
 // to wait for service         
 deactivate(this);
 // total time in the system: custSojournTime 
 simclock = StaticSync.get_clock();
sojourntime =  (simclock) - (arrivalTime);
Carwash.accum_sojournTime(sojourntime); 
 // display pname, " terminates at ", simclock 
  terminate();
 // terminates itself       
 }
 else {
 // queue full, abandon hope                                
Carwash.incr_num_rejected(); 
 // display pname, " rejected, queue full " 
tracedisp(pname+ 
" rejected, queue full ");
  terminate();
 // terminate this object 
 } // endif 
 }  // end Main_body 
} // end class/interf Car 
