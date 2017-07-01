# **Shauni**
Command line based tool useful to DBAs to perform automated actions on different Oracle databases on multiple servers simultaneously.

**For further information surf to [Wiki](https://github.com/Shaunyl/shauni/wiki/Commands)!**

Exeternal dependencies used are JCommander, Spring 4, JDBC, Log4J2, Lombok, Mockito, JUnit, Java 8  

It is a Spring version of enver (see my other repositories).  It also takes advantage of Threads (ExecutorService so far) to parallelize some tasks and to perform the same task on different nodes/databases at the same time.  

Current version is **1.0.2 Alpha**  
Current version on branch is **1.0.3 Alpha**  

### **Changelog**  
Take a look at the Wiki page 'Changelog' [here](https://github.com/Shaunyl/shauni/wiki/Change-Log)! 
 
### **Future Improvements**<br/>
Add the possibility to use a custom configuration file (not just multidb.cfg)<br/>
Add the support for MySQLs databases<br/>

### **Commands**  
* exp
* montbs
  
Example Usage:<br/>
```java
exp -tables=dba_users,dba_registry -cluster=3 -parallel=2 -filename=%d-%t_[%n-%w%u] \  
   -Cusername=30 -Cuser_id=9 -directory=export<br/>
   
montbs -directory=tbs -undo -exclude=users,stat -cluster=2 -warning=60 -auto
```
