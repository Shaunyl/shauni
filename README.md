# **Shauni**
Command line based tool to perform automated actions on different Oracle databases on multiple servers simultaneously.

Libraries used are JCommander, Spring, JDBC, Log4J, Lombok

Current version is **1.0.1 Alpha**  

## **Changelog**  
 Introduced Chain of Responsability pattern to cope with the filename building through wildcards.  
 Introduced a way to run jobs simultaneously on different servers/instances.  
 Added parallism to the Export Command.
 Added the property **database.timeout** to specify the timeout of database connections.<br/>
 Up bounded the parameter **cluster**.<br/>
 
## **Issues & Improvements**<br/>
Add a property to choose if to use a crypted configuration file or a not<br/>
Add the commands **viewcs** and **removecs** to copy easily with crypted configuration files<br/>
Add the possibility to use a specific configuration file (not only multidb.cfg or multidb.cry)<br/>
Add the support for MySQLs databases<br/>

## **Commands**  

### **EXP**<br />
Export tables or datasets from databases to files.<br/>
#### &nbsp;Options
&nbsp;&nbsp;&nbsp;-parallel&nbsp;&nbsp;&nbsp;(attempt to export datasets in parallel)<br/>
&nbsp;&nbsp;&nbsp;-cluster&nbsp;&nbsp;&nbsp;(number of threads that will handle the whole task)<br/>
&nbsp;&nbsp;&nbsp;-tables&nbsp;&nbsp;&nbsp;(tables to be exported)<br/>
&nbsp;&nbsp;&nbsp;-queries&nbsp;&nbsp;&nbsp;(queries whose resultset have to be exported)<br/>
&nbsp;&nbsp;&nbsp;-format&nbsp;&nbsp;&nbsp;(format of exported objects)<br/>
&nbsp;&nbsp;&nbsp;-directory&nbsp;&nbsp;&nbsp;(directory where data are exported)<br/>
&nbsp;&nbsp;&nbsp;-filename&nbsp;&nbsp;&nbsp;(ilenames of the objects being exported)<br/>

Only for CSVs filenames<br/>
&nbsp;&nbsp;&nbsp;-start&nbsp;&nbsp;&nbsp;(line of the table at which starting to export)<br/>
&nbsp;&nbsp;&nbsp;-end&nbsp;&nbsp;&nbsp;(last line of the table to be exported)<br/>
&nbsp;&nbsp;&nbsp;-delimiter&nbsp;&nbsp;&nbsp;(CSV delimiter. Defaults to a comma)<br/>

**Notes:**<br/>
&nbsp;&nbsp;queries and tables parameters are mutually exclusive<br/>
&nbsp;&nbsp;parallel must be >= 1. If parallel > #objects, then it will be adjusted<br/>
&nbsp;&nbsp;cluster should be greater than 0 but lesser or equals to the number of configured connections<br/>
&nbsp;&nbsp;format can be tab (i.e. tabular) or csv<br/>
&nbsp;&nbsp;Filename wildcards supported are:<br/>
&nbsp;&nbsp;&nbsp;&nbsp;%u: progress id to identify the object currently being exported<br/>
&nbsp;&nbsp;&nbsp;&nbsp;%t: the name of table being exported (only with Table Mode)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;%u: worker number (0 if parallel=1)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;%d: current timestamp (format is ddMMyy-HHmm)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;%n: name of the thread that is performing the task<br/>
  
**Example:**<br/>
&nbsp;&nbsp;exp -format=tab -tables=dba_users,dba_registry -cluster=2 -parallel=3 -directory=out -filename=%d-%t_[%n-%w%u].txt<br/>
