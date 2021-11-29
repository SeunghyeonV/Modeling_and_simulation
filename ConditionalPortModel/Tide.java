// OOSimL v 1.1 File: Tide.java, Sun Dec 28 20:25:51 2014
 
import java.util.*; 
import psimjava.*; 
/** 
description
OOSimL model of a Port System with conditional waiting
Ships arrive at a port and unload their cargo. Ships request
2 tugboats and a pier (harbor deck). To leave the ships request 1 tugboat.
The activities of a ship, ddocking, unloading, and leaving, 
have a finite period of duration.
If the resources are not available, ships have to wait.
Condition: Ships are only allowed to dock if 2 tugboats are
available and the tide is not low.
 
This class represents the behavior of the tide object, which changes
state from low tide to high tide and from high to low. 
The tide changes every 13 hours (time units). The low tide lasts
 for 4 time units. The tide is modeled as a process.
 
(C) J M Garrido, June 2011. Updated Nov. 2014. 
Dept of Computer Science, Kennesaw State University

Main class: Cport File: Tide.java
*/
 public  class Tide  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private boolean  lowtide; 
  // state of tide object 
 private String  tidename; 
  // name of tide object 
 public Tide(String  tidename) { 
super(tidename); 
lowtide =   true ;
this.tidename =  tidename;
System.out.println(tidename+ 
" created ");
 } // end initializer 
 // 
 public boolean  get_lowtide(   ) { 
return lowtide; 
 }  // end get_lowtide 
 // 
 public void  Main_body(   ) { 
 double  simclock; 
  double  lowtidedur; 
  double  hightidedur; 
  simclock = StaticSync.get_clock();
lowtidedur =  (4.0) * (60.0);
 // low tide duration in min 
hightidedur =  (9.0) * (60.0);
 while ( simclock <= Cport.simperiod ) { 
lowtide =   true ;
System.out.println(tidename+ " lowtide = true at: "+ simclock);
tracedisp(tidename+ " lowtide = true");
  delay(lowtidedur);
 // duration of low tide 
lowtide =   false ;
 simclock = StaticSync.get_clock();
System.out.println(tidename+ " lowtide = false at: "+ simclock);
tracedisp(tidename+ " lowtide = false");
  Cport.dockq.signal();
  delay(hightidedur);
 // duration of high tide 
 simclock = StaticSync.get_clock();
 } // endwhile 
  terminate();
 }  // end Main_body 
} // end class/interf Tide 
