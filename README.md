# **Shauni**
Command line based tool to perform automated actions on different Oracle databases on multiple servers simultaneously

Current version is **1.0.1.77a**  

## **Changelog**  
 Introduced Chain of Responsability pattern to cope with the filename building through wilcards  
 Introduced a way to run jobs simultaneously on different servers/instances  
 Added parallism to the Export Command  
 Connection strings are now encrypted in the configuration file multidb.cry [not yet supported]  
 New command **addcs** which let you add connection string to the encrypted file [alpha]

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
