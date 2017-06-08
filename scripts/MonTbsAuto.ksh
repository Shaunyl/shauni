#!/usr/bin/ksh -x

MAIL_LIST="filippo.testino@gmail.it"
MAIL_FILE=auto_tablespace_report_mail-`date "+%d%m%H%M"`.txt
MAIL_PATH=$PWD/tablespace/mail/$MAIL_FILE
LOG_RETENTION=7
THRESHOLD=85
DIR=./tablespace
MULTI=y
hosts=1

if [ "$MULTI" = "y" ]; then
 hosts=$(cat ./config/multidb.cfg | sed '/^\s*#/d;/^\s*$/d' | wc -l | awk '{print $1}')
fi

echo "Alarm Auto Tablespace Report" > $MAIL_PATH
echo "" >> $MAIL_PATH
echo "Starting MonTbsAuto on $hosts hosts at $(date '+%d-%b-%Y %X')" >> $MAIL_PATH

echo "Threshold used (inclusive) -> $THRESHOLD" >> $MAIL_PATH
echo "" >> $MAIL_PATH

java -cp lib/*:. -jar lib/enver-0.91.4-ALPHA.jar montbsauto -multi=$MULTI -directory=$DIR -threshold=$THRESHOLD

echo "Tablespace check:" >> $MAIL_PATH
results=$(egrep -ih "%" ./tablespace/* | awk {'printf ("%-20s %-36s %29s %19s %25s\n", $1, $2, $3, $4, $5)'} | sort -nr -k 5)
if [ ! -n "$results" ]; then
 echo "> all datafiles are sub-threshold for all tablespaces." >> $MAIL_PATH
else
 echo "$results" >> $MAIL_PATH
fi
echo " " >> $MAIL_PATH

echo "Errors during execution:" >> $MAIL_PATH
fails=$(./grep.pl "aborted" 4 enverMONTBSAUTO.log)
if [ ! -n "$fails" ]; then
 echo "> nope, all databases have been checked successfully.." >> $MAIL_PATH
else
 echo " " >> $MAIL_PATH
 echo "$fails" >> $MAIL_PATH
 echo " " >> $MAIL_PATH
 echo "See logs for further information.." >> $MAIL_PATH
 echo " " >> $MAIL_PATH
fi
echo " " >> $MAIL_PATH

echo "MonTbsAuto completed at $(date '+%d-%b-%Y %X')" >> $MAIL_PATH
(printf "%s\n" "MonTbsAuto Report in the attachment..!" ; uuencode $MAIL_PATH $MAIL_FILE) | mailx -r "oracle@intranet.it" -s "Alarm Auto Tbs - SOA/SOM - EDH/DWH" ${MAIL_LIST}
mv ./tablespace/*.txt ./tablespace/log
find ./tablespace/log/ -name *.txt -mtime "+$LOG_RETENTION" -a -exec rm -f {} \; >/dev/null
