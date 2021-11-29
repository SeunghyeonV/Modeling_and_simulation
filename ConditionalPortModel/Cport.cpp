// OOSimL v1.1 File: Cport.cpp, Sun Apr 14 19:32:08 2019
 
/*
Port System with conditional waiting
   File: Cport.osl
Ships arrive at a port and unload their cargo. Ships request
2 tugboats and a pier (harbor deck). To leave the ships 
request 1 tugboat.
The activities of a ship, docking, unloading, and leaving, 
have a finite period of duration.
If the resources are not available, ships have to wait.

Condition: Ships are only allowed to dock if 2 tugboats are
available and the tide is not low. 
The tide changes every 13 hours. The low tide lasts
 for 4 hours. The tide is modeled as a process.

This system is similar to an operating system in which
processes have conditional waiting, using conditional
variables in monitors.

OOsimL model, J. M. Garrido. Updated Nov 12, 2016
Department of Computer Science
College of Computing and Software Engineering
Kennesaw State University

*/
#include <iomanip> 
#include "oosiml.h" 
 // simulation package 
#include "res.h" 
#include "condq.h" 
// forward references 
class Cport;
class Arrivals;
class Ship;
class BigShip;
class Tide;
// global declarations 
 static double  arr_mean; 
 static double	barr_mean;
  // ship mean inter-arrival    
 static double  unloadmean; 
 static double	bunloadmean;
  // ship mean unload int  
 static double  unloadstdev; 
 static double	bunloadstdev;
  // ship unload std 
 static double  simperiod; 
  // simulation per 
 static double  close_arrival; 
  // close time arr 
 static int  numarrivals = 0; 
 static int  bnumarrivals = 0;
  // number of ships 
 static double  acc_wait = 0.0; 
 static double	bacc_wait = 0.0;
  // accum wait interval 
 static double  acc_tport = 0.0;
 static double	bacc_tport = 0.0;
  // accum time in port 
 static double  avg_wait; 
 static double	bavg_wait;
  static double  avg_tport; 
  static double	bavg_tport;
  static Condq*  dockq; 
  // conditional wait 
 static Res*  tugs; 
  // tugboats 
 static Res*  piers; 
  // jetties 
 static Simulation*  run; 
  static Arrivals*  carrivals; 
  static Tide*  cur_tide; 
  static Cport*  port; 
 // specifications 
/** 
   description
   Main class: Cport
*/
  class Cport  : public Process    {
public: 
 Cport(string  portname );
/** 
 description
 function to evaluate condition
 there must be at least two tugboats available and
 the tide must not be low
 static function tugs_low_tide return type boolean is
 */
 bool  tugs_low_tide(  );
 bool  btugs_low_tide();
 // 
 void  Main_body(  ); 
}; // end class/struct Cport 
/** 
description
This class represents the behavior of the tide object, 
which changes state from low tide to high tide and 
from high to low. 
The tide changes every 13 hours (time units). The low tide
 lasts for 4 time units. The tide is modeled as a process.
*/
  class Tide  : public Process    {
private: 
bool  lowtide; 
  // state of tide object 
string  tidename; 
 public: 
 // name of tide object 
 Tide(string  tname );
 bool  get_lowtide(  ); 
 void  Main_body(  ); 
}; // end class/struct Tide 

  class Arrivals  : public Process    {
private: 
string  ss; 
 double  unloadmean;
 double	bunloadmean;
 double  unloadstd; 
 double	bunloadstd;

 NegExp*  next;
 NegExp*  bnext;
  // Random number generator 
 // Random number generator 
Normal*  unload; 
Normal*	bunload;
 public: 
 // 
 Arrivals(string  arrname, string barrname, double  arr_mean, double barr_mean, double  umean, double bumean, double  unlstd,
	 double bunlstd);
 void  Main_body(  ); 
}; // end class/struct Arrivals 
/** 
description
 Activity of a ship object:
 1. conditional wait for tugboats and low tide
 2. dock
 3. unload
 4. undock
 5. leave (terminate)

 Assumptions: 
 1. dock interval is constant
 2. undock interval takes about 65% of dock interval
*/

  class BigShip : public Process {
  private:
	  double  bstartw;
	  // ship start time of wait 
	  double  bstartp;
	  // ship start time in port 
	  double  bunload;
	  // unload interval 
	  double  simclock;
	  // current simulation time 
	  string  bshipname;
	  static Tide*  ctide;
  public:
	  // ship number 
	  // 
	  int  bshipnum;
	  //      Functions  of class Ship 
	  BigShip(string  bshname, double  bunloadper);
	  void  Main_body();
  }; // end class/struct Ship 
  // implementations 
   // 
   // 
  BigShip::BigShip(string  bshname, double  bunloadper) : Process(bshname) {
	  cout << bshname <<" unloadper: " <<bunloadper << endl;
	  bunload = bunloadper;
	  bshipnum = bnumarrivals;
	  // ship number 
	  simclock = get_clock();
	  cout << bshname <<" created at time: " <<simclock << endl;
	  bshipname = bshname;
  } // end initializer 
  // 
  void  BigShip::Main_body() {
	  double  bwait_per;
	  // wait interval for this ship 
	  double  btport_per;
	  double  bdoc_per;
	  // dock interval 
	  int  tugsavail;
	  // tug boats available 
	  bool  mcond = false;
	  // condition for tide 
	 // constant dock period 
	  bdoc_per = 30.5;
	  // 
	  simclock = get_clock();
	  bstartw = simclock;
	  // start time to wait 
	  bstartp = simclock;
	  // 
	  cout << bshipname << " requests pier at: " << simclock << endl;
	  // 
	  tracef << setprecision(3) << setw(10) << get_clock() << "  " << bshipname << " requests pier" << endl;
	  tracef.flush();
	  // attempt to acquire pier 
	  piers->acquire(1);
	  // 
	  simclock = get_clock();
	  cout << bshipname << " acquired pier at time: " << simclock << endl;
	  tracef << setprecision(3) << setw(10) << get_clock() << "  " << bshipname << " acquires pier" << endl;
	  tracef.flush();
	  // wait for high tide and 2 tugboats 
	  cout << bshipname << " waits until condition true at: " << simclock << endl;
	  tracef << setprecision(3) << setw(10) << get_clock() << "  " << bshipname << " waits until condition true" << endl;
	  tracef.flush();
	  //          
	  mcond = false;
	  while (mcond != true) {
		  mcond = port->btugs_low_tide();
		  // evaluate condition 
		  cout << bshipname << " Testing condition: " << mcond << " at: " << simclock << endl;
		  tracef << setprecision(3) << setw(10) << get_clock() << "  " << bshipname << " Testing condition: " << endl;
		  tracef.flush();
		  //    
		  dockq->waituntil(mcond);
		  // 
		  simclock = get_clock();
		  // 
		  // display shipname, " continues at: ", simclock 
		  // tracewrite shipname, " continues" 
		  // 
		  // wait for available tugboats 
		  // assign available units of tugs to tugsavail 
		  // display "Number of available tugboats: ", tugsavail 
	  } // endwhile 
	  // 
	  // attempt to acquire tugboats 
	  tugs->acquire(3);
	  // 
	  // display shipname, " acquires 2 tugboats at: ", simclock 
	  simclock = get_clock();
	  // 
	  bwait_per = (simclock)-(bstartw);
	  // wait interval 
	  bacc_wait += bwait_per;
	  // accumulate wait 
	  cout << bshipname << " wait interval: " << bwait_per << endl;
	  //  
	  tracef << setprecision(3) << setw(10) << get_clock() << "  " << bshipname << " wait interval; " << doublestr(bwait_per) << endl;
	  tracef.flush();
	  // ship now will dock 
	  // dock interval 
	  delay(bdoc_per);
	  // 
	  tugs->release(3);
	  // 
	  // display shipname, " releases tugs, signaling dockq at: ", simclock 
	  simclock = get_clock();
	  //    
	  // 
	  // ship will now unload 
	  // display shipname, " with unloading interval: ", unload 
	  dockq->signal();
	  // 
	  // take time to unload 
	  delay(bunload);
	  // 
	  simclock = get_clock();
	  cout << bshipname <<" requesting tugboat at: " << simclock << endl;
	  tracef << setprecision(3) << setw(10) << get_clock() << "  " << bshipname << " requesting tugboat" << endl;
	  tracef.flush();
	  //    
	  // start to wait again 
	  bstartw = simclock;
	  // 
	  tugs->acquire(1);
	  // 
	  simclock = get_clock();
	  // 
	  cout << bshipname << " acquires 1 tugboat at " << simclock << endl;
	  tracef << setprecision(3) << setw(10) << get_clock() << "  " << bshipname << " acquires tugboat" << endl;
	  tracef.flush();
	  // 
	  bwait_per = (simclock)-(bstartw);
	  // 
	  bacc_wait += bwait_per;
	  // undock 
	  // time ship takes to undock 
	  delay(((0.65) * (bdoc_per)));
	  // 
	  // release the tugboat 
	  tugs->release(1);
	  // 
	  simclock = get_clock();
	  cout << bshipname << " releases tugboat at: " << simclock << endl;
	  tracef << setprecision(3) << setw(10) << get_clock() << "  " << bshipname << " releases tugboat" << endl;
	  tracef.flush();
	  // 
	  // release the pier 
	  piers->release(1);
	  // 
	  // signal condition object 
	  dockq->signal();
	  // 
	  simclock = get_clock();
	  btport_per = (simclock)-(bstartp);
	  bacc_tport += btport_per;
	  // 
	  cout << bshipname << " time spent in port: " << btport_per << endl;
	  tracef << setprecision(3) << setw(10) << get_clock() << "  " << bshipname << " time spent in port: " << btport_per << endl;
	  tracef.flush();
	  cout << bshipname << " departing at: " << simclock << endl;
	  tracef << setprecision(3) << setw(10) << get_clock() << "  " << bshipname << " departing" << endl;
	  tracef.flush();
	  terminate();
	  // terminate this ship process 
  }  // end Main_body 



  class Ship  : public Process    {
private: 
double  startw; 
  // ship start time of wait 
double  startp; 
  // ship start time in port 
double  unload; 
  // unload interval 
double  simclock; 
  // current simulation time 
string  shipname; 
  static Tide*  ctide; 
 public: 
 // ship number 
 // 
int  shipnum; 
  //      Functions  of class Ship 
 Ship(string shname, double unloadper );
 void  Main_body(  ); 
}; // end class/struct Ship 
// implementations 
 // 
 // 
 Ship :: Ship(string  shname, double  unloadper ) : Process(shname) { 
 cout << shname << 
" unloadper: " << 
unloadper << endl;
unload = unloadper;
shipnum = numarrivals;
 // ship number 
 simclock = get_clock();
 cout << shname << 
" created at time: " << 
simclock << endl;
shipname = shname;
 } // end initializer 
 // 
 void  Ship ::Main_body(  ) { 
double  wait_per; 
  // wait interval for this ship 
double  tport_per; 
 double  doc_per; 
  // dock interval 
int  tugsavail; 
  // tug boats available 
bool  mcond =  false ; 
  // condition for tide 
 // constant dock period 
doc_per = 22.5;
 // 
 simclock = get_clock();
startw = simclock;
 // start time to wait 
startp = simclock;
 // 
 cout << shipname << " requests pier at: " << simclock << endl;
 // 
tracef << setprecision(3) << setw(10) << get_clock() << "  " << shipname << " requests pier" << endl;
tracef.flush();
 // attempt to acquire pier 
 piers->acquire(1);
 // 
simclock = get_clock();
 cout << shipname << " acquired pier at time: " << simclock << endl;
tracef << setprecision(3) << setw(10) << get_clock() << "  " << shipname << " acquires pier" << endl;
tracef.flush();
 // wait for high tide and 2 tugboats 
cout << shipname << " waits until condition true at: " << simclock << endl;
tracef << setprecision(3) << setw(10) << get_clock() << "  " << shipname << " waits until condition true" << endl;
tracef.flush();
 //          
mcond =  false ;
 while ( mcond !=  true  ) { 
mcond = port->tugs_low_tide();
 // evaluate condition 
 cout << shipname << " Testing condition: " << mcond << " at: " << simclock << endl;
tracef << setprecision(3) << setw(10) << get_clock() << "  " << shipname << " Testing condition: " << endl;
tracef.flush();
 //    
 dockq->waituntil(mcond);
 // 
 simclock = get_clock();
 // 
 // display shipname, " continues at: ", simclock 
 // tracewrite shipname, " continues" 
 // 
 // wait for available tugboats 
 // assign available units of tugs to tugsavail 
 // display "Number of available tugboats: ", tugsavail 
 } // endwhile 
 // 
 // attempt to acquire tugboats 
 tugs->acquire(2);
 // 
 // display shipname, " acquires 2 tugboats at: ", simclock 
simclock = get_clock();
 // 
wait_per = (simclock) - (startw);
 // wait interval 
 acc_wait += wait_per;
 // accumulate wait 
 cout << shipname << " wait interval: " << wait_per << endl;
 //  
tracef << setprecision(3) << setw(10) << get_clock() << "  " << shipname << " wait interval; " << doublestr(wait_per) << endl;
tracef.flush();
 // ship now will dock 
 // dock interval 
  delay(doc_per);
 // 
 tugs->release(2);
 // 
 // display shipname, " releases tugs, signaling dockq at: ", simclock 
simclock = get_clock();
 //    
 // 
 // ship will now unload 
 // display shipname, " with unloading interval: ", unload 
  dockq->signal();
 // 
 // take time to unload 
  delay(unload);
 // 
 simclock = get_clock();
 cout << shipname << 
" requesting tugboat at: " << simclock << endl;
tracef << setprecision(3) << setw(10) << get_clock() << "  " << shipname << " requesting tugboat" << endl;
tracef.flush();
 //    
 // start to wait again 
startw = simclock;
 // 
 tugs->acquire(1);
 // 
simclock = get_clock();
 // 
 cout << shipname << " acquires 1 tugboat at " << simclock << endl;
tracef << setprecision(3) << setw(10) << get_clock() << "  " << shipname << " acquires tugboat" << endl;
tracef.flush();
 // 
wait_per = (simclock) - (startw);
 // 
 acc_wait += wait_per;
 // undock 
 // time ship takes to undock 
  delay(( (0.65) * (doc_per)) );
 // 
 // release the tugboat 
 tugs->release(1);
 // 
simclock = get_clock();
 cout << shipname << " releases tugboat at: " << simclock << endl;
tracef << setprecision(3) << setw(10) << get_clock() << "  " << shipname << " releases tugboat" << endl;
tracef.flush();
 // 
 // release the pier 
 piers->release(1);
 // 
 // signal condition object 
  dockq->signal();
 // 
 simclock = get_clock();
tport_per = (simclock) - (startp);
 acc_tport += tport_per;
 // 
 cout << shipname << " time spent in port: " << tport_per << endl;
tracef << setprecision(3) << setw(10) << get_clock() << "  " << shipname << " time spent in port: " << tport_per << endl;
tracef.flush();
 cout << shipname << " departing at: " << simclock << endl;
tracef << setprecision(3) << setw(10) << get_clock() << "  " << shipname << " departing" << endl;
tracef.flush();
  terminate();
 // terminate this ship process 
 }  // end Main_body 
 // 

 Tide :: Tide(string  tname ) : Process(tname) { 
lowtide =  true ;
tidename = tname;
 cout << tidename << 
" created " << endl;
 } // end initializer 
 // 
 bool  Tide ::get_lowtide(  ) { 
return lowtide; 
 }  // end get_lowtide 
 // 
 void  Tide ::Main_body(  ) { 
double  simclock; 
 double  lowtidedur; 
 double  hightidedur; 
 double  simper; 
  simclock = get_clock();
lowtidedur = (4.0) * (60.0);
 // low tide duration in min 
hightidedur = (9.0) * (60.0);
simper = get_simper();
 while ( simclock <= simper ) { 
lowtide =  true ;
 cout << tidename << 
" lowtide = true at: " << 
simclock << endl;
tracef << setprecision(3) << setw(10) << get_clock() << "  " << tidename << 
" lowtide" << endl;
tracef.flush();
  delay(lowtidedur);
 // duration of low tide 
lowtide =  false ;
 simclock = get_clock();
 // display tidename, " lowtide = false at: ", simclock 
tracef << setprecision(3) << setw(10) << get_clock() << "  " << tidename << 
" hightide" << endl;
tracef.flush();
  dockq->signal();
  delay(hightidedur);
 // duration of high tide 
 simclock = get_clock();
 } // endwhile 
  terminate();
 }  // end Main_body 
 // 


 Arrivals :: Arrivals(string  arrname, string barrname, double  arr_mean, double barr_mean,
	 double  umean, double bumean, double unlstd, double bunlstd) : Process(arrname) { 
 // generator of random numbers for inter-arrival times  
 // Negative exponential probability distribution 
 cout << arrname << " arr_mean: " << arr_mean << " umean: " << umean << " unlstd: " << unlstd << endl;
 cout << barrname << "barr_mean: " << barr_mean << "bumean: " << umean << "bunlstd: " << bunlstd << endl;
next = new NegExp("Ship inter-arrival", arr_mean);
bnext = new NegExp("Big Ship inter-arrival", barr_mean);
 // 
unloadmean = umean;
bunloadmean = bumean;
 // unload mean interval (Normal dist)  
 // unload standard deviation  
unloadstd = unlstd;
bunloadstd = bunlstd;
 // random unload interval 
unload = new Normal("Ship Unload interval", unloadmean, unloadstd);
bunload = new Normal("Big Ship Unload interval", bunloadmean, bunloadstd);

 } // end initializer 
 // 
 void  Arrivals ::Main_body(  ) { 
double  simclock; 
 double  ttr; 
 double bttr;
  // inter-arrival interval 
double  unloadper; 
double  bunloadper;
  // unload period 

int  boatnum; 
int	bboatnum;

 string  boatnstr; 
 string  bboatnstr;
 string  arrname; 
 string  barrname;
 string  shipobjname; 
 string  bshipobjname;
 Ship*  lboat; 
 BigShip* blboat;
  simclock = get_clock();
 arrname = this->get_name(); 
 simclock = get_clock();
 barrname = this->get_name();

 while ( simclock <= close_arrival ) { 
 // generate random interarrival interval 
 ttr = next->fdraw(); 
 bttr = bnext->fdraw();
 cout << arrname << " Inter-arrival interval: " << ttr << endl;
 delay(ttr); // wait for ship arrival 
 numarrivals++; // increment ship counter 
 boatnum = numarrivals;
 unloadper = unload->fdraw();
 boatnstr = intstr(boatnum);
 shipobjname = "Ship" + boatnstr;
 lboat = new Ship(shipobjname, unloadper);
 lboat->pstart();
 simclock = get_clock();

 cout << barrname << " Inter-arrival interval: " << bttr << endl;
 delay(bttr);
 bnumarrivals++;
 bboatnum = bnumarrivals;
 bunloadper = bunload->fdraw();
 bboatnstr = intstr(bboatnum);
 bshipobjname = "Big Ship" + bboatnstr;
 blboat = new BigShip(bshipobjname, bunloadper);
 blboat->pstart();
 simclock = get_clock();

 // random unload interval 
 } // endwhile 
 cout << arrname << " terminates at: " << simclock << endl;
  terminate();

  while (simclock <= close_arrival) {
	  // generate random interarrival interval 
	  bttr = bnext->fdraw();
	  cout << barrname << " Inter-arrival interval: " << bttr << endl;
	  // 
	  delay(bttr);
	  // wait for ship arrival 
	  bnumarrivals++;
	  // increment ship counter 
	  // ship number 
	  // 
	  bboatnum = bnumarrivals;
	  // random unload interval 
	  bunloadper = bunload->fdraw();
	  bboatnstr = intstr(bboatnum);
	  bshipobjname = "Big Ship" + bboatnstr;
	  blboat = new BigShip(bshipobjname, bunloadper);
	  blboat->pstart();
	  simclock = get_clock();
  } // endwhile 
  cout << barrname << " terminates at: " << simclock << endl;
  terminate();
 }  // end Main_body 
 // 
 Cport :: Cport(string  portname ) : Process(portname) { 
 cout << "Initializing " << portname << endl;
 } // end initializer 
 // 
 // function to evaluate condition 
 // there must be atleast two tugboats available and 
 // the tide must not be low 
 bool  Cport ::tugs_low_tide(  ) { 
int  num_av; 
 bool  tide_stat; 
 num_av = tugs->num_avail();
tide_stat = cur_tide->get_lowtide();
 if ( (( num_av >= 2) ) && (( tide_stat !=  true ) )) { 
return  true ; 
 }
 else {
return  false ; 
 } // endif 
 }  // end tugs_low_tide 

 bool  Cport::btugs_low_tide() {
	 int  bnum_av;
	 bool  btide_stat;
	 bnum_av = tugs->num_avail();
	 btide_stat = cur_tide->get_lowtide();
	 if (((bnum_av >= 3)) && ((btide_stat != true))) {
		 return  true;
	 }
	 else {
		 return  false;
	 } // endif 
 }  // end tugs_low_tide 


 // 
 void  Cport ::Main_body(  ) { 
string  fmtout1; 
  cout << "Initiating Arrivals with mean inter-arrival: " << ((arr_mean + barr_mean)/2) << endl;
carrivals = new Arrivals("Arrivals ", "BArrivals", arr_mean, barr_mean, unloadmean, bunloadmean, unloadstdev, bunloadstdev);
 carrivals->pstart();
 // 
cur_tide = new Tide("Tide");
 cur_tide->pstart();
 // 
 cout << "Starting simulation ..." << endl;
 run->start_sim( simperiod );
 // 
avg_wait = (acc_wait + bacc_wait)/(numarrivals + bnumarrivals);
avg_tport = (acc_tport + bacc_tport)/(numarrivals + bnumarrivals);
statf << " " << endl;
statf.flush();
statf << "Total number of ships that arrived: " << (numarrivals + bnumarrivals) << endl;
statf.flush();
 cout << "Total number of ships that arrived: " << (numarrivals + bnumarrivals) << endl;
statf << "Average wait period: " << avg_wait << endl;
statf.flush();
 cout << "Average wait period: " << avg_wait << endl;
statf << "Average time in port: " << avg_tport << endl;
statf.flush();
 cout << "Average time in port: " << avg_tport << endl;
 }  // end Main_body 
 //  
 int main() {
 // setup files for reporting 
 Simulation::set_tracef("cporttrace.txt");
tracef.setf(ios::fixed);
tracef.setf(ios::showpoint);
 Simulation::set_statf("cportstatf.txt");
 run = new Simulation( "Port System with Conditions" );
 cout << "Starting the GUI module" << endl;
simperiod = 3760.5;
 // simulation period 
 // time to close ship arrivals 
close_arrival = 2073.5;
 // 
arr_mean = 46.45;
barr_mean = 56.45;
 // mean ships inter-arrival period  
unloadmean = 24.5;
bunloadmean = 30.5;
 // mean unload interval  
unloadstdev = 3.85;
bunloadstdev = 5.25;
 // unload standard dev 
 cout << "Simulation period: " << simperiod << endl;
statf << "Simulation period: " << simperiod << endl;
statf.flush();
 cout << "Close arrivals: " << close_arrival << endl;
statf << "Close arrivals: " << close_arrival << endl;
statf.flush();
 cout << "Mean inter-arrival: " << ((arr_mean + barr_mean) / 2) << endl;
statf << "Mean inter-arrival: " << ((arr_mean + barr_mean) / 2) << endl;
statf.flush();
 cout << "Mean unload interval: " << ((unloadmean + bunloadmean) / 2) << endl;
statf << "Mean unload interval: " << ((unloadmean + bunloadmean) / 2) << endl;
statf.flush();
 cout << "Unload STD: " << ((unloadstdev + bunloadstdev) / 2) << endl;
 // 
statf << "Unload STD: " << ((unloadstdev + bunloadstdev) / 2) << endl;
statf.flush();
 // create standard resource objects 
tugs = new Res("Tugs", 4);
 // 
piers = new Res("Piers", 2);
 // Conditional wait object 
 // 
dockq = new Condq("Dock cond queue");
 // main active object of model 
port = new Cport("Port with Conditions");
 port->pstart();
 run->end_sim();
 }  // end main 
