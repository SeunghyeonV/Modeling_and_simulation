// OOSimL v 1.1 File: Receiver.java, Sun Dec 28 17:17:24 2014
 
import java.util.*; 
import java.io.*; 
import psimjava.*; 
/** 
description
   Synchronous communication (direct comm)
    J. Garrido. Updated Aug. 2012

 IPC synchronous communication
 Sender process directly communicates with a receiver process
 to transfer a message through a channel.
 This model uses an array of sender and array of receiver processes.
 A waitq object for the rendezvous represents a channel for
 each pair of processes.

File: Receiver.osl
*/
 public  class Receiver  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private double  mean_cons; 
  // mean interval to consume message 
 private int  k; 
  // receiver number 
 private double  rec_wait; 
  // total wait period 
 private double  startw; 
  // time start waiting 
 private double  wait_int; 
  // wait time interval 
 private String  rdata; 
  // data in message received 
 private double  simclock; 
 
 private String rname;
 // name of receiver process 
   private Message rmsg; 
 // message from sender 
  private NegExp consume; 
 // random gen for cons int 
 // sender of message 
 // 
 //       implementation of Receiver 
  private Sender sender_proc; 
 //    
 public Receiver(String  rname, int  rnum, double  mean_cons) { 
super(rname); 
 // receiver number 
k =  rnum;
 // random period to use message 
consume = new NegExp("Consume interval" + k, mean_cons);
rec_wait =  0.0;
System.out.println(rname+ " created");
 } // end initializer 
 // 
 public double  get_wait(   ) { 
return rec_wait; 
 // 
 }  // end get_wait 
 //     
 public void  Main_body(   ) { 
 double  consume_per; 
  int  msg_index; 
  String  recname; 
  String  sendname; 
  String  channame; 
   Comchan lchannel; 
lchannel =  Scomm.channel [k];
 // ref to channel 
rname = get_name();
 simclock = StaticSync.get_clock();
recname =  get_name();
channame =  lchannel.get_chname();
 while ( simclock < Scomm.simperiod ) { 
System.out.println(recname+ " attempting comm via "+ channame+ " at: "+ simclock);
 // 
 // wait to communicate with receiver 
tracedisp(recname+ " attempting comm via "+ channame);
 //  and get message from channel 
receive(lchannel); 
 //  
 simclock = StaticSync.get_clock();
System.out.println(recname+ " received message "+ rmsg.get_messnum()+ " via channel "+ channame+ " at "+simclock);
 // 
tracedisp(recname+ " received message "+ rmsg.get_messnum()+ " via channel "+ channame);
 // get rdata from message 
 // data from message        
 // 
 // accumulate wait interval 
rdata =  rmsg.get_data();
 // 
rec_wait =  (rec_wait) + (wait_int);
 //   
sendname =  sender_proc.get_name();
System.out.println(recname+ " received "+ rdata+ " from: "+ sendname+ " at "+ simclock);
tracedisp(recname+ " received "+ rdata+ " from: "+ sendname);
 // 
 consume_per = consume.fdraw(); 
 // wait for time to consume item 
  delay(consume_per);
 // 
 simclock = StaticSync.get_clock();
 } // endwhile 
System.out.println(recname+ " terminating");
 deactivate(this);
 //  terminate in main class 
 // 
 }  // end Main_body 
 // wait to communicate with sender process and receive message 
 public void  receive(  Comchan pchannel ) { 
 double  comm_int; 
  // comm interval 
  Waitq synchan; 
 // display get_name(), " to receive from chan: ", pchannel.get_chname() 
synchan =  pchannel.get_chan();
 // start wait time 
 // 
 // display get_name(), " comm via synchan: ", synchan.get_name() 
startw =  simclock;
 // 
 // 
sender_proc = (Sender) synchan.coopt();
 // display get_name(), " communicated via chan: ", pchannel.get_chname() 
 simclock = StaticSync.get_clock();
 // 
wait_int =  (simclock) - (startw);
 // get communication interval from channel 
comm_int =  pchannel.get_commint();
 // communication interval 
 // 
  delay(comm_int);
 // get message 
rmsg =  pchannel.receive();
 // 
 if ( rmsg ==  null ) { 
System.out.println(get_name()+ " rmsg is null");
 } // endif 
 // 
 reactivate(sender_proc);
 }  // end receive 
} // end class/interf Receiver 
