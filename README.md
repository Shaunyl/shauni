# **Shauni**
Command line based tool useful to DBAs to perform automated actions on different Oracle databases on multiple servers simultaneously.

Exeternal dependencies used are JCommander, Spring 4, JDBC, Log4J2, Lombok, Mockito, JUnit, Java 8  

It is a Spring version of enver (see my other repositories).  It also takes advantage of Threads (ExecutorService so far) to parallelize some tasks and to perform the same task on different nodes/databases at the same time.  

Current version is **1.0.2 Alpha**  

For further information surf to Wiki!

### **Changelog**  
**1.0.2**  
 Used Template design pattern to write a CommandAction to represent the base workflow of a command    
 Code refactored multiple times  
 Fixed some bugs.  
 Added parfile option. Usage:  
```java
exp -parfile=/exp/shauni.par -format=csv -delimiter=;
 
 ### shauni.par ###
filename=%t-%d-%w%u
queries=dba_users(user_id,username):WHERE profile = 'DEFAULT';
        dba_registry(comp_id,comp_name):;
        dual:;
        v$session(sid,event):WHERE type != 'BACKGROUND'
directory=out
parallel=4
cluster=2
```
 
**1.0.1**  
 Used Chain of Responsability design pattern to cope with the filename building through wildcards. 
 Enhanced parallelism:
 * Introduced a way to run jobs simultaneously on different servers/instances through the parameter **cluster**
 * Added further parallelism to the Export Command through the parameter **parallel**. 
 ![exp-shauni-diagram](https://github.com/Shaunyl/shauni/blob/master/exp-shauni.PNG)
 
 Added the property **database.timeout** to specify the timeout of database connections.<br/>
 Up bounded the parameter **cluster**.<br/>
 
### **Future Improvements**<br/>
Add the possibility to use a custom configuration file (not just multidb.cfg)<br/>
Add the support for MySQLs databases<br/>
Improve boolean command inputs (i.e., from this -log=y to this -log)<br/>

### **Commands**  
* exp
* montbs
  
Example Usage:<br/>
```java
exp -tables=dba_users,dba_registry -cluster=3 -parallel=2 -filename=%d-%t_[%n-%w%u] \  
   -Cusername=30 -Cuser_id=9 -directory=export<br/>
   
montbs -directory=tbs -undo=n -exclude=users,stat -cluster=2 -warning=60  
```
