
// OOSimL v 1.1 File: Comchan.java, Sun Dec 28 16:55:00 2014
 
import java.util.*; 
import psimjava.*; 
/** 
description
   Synchronous communication (direct comm)
    J. Garrido

 Process synchronous communication in OOSimL
 Sender process directly communicates with a receiver process
 to transfer a message through a channel.
 This model uses an array of sender and array of receiver processes.
 A waitq object for the rendezvous represents a channel for
 each pair of processes.

  File: Comchan.osl
  This class defines a Communication channel
*/
 public  class Comchan     {
static Scanner scan = new Scanner (System.in);
 private String  chname; 
  // channel name 
 private double  commint; 
  // current comm interval 
 // 
 // communication channel 
  private Waitq synchan; 
 // 
  private Sender send_proc; 
 // process sending message 
  private Receiver rec_proc; 
 // process to receive message 
 // message 
  private Message mess; 
 // random generator for communication interval 
  private Normal gencommint; 
 public Comchan(  String  lname, double  meancomm, double  stdcomm) { 
chname =  lname;
 // the actual communication channel 
synchan = new Waitq("syn" + chname, 1);
gencommint = new Normal("Comm int " + chname, meancomm, stdcomm);
 } // end initializer 
 //   
 public Waitq get_chan(   ) { 
return synchan; 
 }  // end get_chan 
 //   
 public void  send(  Message msg ) { 
 // set send_proc = call mess.get_sender 
 // set rec_proc = call mess.get_receiver 
 // call comch.qwait        // wait to communicate 
 // wait for master in comch   // wait to comm with master process 
mess =  msg;
 // display "Chan: ", chname, " data sent in mess: ", mess.get_data() 
 // 
 }  // end send 
 // return communication interval 
 public double  get_commint(   ) { 
return commint; 
 }  // end get_commint 
 // 
 public Message receive(   ) { 
  Sender sendpr; 
 // communicate with slave process  
 // and receive message 
 // set recpr = call mess.get_receiver 
 // 
 // generate random communication interval 
 commint = gencommint.fdraw(); 
 //  
 if ( mess ==  null ) { 
System.out.println("Chan "+ chname+ " mess is null");
 } // endif 
return mess; 
 }  // end receive 
 // 
 public String  get_chname(   ) { 
return chname; 
 }  // end get_chname 
 // 
 public void  set_sender(  Sender sproc ) { 
send_proc =  sproc;
 }  // end set_sender 
 // 
 public void  set_rec(  Receiver rproc ) { 
rec_proc =  rproc;
 }  // end set_rec 
} // end class/interf Comchan 
