// OOSimL v 1.1 File: Mqcarwash.java, Wed Feb 27 22:34:45 2019

import java.util.*; 
import java.io.*; 
import java.text.DecimalFormat; 
import psimjava.*; 
/** 
 description
 A multi-server multi-queue model of a Car-wash system
 Arriving cars (customers) join the shortest queue to wait 
 for service. Each server is a car-wash machine that 
 services one car at a time. Service consists of a 
 complete external wash of a car.
 
 OOSimL model J. Garrido. Updated Nov 12, 2016
 Main class: Mqcarwash
 
 This class sets up simulation parameters and creates the
 relevant objects.
 There are four types of active objects of classes: 
 Mqcarwash, Server, Arrivals, and Car.
 There are several passive objects, the car_queues of class 
 Squeue, one queue for every server.
 The arrivals object creates the various car objects that 
 arrive at random intervals. 
 Random variables: inter-arrival period of a negative 
 exponential distribution and service period of a 
 Normal distribution.
 
 File: Mqcarwash.osl
 */
public  class Mqcarwash  extends psimjava.Process     {
  static Scanner scan = new Scanner (System.in);
  private  static boolean  gui_flag; 
  // files for reporting 
  private  static PrintWriter statf; 
  // statistics 
  private  static PrintWriter tracef; 
  // trace 
  final public  static int  cartype = 4; 
  // number of servers 
  public  static int  num_serv = 4; 
  // input car queue size 
  //  
  public  static int  totalQSize = 300; 
  // simulation period    
  // 
  public  static double  simPeriod = 2200.0; 
  // time to close arrival of cars 
  public  static double  close_arrival = 1150.25; 
  // 
  public  static String  simname = "Multiserver Multi-queue Carwash with priorities"; 
  // mean car inter-arrival interval 
  // 
  // car service interval  
  public  static double  MeanInterarrival[]; 
  // Uniform distribution  
  public  static double  minServiceDur[]; 
  
// Accumulative variables 
  public static double  maxServiceDur[]; 
  // accumulated customer service time 
  public  static double  custServiceTime[]; 
  // accumulated customer sojourn time 
  public  static double  custSojournTime[]; 
  // accumulated car waiting time   
  //  
  public  static double  custWaitTime[]; 
  // accum server idle time 
  // 
  public  static double  accum_serv[]; 
  // current number of customers serviced 
  public  static int  num_serviced[]; 
  // current number of cars that arrived  
  public  static int  num_arrived[]; 
  // current number of cars rejected 
  public  static int  num_rejected[]; 
  
  public  static double  accum_idle = 0.0; 
  // accumulated waiting time 
  public  static double  totalWait = 0.0; 
  public  static double  totalSojourn = 0.0; 
  public  static int  numcar; 
  public  static int  carServiced; 
  // Simulation model essential objects 
  // 
  // Queues for the model 
  // 
  public  static Simulation sim; 
  // array of simple queues for arriving cars 
  //  
  // References for the active objects of the model 
  public  static Squeue car_queue[]; 
  // 
  public  static Server wash_machine[]; 
  public  static Arrivals genCust[]; 
  // define model of class SimModel 
  public  static Mqcarwash shop; 
  /** 
   description
   This is the constructor for the main class
   */
  //  
  public Mqcarwash(  String ss) { 
    super(ss); 
  } // end initializer 
  // 
  public static void main(String[] args) { 
    int  i; 
    gui_flag =   false ;
    MeanInterarrival = new double  [cartype];
    minServiceDur = new double  [cartype];
    maxServiceDur = new double  [cartype];
    custServiceTime = new double  [cartype];
    custSojournTime = new double  [cartype];
    custWaitTime = new double  [cartype];
    num_serviced = new int  [cartype];
    num_arrived = new int  [cartype];             
    num_rejected = new int  [cartype];
    
    MeanInterarrival [0] =  5.2;
    minServiceDur [0] =  4.6;
    maxServiceDur [0] =  7.85;
    MeanInterarrival [1] =  6.2;
    minServiceDur [1] =  3.6;
    maxServiceDur [1] =  6.1;
    MeanInterarrival [2] =  3.2;
    minServiceDur [2] =  4.3;
    maxServiceDur [2] =  9.3;
    MeanInterarrival [3] =  7.2;
    minServiceDur [3] =  6.6;
    maxServiceDur [3] =  11.8;
    
    for (i = 0 ; i <= (cartype) - (1); i++) { 
      custServiceTime [i] =  0.0;
      custSojournTime [i] =  0.0;
      custWaitTime [i] =  0.0;
      num_serviced [i] =  0;
      num_arrived [i] =  0;
      num_rejected [i] =  0;
      // 
    }
    // set-up simulation with an object of class simulation 
    // 
    sim = new Simulation( "Multi-server multi-queue Car-Wash Model" );
    // setup files for reporting 
    Simulation.set_tracef("mqcarwtrace.txt");
    tracef = Simulation.get_tracefile();
    // 
    Simulation.set_statf("mqcarwstatf.txt");
    statf = Simulation.get_statfile();
    // create array of queues (passive objects) 
    car_queue = new Squeue [num_serv];
    // create each queue 
    i =  0;
    while ( i < num_serv ) { 
      car_queue [i] = new Squeue("Cust Queue" + i, totalQSize);
      i++;
      // 
    } // endwhile 
    // Create and start the main active objects of the model 
    shop = new Mqcarwash(simname);
    // "Multi-server multi-queue Car Wash System" 
    shop.start();
  }  // end main 
  // 
  public void  Main_body(   ) { 
    int  i; 
    //String  pname; 
    double  machutil; 
    // formatting strings 
    String  fmtout1; 
    String  fmtout2; 
    String  fmtout3[]; 
    String  dispouta; 
    String  dispoutb; 
    // 
    DecimalFormat dfmt; 
    statf = Simulation.get_statfile(); 
    // 
    accum_serv = new double  [num_serv];
    // 
    wash_machine = new Server [num_serv];
    // 
    // 
    // create the other active objects of the model 
    fmtout3 = new String  [num_serv];
    //  
    System.out.println("Number of servers: "+ num_serv);
    i =  0;
    while ( i < num_serv ) { 
      accum_serv [i] =  0.0;
      // added -03-19-2012 
      wash_machine [i] = new Server("Wash_machine" + i, i);
      wash_machine [i].start();
      i++;
    } // endwhile 
    
    genCust = new Arrivals[cartype];
    for (i = 0 ; i <= (cartype) - (1); i++) { 
      genCust[i] = new Arrivals("Arrivals" + i, i, MeanInterarrival[i], minServiceDur[i], maxServiceDur[i]);
      genCust[i].start();
      // 
      // display "Workload - car mean inter-arrival: ",  
      //      MeanInterarrival, " mean service: ", maxServiceDur, minServiceDur 
      // 
    } //endfor
    // starting simulation 
    // 
    sim.start_sim( simPeriod );
    // set formatting to round output to 3 dec places 
    
    dfmt = new DecimalFormat("0.###");
    for (i = 0 ; i <= (cartype) - (1); i++) { 
      System.out.println("Total cars of type " + i + " serviced: "+ num_serviced[i]);
      statf.println("Total cars of type " + i + " serviced: "+ num_serviced[i]);
      statf.flush();
    } //endfor
    
    for (i = 0 ; i <= (cartype) - (1); i++) { 
      if ( num_serviced[i] > 0) { 
        statf.println("Car type "+ i+ " average wait period: "+ dfmt.format((custWaitTime [i])/(num_serviced [i])));
        statf.flush();
        System.out.println("Car type "+ i+ " average wait period: "+ dfmt.format((custWaitTime [i])/(num_serviced [i])));
      } //endif
    } //endfor
    // dfmt = new DecimalFormat("0.###");
    
    i =  0;
    while ( i < num_serv ) { 
      machutil =  (accum_serv [i])/(simPeriod);
      fmtout3 [i] =  dfmt.format(machutil);
      // 
      statf.println("Machine" + i + " utilization: "+ fmtout3 [i]);
      statf.flush();
      System.out.println("Machine" + i + " utilization: "+ fmtout3 [i]);
      i++;
    }
    
    statf.println(" ");
    statf.flush();
    if ( carServiced > 0) { 
      System.out.println("Total number of cars serviced: "+ carServiced);
      statf.println("Total number of cars serviced: "+ carServiced);
      statf.flush();
      
      fmtout1 =  dfmt.format((totalWait)/(carServiced));
      System.out.println("Car average wait period: "+ fmtout1);
      statf.println("Total Car average wait period: "+ fmtout1);
      statf.flush();
      
      fmtout2 =  dfmt.format((totalSojourn)/(carServiced));
      System.out.println("Average time car spends in the shop: "+ fmtout2);
      statf.println("Average time car spends in the shop: "+ fmtout2);
      statf.flush();
    }
    
    // machine utilization 
    // endwhile 
    // 
    dispouta =  "Cars serviced: " + carServiced + "\n" + "Avg wait: " + dfmt.format((totalWait)/(carServiced)) + "\n";
    /*
     if guiflag == true
     then call model.println using dispoutb
     endif
     */
    dispoutb =  dispouta + "Mach util: " + dfmt.format((( (simPeriod) - (accum_idle)) )/(simPeriod)) 
      + "\n" + "Avg time in shop: " + dfmt.format((totalSojourn)/(carServiced));
    //System.exit(0); 
  }  // end Main_body 
} // end class/interf Mqcarwash 
