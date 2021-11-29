// OOSimL v 1.1 File: Scomm.java, Sun Apr 14 21:40:11 2019

import java.util.*; 
import java.io.*; 
import psimjava.*; 
import java.text.*; 
import java.util.*; 
/** 
 description
 A OOSimL model of Synchronous Process Cooperation
 system with Synchronous Communication (direct comm)
 A single Sender process directly communicates with five receiver processes
 to transfer messages, each through a channel to every receiver.
 This model uses an array of receiver processes and 
 an array of communication channels.
 A waitq object for the rendezvous represents a channel for
 each Receiver process.
 (C) OOSimL model J. Garrido, August, 2011. Updated Nov.2014
 Department of Computer Science, Kennesaw State University.
 Main class: Scomm. File: Scomm.osl
 */
public  class Scomm  extends psimjava.Process     {
  static Scanner scan = new Scanner (System.in);
  private  static double  mean_prod = 13.25; 
  // mean interval to produce msg 
  private  static double  mean_cons = 42.75; 
  // mean interval to consume msg 
  private  static double  meancomm = 3.45; 
  // mean communication interval 
  private  static double  stdcomm = 0.75; 
  // STD communication interval 
  private  static Simulation run; 
  private  static Scomm comm_obj; 
  final public  static int  MSG_SIZE = 30; 
  // max size of message 
  final public  static int  NUM_RPROC = 5; 
  final public  static int  NUM_SPROC = 5;
  // number of receiver processes 
  // Data to send - an array of strings 
  public  static String  data[]; 
  public  static double  simperiod; 
  //   Channels 
  //   one channel for each receiver process 
  public  static Comchan channel[]; 
  // communication channels 
  public  static Sender send_obj[]; 
  // single sender process 
  // array of receiver processes 
  public  static Receiver rec_obj[]; 
  // files for reporting 
  public  static PrintWriter statf; 
  public  static PrintWriter tracef; 
  //   
  public  static Simulation sim; 
  // 
  public Scomm(String  ss) { 
    super(ss); 
  } // end initializer 
  // 
  // 
  
  
  
  public static void main(String[] args) { 
    // set-up simulation 
    // 
    sim = new Simulation("Synchronous communication with channels");
    // setup files for reporting 
    Simulation.set_tracef("scommtrace.txt");
    tracef = Simulation.get_tracefile();
    Simulation.set_statf("scommstatf.txt");
    statf = Simulation.get_statfile();
    // 
    // simulation period 
    // 
    // create arrays 
    simperiod =  2560;
    // 
    rec_obj = new Receiver [NUM_RPROC];
    send_obj = new Sender [NUM_SPROC];
    
    // 
    channel = new Comchan [NUM_RPROC];
    // data to be sent by every sender    
    // 
    data = new String  [NUM_RPROC];
    // text data as messages 
    data [0] =  "Computer Science";
    data [1] =  "College of Computing and Soft Engineering";
    data [2] =  "Kennesaw State University";
    data [3] =  "1000 Chastain Road"; 
    data [4] =  "Kennesaw, GA";
    // create the main process  
    comm_obj = new Scomm("Synchronous Communication");
    comm_obj.start();
  }  // end main 
  // 
  public void  Main_body(   ) { 
    double  sendwait; 
    String  sendname; 
    double  swait = 0.0; 
    String  fsendwait; 
    // 
    double  recwait; 
    double  rwait = 0.0; 
    String  recname; 
    String  frecwait; 
    String  fsenwait;
    int  j; 
    DecimalFormat fmt; 
    // 
    fmt = new DecimalFormat("0000.0000");
    // open reporting files 
    statf = Simulation.get_statfile(); 
    tracef = Simulation.get_tracefile(); 
    // 
    // 
    System.out.println("Input Parameters:");
    System.out.println("Simulation period: "+ simperiod);
    System.out.println("Receiver mean consume interval: "+ mean_cons);
    System.out.println("Sender mean produce interval: "+ mean_prod);
    System.out.println("Number of rec/sender processes: "+ NUM_RPROC);
    // 
    tracedisp("Input Parameters:");
    tracedisp("Simulation period: "+ simperiod);
    tracedisp("Receiver mean consume interval: "+ mean_cons);
    tracedisp("Sender mean produce interval: "+ mean_prod);
    // 
    tracedisp("Number of rec/sender processes: "+ NUM_RPROC);
    
    for (j = 0 ; j <= (NUM_RPROC) - (1); j++) { 
      // create channel 
      channel [j] = new Comchan("Channel" + j, meancomm, stdcomm);
      
// create and Sender objects 
      send_obj[j] = new Sender("Sender" + j, j, mean_prod);
      send_obj[j].start();
      
      // create and start Receiver objects 
      rec_obj [j] = new Receiver("Receiver" + j, j, mean_cons);
      rec_obj [j].start();
      // 
    } // endfor 
    // now start simulation 
    sim.start_sim( simperiod );
    //  
    
    // 
    for (j = 0 ; j <= (NUM_RPROC) - (1); j++) { 
      sendname = send_obj[j].get_name(); 
      sendwait =  send_obj[j].get_wait();
      fsendwait =  fmt.format(sendwait);
      
      System.out.println(sendname+ " wait interval: "+ sendwait);
      statf.println(sendname+ " wait interval: "+ fsendwait);
      statf.flush();
      // accumulate sender wait intervals 
      swait += sendwait;
      recname =  rec_obj [j].get_name();
      recwait =  rec_obj [j].get_wait();
      frecwait =  fmt.format(recwait);
      // 
      System.out.println(recname+ " wait interval: "+ recwait);
      statf.println(recname+ " wait interval: "+ frecwait);
      statf.flush();
      // accumulate receiver wait intervals 
      rwait += recwait;
      // 
      rec_obj [j].terminate();
      send_obj[j].terminate();
    } // endfor 
    
    // display "Average sender wait: ", swait / NUM_SPROC 
    // 
    System.out.println("Average receiver wait: "+ (rwait)/(NUM_RPROC));
    System.out.println("Average sender wait: " + (swait)/(NUM_SPROC));
    // set fsendwait = call fmt.format using swait / NUM_SPROC 
    // 
    frecwait =  fmt.format((rwait)/(NUM_RPROC));
    fsenwait = fmt.format((swait)/(NUM_SPROC));
    // statwrite "Average sender wait: ", fsendwait 
    statf.println("Average receiver wait: "+ frecwait);
        statf.println("Average receiver wait: "+ fsenwait);
    statf.flush();
//System.exit(1); 
  }  // end Main_body 
} // end class/interf Scomm 
