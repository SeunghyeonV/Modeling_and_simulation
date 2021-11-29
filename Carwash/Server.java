// OOSimL v 1.1 File: Server.java, Tue Jan 24 16:18:01 2017
 
import java.util.*; 
import psimjava.*; 
/** 
description
   OOSimL model of the car-wash system
   This class defines the server object, which
    is the car-wash machine. It takes the car at 
    the head of the queue and services the car. 
    When the machine completes service of the car,
    it gets another car from the queue.
    If the queue is empty, it goes into its 
    idle state (suspends itself)
    (C) J M Garrido, June 2011. Updated November 2016.
    Department of Computer Science, 
    College of Computing and Software Engineering,
    Kennesaw State University
    File: Server.osl 
*/
 public  class Server  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private int  numserviced = 0; 
  private double  cwait = 0; 
  // cust wait time  
 // current customer 
  private Car currentCustomer; 
 // 
 private void  serviceCustomer (   ) { 
 double  startTime; 
  // time of start of service 
 double  service_per; 
  // service period for car  
 String  servname; 
  String  ccustname; 
  double  ccustservt; 
  // cust service time 
 double  simclock; 
  if ( !Carwash.car_queue.empty()) { 
servname =  get_name();
 // get customer from head of waiting queue 
currentCustomer = ( Car) Carwash.car_queue.out();
ccustname =  currentCustomer.get_name();
 // get cust service time 
ccustservt =  currentCustomer.get_serv_dur();
 // service start time 
 // display servname, " starts service of: ", ccustname,  
 startTime = StaticSync.get_clock();
 //   " at: ", startTime, " for ", ccustservt  
tracedisp(servname+ 
" starts service of: "+ 
ccustname);
 // accumulate waiting time for this customer (car) 
cwait =  (startTime) - (currentCustomer.get_arrivalT());
Carwash.accum_custWaitTime(cwait); 
 // 
service_per =  currentCustomer.get_serv_dur();
  delay(service_per);
 // 
 // display servname, " completes service of: ", ccustname,  
 simclock = StaticSync.get_clock();
 //    " at: ", simclock 
tracedisp(servname+ 
" completes service of: "+ 
ccustname);
 reactivate(currentCustomer);
 // let car continue 
 numserviced++;
 }
 else {
return  ; 
 } // endif 
 }  // end serviceCustomer 
 //     
 public Server(  String  sername) { 
super(sername); 
 // display sername, " created at: ", simclock 
 } // end initializer 
 // 
 public void  Main_body(   ) { 
 double  startIdle; 
  double  idle_period; 
  // idle period 
 double  simclock; 
  // simulation time 
 String  cname; 
  // name attribute 
cname =  get_name();
 simclock = StaticSync.get_clock();
System.out.println(cname+ 
" starting main body at "+ 
simclock);
 // 
 while ( simclock <= Carwash.simPeriod ) { 
 if ( Carwash.car_queue.empty()) { 
 // queue is empty 
 // starting time of idle period 
startIdle =  get_clock();
 // display cname, " goes idle at ", startIdle 
tracedisp(cname+ 
" finds car queue empty");
 // suspend server 
 // 
 deactivate(this);
 // reactivated by a car object 
simclock =  get_clock();
idle_period =  (simclock) - (startIdle);
 Carwash.accum_idle += idle_period;
System.out.println(cname+ 
(" reactivated at ") + (simclock));
 } // endif 
serviceCustomer(); 
 // service the car 
 } // endwhile 
 // terminate self 
 }  // end Main_body 
 // 
 public int  get_serviced(   ) { 
return numserviced; 
 }  // end get_serviced 
 // 
} // end class/interf Server 
