// OOSimL v1.1 File: Scomm.cpp, Wed Apr 17 23:11:46 2019
 
#include "oosiml.h" 
#include "queue.h" 
#include "bin.h" 
/* description
 A OOSimL model of Synchronous Process Cooperation
 system with Synchronous Communication (direct comm)
 A single Sender process directly communicates with five receiver processes
 to transfer messages, each through a channel to every receiver.
 This model uses an array of receiver processes and 
 an array of communication channels.
 
 (C) OOSimL model J. Garrido, Updated Dec 2016
  Department of Computer Science
  College of Computing and Software Engineering
  Kennesaw State University.
  File: Scomm.osl
*/
#include "waitq.h" 
// forward references 
class Scomm;
class Channel;
class Sender;
class Receiver;
class Message;
// global declarations 
const static int  MSG_SIZE = 30; 
  // max size of message 
const static int  NUM_RPROC = 5; 
const static int  NUM_SPROC = 5;
  // number of receiver processes 
 // Data to send - an array of strings 
 static string  data [NUM_RPROC]; 
  static double  simperiod; 
  // Channels 
 // one channel for each pair of receiver & sender processes 
Channel*  channel [NUM_RPROC]; 
  // communication channels 
 // single sender process 
Sender*  send_obj [NUM_SPROC]; 
  // array of receiver processes 
Receiver*  rec_obj [NUM_RPROC]; 
 Scomm*  comm_obj; 
  //   
 static Simulation*  sim; 
 // specifications 
  class Scomm  : public Process    {
private: 
const double Scomm::mean_prod = 13.25; 
  // mean interval to produce msg 
const  double Scomm::mean_cons = 42.75;
  // mean interval to consume msg 
const  double Scomm::meancomm = 3.45;
  // mean communication interval 
const  double Scomm::stdcomm = 0.75;
  // STD communication interval 
Scomm*  comm_obj; 
 public: 
 Scomm(string  ss );
 void  Main_body(  ); 
}; // end class/struct Scomm 
 // 
  class Sender  : public Process    {
private: 
double  mean_prod; 
  // mean interval produce message 
int  idnum; 
  // sender id number 
double  sender_wait; 
  // total sender wait  
double  startw; 
  // time of start waiting 
double  simclock; 
  // current sim time 
string  sname; 
  // name of sender process 
Message*  smsg; 
  // message to send 
NegExp*  produc; 
  // random period produce message 
Receiver*  rec_proc; 
  // receiver object 
Channel*  lchan; 
 public: 
 // ref to channel object    
 Sender(string  pname, double  mean_prod );
 void  assemble( string  contents, int  messnum ); 
 void  Main_body(  ); 
 double  get_wait(  ); 
}; // end class/struct Sender 
 // 
  class Receiver  : public Process    {
private: 
string  recname; 
 double  mean_cons; 
  // mean interval to consume message 
int  k; 
  // receiver number 
double  rec_wait; 
  // total wait period 
string  rdata; 
  // data in message received 
double  simclock; 
 Message*  rmsg; 
  // message from sender 
NegExp*  consume; 
  // random gen for consume int 
 // random gen for internal task 
 // 
 //       implementation of Receiver 
NegExp*  rtask; 
 public: 
 //    
 Receiver(string  rname, int  rnum, double  mean_cons );
 double  get_wait(  ); 
 void  Main_body(  ); 
}; // end class/struct Receiver 
 // 
  class Message    {
private: 
int  seq_num; 
  // message number 
int  mess_num; 
 double  time_stamp; 
 string  mes_data; 
 public: 
 // data contents of message 
 Message(string  ldata, int  lmessnum );
 int  get_messnum(  ); 
 string  get_data(  ); 
 // set time stamp 
 void  set_time( double  ltime ); 
}; // end class/struct Message 
 // 
  class Channel    {
private: 
string  chname; 
  // channel name 
double  comm_int; 
  // comm interval 
double  wait_int; 
  // wait interval 
Waitq*  synchan; 
  // communication link 
Process*  send_proc; 
  // process sending message 
Process*  rec_proc; 
  // process to receive message 
Message*  mess; 
  // message 
Normal*  gen_comm; 
 public: 
 // gen comm interval 
 Channel(string  lname, Process *  psend, Process *  prec, double  mean_comm, double  std_comm );
 void  send( Message *  msg ); 
 Message *  receive(  ); 
 string  get_chname(  ); 
 double  get_comm_int(  ); 
 double  get_wait(  ); 
 bool  rec_ready(  ); 
 bool  send_ready(  ); 
}; // end class/struct Channel 
// implementations 
 // 


 Sender :: Sender(string  pname, double  mean_prod ) : Process(pname) { 
 // random number generator for  
	 sname = pname;
 // interval to produce data 
produc = new NegExp("Produce data interval", mean_prod);
sender_wait = 0.0;
 // display sname, " created" 
cout << pname << " created" << endl;
 } // end initializer 
 // 
 // assemble a message with contents  
 void  Sender ::assemble( string  contents, int  pnum ) { 
	 smsg = new Message(contents, pnum);
 // display sname, " assembling Messg ", get_messnum() of smsg, 
	 cout << sname << " assembling Message " << smsg->get_messnum() << " " << smsg->get_data() << endl;
 //      " ", get_data() of smsg 
tracef << setprecision(3) << setw(10) << get_clock() << "  " << sname << 
" assembling Message " << 
smsg->get_messnum() << 
" " << 
smsg->get_data() << endl;
tracef.flush();
 }  // end assemble 
 // 
 void  Sender ::Main_body(  ) { 
double  wait_int; 
  // sender wait interval 
double  produce_per; 
  // interval produce data  
int  j; 
 int  mess_num; 
 double  close_send; 
 Channel*  lchan; 
  // ref to channel 
 simclock = get_clock();
 // stop sending messages at close_send 
close_send = (0.75) * (simperiod);
mess_num = 0;
 while ( simclock < close_send ) { 
 // complete setting up a channel for every receiver 
 for (j = 0 ; j <= (NUM_RPROC) - (1); j++) { 
 // 
lchan = channel [j];
 // assemble data for channel j 
 // 
assemble(::data [j], mess_num); 
 // generate random interval to produce item 
 produce_per = produc->fdraw(); 
 // 
 // interval to produce msg 
 // 
 // display sname, " waiting to communicate via chan ",  
 cout << sname << " waiting to communicate via chan " << lchan->get_chname() << endl;
  delay(produce_per);
 //    get_chname() of lchan, " at ", simclock 
tracef << setprecision(3) << setw(10) << get_clock() << "  " << sname << 
" waiting to communicate via chan " << lchan->get_chname() << endl;
tracef.flush();
 //  
 // send message smsg via channel lchan 
lchan->send(smsg); 
 // 
 simclock = get_clock();
wait_int = lchan->get_wait();
 // 
 // display sname, " sent message ", get_messnum() of smsg, 
cout << sname << " sent message " << smsg->get_messnum() << endl;
sender_wait = (sender_wait) + (wait_int);
 //     " at ", simclock 
tracef << setprecision(3) << setw(10) << get_clock() << "  " << sname << " sent message " << 
smsg->get_messnum() << endl;
tracef.flush();
 // 
  delay((0.1) * (produce_per));
 mess_num++;
 } // endfor 
 } // endwhile 
cout << sname << " terminating at " << simclock << endl;
tracef << setprecision(3) << setw(10) << get_clock() << "  " << sname << " terminating " << endl;
tracef.flush();
 deactivate();
 // terminate in main class 
 }  // end Main_body 
 // 
 double  Sender ::get_wait(  ) { 
return sender_wait; 
 }  // end get_wait 
 // 
 Receiver :: Receiver(string  rname, int  rnum, double  mean_cons ) : Process(rname) { 
recname = rname;
 // receiver number 
k = rnum;
 // random time interval to use message 
consume = new NegExp("Consume interval " + rname, mean_cons);
rtask = new NegExp("Rec task interval " + rname, mean_cons);
rec_wait = 0.0;
 // tracewrite rname, " created" 
tracef << rname << " created" << endl;
cout << rname << " created" << endl;
 } // end initializer 
 // 
 double  Receiver ::get_wait(  ) { 
return rec_wait; 
 }  // end get_wait 
 // 
 void  Receiver ::Main_body(  ) { 
double  wait_int; 
  // wait time interval 
double  rec_task; 
 double  consume_per; 
 int  msg_index; 
 string  channame; 
 Channel*  lchannel; 
 lchannel = channel [k];
 // ref channel 
 simclock = get_clock();
recname = get_name();
channame = lchannel->get_chname();
 while ( simclock < simperiod ) { 
 rec_task = rtask->fdraw(); 
 // time to perform internal task 
 // display recname, " attempting comm via ",  
 cout << recname << " attempting comm via " << channame << endl;
  delay(rec_task);
 //    channame, " at: ", simclock 
 // 
tracef << setprecision(3) << setw(10) << get_clock() << "  " << recname << " attempting comm via " << channame << endl;
tracef.flush();
 // wait to communicate and get message from channel 
 simclock = get_clock();
rmsg = lchannel->receive();
 // get rdata from message 
 // data from message        
 // display recname, " received message ", get_messnum() of rmsg, 
cout << recname << " received message " << rmsg->get_messnum() << " via " << channame << endl;
rdata = rmsg->get_data();
 //     " via ", channame, " at ", simclock 
tracef << setprecision(3) << setw(10) << get_clock() << "  " << recname << " received message " << rmsg->get_messnum() << 
" via " << 
channame << endl;
tracef.flush();
 // accumulate wait interval 
wait_int = lchannel->get_wait();
rec_wait = (rec_wait) + (wait_int);
 //   
 consume_per = consume->fdraw(); 
 // assign random value from consume to consume_per 
 // wait for time to consume item 
  delay(consume_per);
 // 
 simclock = get_clock();
 } // endwhile 
 cout << recname << 
" terminating" << endl;
 deactivate();
 //  terminate in main class 
 }  // end Main_body 
 // 
 Message :: Message(string  ldata, int  pnum ) { 
seq_num = 0;
mes_data = ldata;
mess_num = pnum;
 time_stamp = get_clock();
 } // end initializer 
 // 
 //get message number 
 int  Message ::get_messnum(  ) { 
return mess_num; 
 }  // end get_messnum 
 // 
 string  Message ::get_data(  ) { 
return mes_data; 
 // 
 }  // end get_data 
 // set time stamp 
 void  Message ::set_time( double  ltime ) { 
time_stamp = ltime;
 }  // end set_time 
 //    
 // 
 Channel :: Channel(string  lname, Process *  psend, Process *  prec, double  mean_comm, double  std_comm ) { 
chname = lname;
send_proc = psend;
rec_proc = prec;
 // the actual communication link 
synchan = new Waitq(lname + chname, 1);
gen_comm = new Normal("Comm interval " + chname, mean_comm, std_comm);
 // tracewrite chname, " created" 
 } // end initializer 
 //  
 // This functions returns status of channel for receiver process 
 bool  Channel ::rec_ready(  ) { 
int  slave_queue_len; 
 slave_queue_len = synchan->length(); 
 if ( slave_queue_len > 0) { 
return  true ; 
 }
 else {
return  false ; 
 } // endif 
 // 
 }  // end rec_ready 
 // This functions returns status of channel for sender process 
 bool  Channel ::send_ready(  ) { 
int  master_queue_len; 
 master_queue_len = synchan->lengthm(); 
 if ( master_queue_len > 0) { 
return  true ; 
 }
 else {
return  false ; 
 } // endif 
 }  // end send_ready 
 // 
 void  Channel ::send( Message *  msg ) { 
double  simclock; 
 double  startw; 
 Process*  psender; 
 mess = msg;
 if ( mess ==  NULL ) { 
 cout << "message to send is null" << endl;
 } // endif 
 simclock = get_clock();
startw = simclock;
 synchan->qwait();
 // communicate 
 simclock = get_clock();
wait_int = (simclock) - (startw);
 comm_int = gen_comm->fdraw(); 
 // display chname, " end send"
 cout << chname << " end send" << endl;
 }  // end send 
 // 
 Message *  Channel ::receive(  ) { 
double  startw; 
 double  comm_int; 
 double  simclock; 
 Process*  sender; 
  // display chname, " ready to receive message from ",  
 //       send_proc.get_name() 
 simclock = get_clock();
startw = simclock;
 // start time      
sender = (Process *) synchan->coopt();
 simclock = get_clock();
wait_int = (simclock) - (startw);
 if ( mess ==  NULL ) { 
 cout << "Chan " << 
chname << 
" mess is null" << endl;
 } // endif 
 comm_int = gen_comm->fdraw(); 
 if ( rec_proc != NULL ) 
     rec_proc->delay(comm_int);
 // display "end receive" 

 sender->reactivate();
return mess; 
 }  // end receive 
 // 
 string  Channel ::get_chname(  ) { 
return chname; 
 // 
 }  // end get_chname 
 // get the communication interval 
 double  Channel ::get_comm_int(  ) { 
return comm_int; 
 }  // end get_comm_int 
 // 
 double  Channel ::get_wait(  ) { 
return wait_int; 
 }  // end get_wait 
 // 
 Scomm :: Scomm(string  ss ) : Process(ss) { 
 } // end initializer 
 // 
 void  Scomm ::Main_body(  ) { 
double  sendwait; 
 string  sendname; 
 double  swait = 0.0; 
 string  fsendwait; 
  // 
double  recwait; 
 double  rwait = 0.0; 
 string  recname; 
 string  frecwait; 
 int  j; 
 string  senname;
 string  rname; 
 string  cname; 
 string  jstr; 
 Sender*	sobj;
 Receiver*  robj; 
  cout << "Input Parameters:" << endl;
 cout << "Simulation period: " << 
simperiod << endl;
 cout << "Receiver mean consume interval: " << 
mean_cons << endl;
 cout << "Sender mean produce interval: " << 
mean_prod << endl;
 cout << "Number of rec/sender processes: " << 
NUM_RPROC << endl;
 // 
statf << "Input Parameters:" << endl;
statf.flush();
statf << "Simulation period: " << 
simperiod << endl;
statf.flush();
statf << "Receiver mean consume interval: " << 
mean_cons << endl;
statf.flush();
statf << "Sender mean produce interval: " << 
mean_prod << endl;
statf.flush();
 // 
statf << "Number of rec/sender processes: " << 
NUM_RPROC << endl;
statf.flush();

senname = "Sender";
rname = "Receiver";
cname = "Channel";

 for (j = 0 ; j <= (NUM_RPROC) - (1); j++) { 
jstr = intstr(j);
// create and start a single Sender object 
send_obj[j] = new Sender(senname + jstr, mean_prod);
send_obj[j]->pstart();
 // create and start Receiver objects 
rec_obj [j] = new Receiver(rname + jstr, j, mean_cons);
 rec_obj [j]->pstart();
 // create channels between a pair of processes 
channel [j] = new Channel(cname + jstr, send_obj [j], rec_obj [j], meancomm, stdcomm);
 // 
 } // endfor 
 // now start simulation 
 sim->start_sim( simperiod );
 //  
 cout << " " << endl;
 for (j = 0 ; j <= (NUM_RPROC) - (1); j++) { 
sendname = send_obj[j]->get_name();
sendwait = send_obj[j]->get_wait();
statf.flush(); 
 cout << sendname << " accum wait interval: " << sendwait << endl;
statf << sendname << " accum wait interval: " << sendwait << endl;
statf.flush();
 // accumulate sender wait intervals 
 swait += sendwait; 

//robj = rec_obj [j];
recname = rec_obj[j]->get_name();
recwait = rec_obj[j]->get_wait();
 // 
statf.flush();
cout << recname << " accum wait interval: " << recwait << endl;
statf << recname << " accum wait interval: " << recwait << endl;
statf.flush();
 // accumulate receiver wait intervals 
 rwait += recwait;
 // terminate rec_obj[j] 
// rec_obj[j]->terminate();
 //send_obj[j]->terminate();
 } // endfor 
cout << "Average accum sender wait: " << (swait) / (NUM_SPROC) << endl;
sendwait = (swait) / (NUM_RPROC);
cout << "Average accum receiver wait: " << (rwait)/(NUM_RPROC) << endl;
recwait = (rwait)/(NUM_RPROC);
 // 
statf << "Acum sender wait: " << sendwait << endl;
statf.flush();
statf << "Average accum receiver wait: " << 
recwait << endl;
statf.flush();
 exit(0); 
 }  // end Main_body 
 int main() {
 // set-up simulation 
 // 
sim = new Simulation("Synchronous communication with channels");
 // setup files for reporting 
 Simulation::set_tracef("scommtrace.txt");
tracef.setf(ios::fixed);
tracef.setf(ios::showpoint);
 Simulation::set_statf("scommstat.txt");
 // 
 // simulation period 
 // 
simperiod = 2560;
 // text data as messages 
::data [0] = "Computer Science";
::data [1] = "College of Computing and Soft Engineering";
::data [2] = "Kennesaw State University";
::data [3] = "1100 South Marietta Pkwy";
::data [4] = "Marietta, GA";
 // create the main process  
comm_obj = new Scomm("Synchronous Communication");
 comm_obj->pstart();
 sim->end_sim();
 exit(0); 
 }  // end main 
