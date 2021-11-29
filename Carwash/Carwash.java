// OOSimL v 1.1 File: carwash.java, Mon Feb  4 09:18:04 2019
 
import java.util.*; 
import java.io.*; 
import java.text.DecimalFormat; 
import psimjava.*; 
/** 
  description
  OOSimL model: A single-server model of a Car-wash system
  arriving cars (customers) join a queue to wait for service.
  The server is a car-wash machine that services one car at
  a time. Service consists of a complete external wash of a car.
  There are four types of active objects of classes: Carwash, 
   Server, Arrivals, and Car.
  There is one passive object, the car_queue of class Squeue.
  The arrivals object creates the various car objects that 
  arrive at random intervals. 
  Random variables: inter-arrival period of a negative exponential
   distribution and service period of a Normal distribution.
  
  (C) J. Garrido. Jan 2012. Updated Nov. 2016.
  Department of Computer Science, 
  College of Computing and Software Engineering,
  Kennesaw State University
  Main class: Carwash
  File: Carwash.osl
  */
 public  class Carwash  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 // files for reporting 
  private  static PrintWriter statf; 
 // statistics 
  private  static PrintWriter tracef; 
 // trace 
 // car queue capacity  
 //  
 public  static int  totalQSize = 40; 
  // simulation period    
 // 
 public  static double  simPeriod = 1200.0; 
  // time to close arrival of cars 
 // 
 public  static double  close_arrival = 600.5; 
  // mean car inter-arrival interval 
 // 
 public  static double  MeanInterarrival = 3.75; 
  // mean car service interval  
 // 
 public  static double  MeanServiceDur = 11.25; 
  // standard deviation of service interval 
 // 
 // Accumulative variables 
 // 
 public double  StdServiceDur = 1.25; 
  // accumulated customer service time 
 public  static double  custServiceTime = 0.0; 
  // accumulated customer sojourn time 
 public  static double  custSojournTime = 10.0; 
  // accumulated car waiting time   
 public  static double  custWaitTime = 0.0; 
  // accum server idle time 
 public  static double  accum_idle = 0.0; 
  // current number of customers serviced 
 public  static int  num_serviced = 0; 
  // current number of cars that arrived  
 public  static int  num_arrived = 0; 
  // current number of cars rejected 
 public  static int  num_rejected = 0; 
  // 
  public  static Simulation sim; 
 // queue for arrived waiting cars 
 //  
  public  static Squeue car_queue; 
 // Classes for the active objects of the model 
  public  static Server wash_machine; 
  public  static Arrivals genCust; 
  public  static Carwash shop; 
 // 
 public static void main(String[] args) { 
 // set-up simulation  
 // 
 sim = new Simulation( "Simple Car-Wash System" );
 // setup files for reporting 
 Simulation.set_tracef("carwtrace.txt");
tracef = Simulation.get_tracefile();
 // 
 Simulation.set_statf("carwstatf.txt");
statf = Simulation.get_statfile();
 // create queue  
 // 
car_queue = new Squeue("Customer Queue", totalQSize);
 // Create and start the main active objects of the model 
shop = new Carwash("Car Wash Shop");
 shop.start();
 }  // end main 
 // 
 public Carwash(  String mnane) { 
super(mnane); 
 } // end initializer 
 // 
 public void  Main_body(   ) { 
 String  pname; 
  double  machutil; 
  // formatting strings 
 String  fmtout1; 
  String  fmtout2; 
  String  fmtout3; 
   DecimalFormat dfmt; 
statf =  Simulation.get_statfile();
wash_machine = new Server("Wash_machine");
genCust = new Arrivals("Arrivals ", MeanInterarrival, MeanServiceDur, StdServiceDur);
 genCust.start();
 // 
 // display "Workload - car mean inter-arrival: ",  
 //      MeanInterarrival, " mean service: ", MeanServiceDur 
 // 
 wash_machine.start();
 // starting simulation 
 // 
 sim.start_sim( simPeriod );
 // set formatting to 3 dec places 
dfmt = new DecimalFormat("0.###");
 // 
tracedisp("End of Simulation");
num_serviced =  wash_machine.get_serviced();
  wash_machine.terminate();
System.out.println("Total number of cars serviced: "+ 
num_serviced);
statf.println("Total number of cars serviced "+ 
num_serviced);
statf.flush();
 if ( num_serviced > 0) { 
fmtout1 =  dfmt.format((custWaitTime)/(num_serviced));
System.out.println("Car average wait time: "+ 
fmtout1);
statf.println("Car average wait time: "+ 
fmtout1);
statf.flush();
 // 
fmtout2 =  dfmt.format((custSojournTime)/(num_serviced));
System.out.println("Average period car spends in the shop: "+ 
fmtout2);
statf.println("Average period car spends in the shop: "+ 
fmtout2);
statf.flush();
 // 
 } // endif 
 // machine utilization 
machutil =  (( (simPeriod) - (accum_idle)) )/(simPeriod);
fmtout3 =  dfmt.format(machutil);
 // 
statf.println("Machine utilization: "+ 
fmtout3);
statf.flush();
System.out.println("Machine utilization: "+ 
fmtout3);
// System.exit(0); 
 }  // end Main_body 
 // 
 static  public void  set_num_arrived(  int  carrived ) { 
num_arrived =  carrived;
 // cars that have arrived 
 }  // end set_num_arrived 
 // 
 static  public void  incr_num_rejected(   ) { 
 num_rejected++;
 // cars rejected 
 }  // end incr_num_rejected 
 // 
 static  public void  accum_custWaitTime(  double  custwait ) { 
custWaitTime =  (custWaitTime) + (custwait);
 }  // end accum_custWaitTime 
 // 
 static  public void  accum_sojournTime(  double  custsojourn ) { 
custSojournTime =  (custSojournTime) + (custsojourn);
 }  // end accum_sojournTime 
} // end class/interf Carwash 
