// OOSimL v 1.1 File: Server.java, Wed Feb 27 22:34:27 2019
 
import java.util.*; 
import psimjava.*; 
/** 
description
   OOSimL model of the multi-server multi-queue car-wash system
   J Garrido. Updated Nov 24, 2016.
   

   This class defines the server objects, which represent
    is the car-wash machines. 
    A server object takes the car at the head of
   the queue and services the car. When the machine completes service
   of the car, it gets another car from the car queue
   If the car queue is empty, it joins the server queue and goes
   into its idle state.
*/
 public  class Server  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private int  snum; 
  // server number 
  private Car currentCustomer; 
 // current customer 
 private void  serviceCustomer (   ) { 
 double  startTime; 
  // time of start of service 
 double  service_per; 
  // service period for car  
 String  objname; 
  String  ccustname; 
  double  ccustservt; 
  // cust service time 
  int custpriority;
 double  simclock; 
  if ( !Mqcarwash.car_queue [snum].empty()) { 
 // get customer from head of waiting queue 
currentCustomer = ( Car) Mqcarwash.car_queue [snum].out();
 // 
custpriority =  currentCustomer.get_priority();
objname =  get_name();
ccustname =  currentCustomer.get_name();
 // get cust service time 
ccustservt =  currentCustomer.get_serv_dur();
 // service start time 
 // display objname, " begins service of: ", ccustname,  
 //    " at: ", startTime, " for ", ccustservt  
 startTime = StaticSync.get_clock();
 // 
 // 
tracedisp(objname+ " starts service of "+ ccustname + " type " + custpriority);
 // accumulate waiting time for this customer (car) 
 // add (startTime - currentCustomer.get_arrivalT())  
 //       to Mqcarwash.custWaitTime 
Mqcarwash.custWaitTime [custpriority] += ( (startTime) - (currentCustomer.get_arrivalT()));
Mqcarwash.totalWait += ( (startTime) - (currentCustomer.get_arrivalT())) ;
 // 
service_per =  currentCustomer.get_serv_dur();
  delay(service_per);
 // 
 simclock = StaticSync.get_clock();
 // display objname, " completes service of: ", ccustname,  
 Mqcarwash.custServiceTime [custpriority] += ( (simclock) - (startTime)) ;
 //     " at: ", simclock 
tracedisp(objname+ " completes service of "+ ccustname);
 reactivate(currentCustomer);
 // let car continue   
 Mqcarwash.accum_serv [snum] += service_per;
 }
 else {
return  ; 
 } // endif 
 }  // end serviceCustomer 
 //     
 public Server(  String  sname, int  servnum) { 
super(sname); 
snum =  servnum;
 // display sname, " created" 
 } // end initializer 
 // 
 public void  Main_body(   ) { 
 double  startIdle; 
  double  idle_period; 
  // idle period 
 double  simclock; 
  // simulation ime 
 String  sname; 
  // server name  
sname =  get_name();
 // display sname, " starting main body at ", simclock 
 simclock = StaticSync.get_clock();
 // 
 while ( simclock < Mqcarwash.simPeriod ) { 
 // display sname, " searching in queue ", snum, " for car" 
tracedisp(sname+ " searching in queue "+ snum+ " for car");
 if ( Mqcarwash.car_queue [snum].empty()) { 
 // car queue is empty 
 // starting time of idle period 
startIdle =  get_clock();
 // display sname, " goes idle at ", simclock 
tracedisp(sname+ " to idle state");
 // suspend server 
 // 
 // reactivated by a car object 
 deactivate(this);
 // queue must now be nonempty           
 // set idle_period = simclock - startIdle 
simclock =  get_clock();
idle_period =  (simclock) - (startIdle);
 Mqcarwash.accum_idle += idle_period;
 // display sname, " reactivated at " + simclock 
 } // endif 
serviceCustomer(); 
 // service the car 
 } // endwhile 
 // display "terminating ", sname 
tracedisp("terminating "+ sname);
  terminate();
 }  // end Main_body 
 // 
} // end class/interf Server 
