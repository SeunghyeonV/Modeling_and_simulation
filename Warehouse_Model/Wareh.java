// OOSimL v 1.1 File: Wareh.java, Mon Mar  4 18:30:43 2019
 
import java.util.*; 
import java.io.*; 
import java.text.DecimalFormat; 
import psimjava.*; 
/** 
description
   An OOsimL model of a busy warehouse system
   Small and big trucks constantly arrive to a warehouse to unload
   goods. Each truck needs an unloading bay to dock and start
   unloading. Small trucks need two workers for unloading;; big
   trucks need three workers. Trucks may need to wait for an
   available unloading bay then wait for available workers.
   
   Both types of trucks have different mean arrival rates and
   different mean unloading periods.
   The warehouse has 2 unloading bays and 5 workers.

   This class sets up simulation parameters and creates the
   relevant objects (two active objects: arrivals for small and big
   trucks; two passive objects, the resource pool for unloading
   bays and the resource pool for workers.
   The arrivals object creates the various truck objects at random
   intervals. Random variables: inter-arrival period and unloading
   period for trucks.
 
   (C) J. Garrido, September 2012, updated Nov. 2016.
   Department of Computer Science, Kennesaw State University
   Main class: Wareh   File: Wareh.osl
*/
 public  class Wareh extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
  private  static PrintWriter statf; 
 // file for statistics 
  private  static PrintWriter tracef; 
 // file for trace 
 public  static int  NUM_WORKERS = 7; 
  // total workers  
 public  static int  NUM_BAYS = 3; 
  // total bays 
 public  static double  simPeriod = 825.65; 
  // simulation period 
 // close arrivals 
 public  static double  close_arrival = 645.5; 
  // 
 public static double mMeanInterarr = 7.5;
 // mean inter-arr medium trucks
 public  static double  bMeanInterarr = 13.2; 
  // mean inter-arr big trucks 
 public  static double  sMeanInterarr = 18.1; 
  // mean inter-arr small trucks 

 public  static double  mLMeanServ = 4.3; 
  // low service medium trucks 
 public  static double  mHMeanServ = 9.5; 
  // high service medium trucks 
 public  static double  bLMeanServ = 3.3; 
  // low service big trucks 
 public  static double  bHMeanServ = 7.2; 
  // high service big trucks 
 public  static double  sLMeanServ = 9.30; 
  // low service small trucks 
 public  static double  sHMeanServ = 28.35; 
  // high service small trucks  
 public  static double  bServ = 0.0; 
  // accum service big trucks 
 public  static double  mServ = 0.0; 
  // accum service medium trucks 
 public  static double  sServ = 0.0; 
  // accum service small trucks 
 public  static double  bSojourn = 0.0; 
  // accum sojourn big trucks
 public  static double  mSojourn = 0.0; 
  // accum sojourn medium trucks 
 public  static double  sSojourn = 0.0; 
  // accum sojourn small trucks 
 public  static double  bWait = 0.0; 
  // accum waiting big trucks 
public  static double  mWait = 0.0; 
  // accum waiting medium trucks 
 public  static double  sWait = 0.0; 
  // accum waiting small trucks 
 public  static int  bnum_serv = 0; 
  // num serviced big trucks
 public  static int  mnum_serv = 0; 
  // num serviced medium trucks
 public  static int  snum_serv = 0; 
  // num serviced small trucks 
 public  static int  bnum_arr = 0; 
  // num big trucks arrived so far
 public  static int  mnum_arr = 0; 
  // num medium trucks arrived so far 
 public  static int  snum_arr = 0; 
  // num small trucks arrived so far 
  public  static Simulation sim; 
  public  static PrintStream out; 
  public  static BArrivals genBtruck; 
 // arrivals object 
  public  static MArrivals genMtruck; 
 // arrivals object 
  public  static SArrivals genStruck; 
 // arrivals object 
  public  static Wareh warehobj; 
  public  static Res bays; 
 // unloading bays 
  public  static Res workers; 
 // unloading workers 
  
 public Wareh(String  pname) { 
super(pname); 
 } // end initializer 
 // 
 public static void main(String[] args) { 
out =  System.out;
 // set-up simulation 
 // 
 sim = new Simulation( "Busy Warehouse System" );
 // Set up report files 
 Simulation.set_tracef("warehtrace.txt");
tracef = Simulation.get_tracefile();
 // 
 Simulation.set_statf("warehstatf.txt");
statf = Simulation.get_statfile();
 // create passive objects 
workers = new Res("Workers", NUM_WORKERS);
 // passive obj  
bays = new Res("Bays", NUM_BAYS);
 // passive obj 

warehobj = new Wareh("Warehouse model");
 warehobj.start();
 // Create and start main object      
 }  // end main 
 
 // increment the number of big trucks that arrived 
 static  public void  incr_bigTArr(   ) { 
 bnum_arr++;
 // 
 }  // end incr_bigTArr 
 // get number of big trucks that arrived 
 static  public int  get_bnumArr(   ) { 
return bnum_arr; 
 // 
 }  // end get_bnumArr 
 // accumulate big truck wait intervals 
 static  public void  accumBWait(  double  waitP ) { 
 bWait += waitP;
 // 
 }  // end accumBWait 
 // increment the number of big trucks serviced 
 static  public void  incr_bigTserv(   ) { 
 bnum_serv++;
 // 
 }  // end incr_bigTserv 
 
 static  public void  incr_medTArr(   ) { 
 mnum_arr++;
 // 
 }  // end incr_medTArr 
 // get number of medium trucks that arrived 
 static  public int  get_mnumArr(   ) { 
return mnum_arr; 
 // 
 }  // end get_mnumArr 
 // accumulate medium truck wait intervals 
 static  public void  accumMWait(  double  waitP ) { 
 mWait += waitP;
 // 
 }  // end accumMWait 
 // increment the number of medium trucks serviced 
 static  public void  incr_medTserv(   ) { 
 mnum_serv++;
 // 
 }  // end incr_medTserv 
 
 // increment the number of small trucks that arrived 
 static  public void  incr_smallTArr(   ) { 
 snum_arr++;
 // 
 }  // end incr_smallTArr 
 // get number of small trucks that arrived 
 static  public int  get_snumArr(   ) { 
return snum_arr; 
 // 
 }  // end get_snumArr 
 // accumulate small truck wait intervals 
 static  public void  accumSWait(  double  waitP ) { 
 sWait += waitP;
 // 
 }  // end accumSWait 
 // increment the number of small trucks serviced 
 static  public void  incr_smallTserv(   ) { 
 snum_serv++;
 // 
 }  // end incr_smallTserv 
 
  // accumulate big truck sojourn time 
 static  public void  accum_Bsojourn(  double  bSn ) { 
 bSojourn += bSn;
 }  // end accum_Bsojourn 
 
 // accumulate medium truck sojourn time 
 static  public void  accum_Msojourn(  double  mSn ) { 
 mSojourn += mSn;
 }  // end accum_Msojourn 
 
 // accumulate small truck sojourn time 
 static  public void  accum_Ssojourn(  double  sSn ) { 
 sSojourn += sSn;
 }  // end accum_Ssojourn 
 //   
 public void  Main_body(   ) { 
  String  fmtout; 
  String  fmtout2; 
  String  fmtout3; 
  String  fmtout4; 
  String fmtout5;
  String fmtout6;
  // Round output to three decimal positions 
  DecimalFormat dfmt; 
statf =  Simulation.get_statfile();
tracef =  Simulation.get_tracefile();
dfmt = new DecimalFormat("0.###");
genBtruck = new BArrivals("Big T Arrivals ", bMeanInterarr, bLMeanServ, bHMeanServ);
genBtruck.start();
 // 
genMtruck = new MArrivals("Medium T Arrivals ", mMeanInterarr, mLMeanServ, mHMeanServ);
genMtruck.start();
 //
genStruck = new SArrivals("Small T Arrivals ", sMeanInterarr, sLMeanServ, sHMeanServ);
genStruck.start();
 // start simulation 
 sim.start_sim( simPeriod );
 // 
 System.out.println("Total medium trucks serviced: "+ mnum_serv);
statf.println("Total medium trucks serviced: "+ mnum_serv);
statf.flush();
fmtout3 =  dfmt.format((mWait)/(mnum_serv));
fmtout4 =  dfmt.format((mSojourn)/(mnum_serv));
System.out.println("medium truck average wait period: " + fmtout3);
statf.println("Medium truck average wait time: "+ fmtout3);
statf.flush();
System.out.println("Average time medium truck in warehouse: "+ fmtout4);
statf.println("Average time medium truck in warehouse: "+ fmtout4);
statf.flush();
System.out.println("Medium truck average wait time: "+ fmtout3);
 // 
System.out.println("Total big trucks serviced: "+ bnum_serv);
statf.println("Total big trucks serviced: "+ bnum_serv);
statf.flush();
fmtout =  dfmt.format((bWait)/(bnum_serv));
fmtout2 =  dfmt.format((bSojourn)/(bnum_serv));
System.out.println("Big truck average wait period: " + fmtout);
System.out.println("Average time big truck in warehouse: "+ fmtout2);
statf.println("Average time big truck in warehouse: "+ fmtout2);
statf.flush();
statf.println("Big truck average wait time: "+ fmtout);
statf.flush();
System.out.println("Big truck average wait time: "+ fmtout);
 // 
System.out.println("Total small trucks serviced: "+ snum_serv);
statf.println("Total small trucks serviced: "+ snum_serv);
statf.flush();
fmtout5 =  dfmt.format((sWait)/(snum_serv));
fmtout6 =  dfmt.format((sSojourn)/(snum_serv));
System.out.println("Small truck average wait period: "+ fmtout5);
System.out.println("Average time small truck in warehouse: "+ fmtout6);
statf.println("Average time small truck in warehouse: "+ fmtout6);
statf.flush();
statf.println("Small truck average wait time: "+ fmtout5);
statf.flush();
System.out.println("Small truck average wait time: "+ fmtout5);

//System.exit(0); 
 }  // end Main_body 
} // end class/interf Wareh 
