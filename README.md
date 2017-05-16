# **Shauni**
Command line based tool to perform automated actions on different Oracle databases on multiple servers simultaneously.

Libraries used are JCommander, Spring, JDBC, Log4J, Lombok, Mockito, JUnit

Current version is **1.0.1 Alpha**  

For further information surf to Wiki!

### **Changelog**  
 Used Chain of Responsability design pattern to cope with the filename building through wildcards.  
 Introduced a way to run jobs simultaneously on different servers/instances.  
 Added parallism to the Export Command.
 Added the property **database.timeout** to specify the timeout of database connections.<br/>
 Up bounded the parameter **cluster**.<br/>
 
### **Issues & Improvements**<br/>
Add a property to choose if to use a crypted configuration file or a not<br/>
Add the commands **viewcs** and **removecs** to copy easily with crypted configuration files<br/>
Add the possibility to use a specific configuration file (not only multidb.cfg or multidb.cry)<br/>
Add the support for MySQLs databases<br/>
Improve boolean command inputs (from this -undo=y to this -undo)<br/>

### **Commands**  
* exp
* montbs
  
Example:<br/>
`exp -tables=dba_users,dba_registry -cluster=3 -parallel=2 -filename=%d-%t_[%n-%w%u] -Cusername=30`<br/>
`montbs -directory=tbs -undo=n -exclude=users,stat -cluster=2 -warning=60`
