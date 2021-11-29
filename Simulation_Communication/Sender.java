// OOSimL v 1.1 File: Sender.java, Sun Dec 28 17:12:52 2014
 
import java.util.*; 
import java.io.*; 
import psimjava.*; 
/** 
description
   Synchronous communication (direct comm)
    J. Garrido, September 2008

 Process synchronous communication with OOSimL
 Sender process directly communicates with a receiver process
 to transfer a message through a channel.
 This model uses an array of receiver processes.
 A waitq object for the rendezvous represents a channel for
 coomunication of sender and receiver processes.

 File: Sender.osl
*/
 public  class Sender  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private double  mean_prod; 
  // mean interval produce message 
 private int s;
 //sender number
 private int  idnum; 
  // sender id number 
 private double  wait_int; 
  // sender wait interval 
 private double  sender_wait; 
  // total sender wait  
 private double  startw; 
  // time of start waiting 
 private double  simclock; 
  // current sim time 
 private String sname; 
  // name of sender process 

   
  private Message smsg; 
 // message to send 
  private NegExp produc; 
 // random period produce message 
  private Receiver rec_proc; 
 // receiver object 
  private Comchan lchan; 
 // ref to channel object 

  
 public Sender(  String  sname, int snum, double  mean_prod) { 
 // random number generator for  
super(sname); 
s = snum;
 // interval to produce data 
produc = new NegExp("Produce data interval", mean_prod);
sender_wait =  0.0;
 //           
System.out.println(sname+ " created");
 } // end initializer 
 // 
 public double  get_wait(   ) { 
return sender_wait; 
 // 
 }  // end get_wait 
 // assemble a message with contents  
 public void  assemble(  String  contents, Receiver rec ) { 
smsg = new Message(contents);
smsg.set_sender( this ); 
 // sender 
smsg.set_rec(rec); 
 // receiver 
//smsg.get_data();

System.out.println("Message No. "+ smsg.get_messnum()+ " "+ smsg.get_data()+ " from "+ get_name()+ " to "+ rec.get_name());
tracedisp("Message No. "+ smsg.get_messnum()+ " "+ smsg.get_data()+ " from "+ get_name()+ " to "+ rec.get_name());
 // 

 }  // end assemble 
 // 
 public void  Main_body(   ) { 
 double  produce_per; 
  // interval produce data  
 int  j; 
   Comchan lchan; 
 // ref to channel 
sname =  get_name();
 simclock = StaticSync.get_clock();
 while ( simclock < (0.5) * (Scomm.simperiod) ) { 
 // display sname, " to generate msg at ", simclock 
 // 
 //  
 // complete setting up a channel for every receiver 
 for (j = 0 ; j <= (Scomm.NUM_RPROC) - (1); j++) { 
lchan =  Scomm.channel [j];
 // receiver object         
 // 
rec_proc =  Scomm.rec_obj [j];
 // assemble data for receiver process rec_proc 
 // 
assemble(Scomm.data [j], rec_proc); 
 // generate random interval to produce item 
 produce_per = produc.fdraw(); 
 // 
 // interval produce msg 
  delay(produce_per);
 // 
 simclock = StaticSync.get_clock();
 // start to wait 
startw =  simclock;
 // 
System.out.println(sname+ " wait to communicate via chan "+ lchan.get_chname()+ " at "+ simclock);
tracedisp(sname+ " wait to communicate via chan "+ lchan.get_chname());
 //                 
 // wait to send message 
send(lchan, smsg); 
 // 
 simclock = StaticSync.get_clock();
System.out.println(sname+ " sent message "+ smsg.get_messnum()+ " at "+ simclock);
tracedisp(sname+ " sent message "+ smsg.get_messnum());
 // 
sender_wait =  (sender_wait) + (wait_int);
 } // endfor 
 } // endwhile 
 // display sname, " terminating at ", simclock 
 deactivate(this);
 // terminate in main class 
 }  // end Main_body 
 // 
 public void  send(  Comchan mchan, Message lmsg ) { 
 double  comm_int; 
   Waitq synchan; 
 // 
 // display sname, " to comm via ", synchan.get_name() 
 // display sname, " sending msg with data: ", lmsg.get_data() 
 // 
synchan =  mchan.get_chan();
 // send message through the channel 
 // 
mchan.send(lmsg); 
 // get communication interval for channel 
comm_int =  mchan.get_commint();
 // display sname, " comm interval: ", comm_int, " chan: ", mchan.get_chname() 
  delay(comm_int);
 //       
 // 
startw =  simclock;
 // communicate with receiver 
 // display sname, " communicated chan: ", synchan.get_name() 
 synchan.qwait();
 // 
 simclock = StaticSync.get_clock();
 // wait interval 
wait_int =  (simclock) - (startw);
 // 
 }  // end send 
} // end class/interf Sender 
