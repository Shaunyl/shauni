# **Shauni**
Command line based tool useful to DBAs to perform automated actions on different Oracle databases on multiple servers simultaneously.

Exeternal dependencies used are JCommander, Spring 4, JDBC, Log4J2, Lombok, Mockito, JUnit, Java 8  

It is a Spring version of enver (see my other repositories).  It also takes advantage of Threads (ExecutorService so far) to parallelize some tasks and to perform the same task on different nodes/databases at the same time.  

Current version is **1.0.2 Alpha**  

For further information surf to Wiki!

### **Changelog**  
 Used Chain of Responsability design pattern to cope with the filename building through wildcards.  
 Introduced a way to run jobs simultaneously on different servers/instances.  
 Added further parallelism to the Export Command.  
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
`exp -tables=dba_users,dba_registry -cluster=3 -parallel=2 -filename=%d-%t_[%n-%w%u] -Cusername=30`<br/>
`montbs -directory=tbs -undo=n -exclude=users,stat -cluster=2 -warning=60`
