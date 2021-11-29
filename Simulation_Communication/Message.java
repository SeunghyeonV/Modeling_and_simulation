
// OOSimL v 1.1 File: Message.java, Sun Dec 28 16:54:47 2014
 
import java.util.*; 
import psimjava.*; 
/** 
description
   Synchronous communication (direct comm)
    J. Garrido, September 2008

  Synchronous communication with OOSimL
  A sender process directly communicates with a receiver process
  to transfer a message through a channel.
  This model uses an array of sender and array of receiver processes.
  A waitq object for the rendezvous represents a channel for
  each pair of processes.

  File: Message.osl
*/
 public  class Message     {
static Scanner scan = new Scanner (System.in);
 private  static int  seq_num = 0; 
  // message number 
 private int  mess_num; 
  private double  time_stamp; 
  private String  mes_data; 
  // data contents of message 
  private Sender send_proc; 
 // process sending message 
 // process to receive message  
  private Receiver rec_proc; 
 // 
 public Message(  String  ldata) { 
mes_data =  ldata;
mess_num =  seq_num;
 seq_num++;
 // message number 
 } // end initializer 
 // 
 //get message number 
 public int  get_messnum(   ) { 
return mess_num; 
 }  // end get_messnum 
 // 
 public Receiver get_receiver(   ) { 
return rec_proc; 
 }  // end get_receiver 
 //  
 public Sender get_sender(   ) { 
return send_proc; 
 }  // end get_sender 
 // 
 public String  get_data(   ) { 
return mes_data; 
 // 
 }  // end get_data 
 // set receiver of message 
 public void  set_rec(  Receiver recpr ) { 
rec_proc =  recpr;
 // 
 }  // end set_rec 
 // set sender of message 
 public void  set_sender(  Sender sendpr ) { 
send_proc =  sendpr;
 // 
 }  // end set_sender 
 // set time stamp 
 public void  set_time(  double  ltime ) { 
time_stamp =  ltime;
 }  // end set_time 
 //    
} // end class/interf Message 
