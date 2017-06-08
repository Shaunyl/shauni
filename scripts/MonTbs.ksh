#!/usr/bin/ksh -x

# File: MonTbs.ksh
# Author: Filippo Testino
# Version 1.0.3.0
#
# This shell script runs Shauni's MonTbs command for retrieving tablespace informations
# and parsing in search of possible errors

#### --------------

MAIL_LIST="filippo.testino@gmail.it"
MAIL_FILE=tablespace_report_mail-`date "+%d%m%H%M"`.txt
MAIL_PATH=$PWD/tablespace/mail/$MAIL_FILE
LOG_RETENTION=7

mkdir -p $PWD/tablespace/mail
mkdir -p $PWD/tablespace/log
mkdir -p $PWD/logs/montbs

# Shauni Parameters

CRITICAL=96
WARNING=96
DIR=./logs/montbs
CLUSTER=4
UNDO="-undo"
PARFILE=
HELP=
EXCLUDE=
AUTO=
UNIT=

#### --------------

# Parse configuration file
hosts=$(cat ./config/multidb.cfg | sed '/^\s*#/d;/^\s*$/d' | wc -l | awk '{print $1}')

echo "Tablespace Report" > $MAIL_PATH
echo "" >> $MAIL_PATH
echo "Starting MonTbs on $hosts hosts at $(date '+%d-%b-%Y %X')" >> $MAIL_PATH

# Check for -undo option
if [ ! "$UNDO" = "-undo" ]; then
 echo "Ignoring tablespace contents -> UNDO" >> $MAIL_PATH
 UNDO=
fi

echo "Thresholds used (inclusive) -> $CRITICAL for critical, $WARNING for warning" >> $MAIL_PATH
echo "" >> $MAIL_PATH

# Running Shauni
java -cp lib/*:. -jar lib/shauni-1.0.2.57.jar montbs -cluster=$CLUSTER -directory=$DIR $UNDO -critical=$CRITICAL -warning=$WARNING

# Parsing output report file
echo "Tablespace check:" >> $MAIL_PATH
echo " " >> $MAIL_PATH
egrep -ih "%" ./tablespace/* | awk {'printf ("%-20s %-43s %10s %15s\n", $1, $2, $3, $4)'} | sort -nr -k 3 >> $MAIL_PATH
echo " " >> $MAIL_PATH

# Check for errors
echo "Errors during execution:" >> $MAIL_PATH
fails=$(./grep.pl "aborted" 4 shauni.log)
if [ ! -n "$fails" ]; then
 echo "> nope, all databases have been checked successfully.." >> $MAIL_PATH
 echo " " >> $MAIL_PATH
else
 echo " " >> $MAIL_PATH
 echo "$fails" >> $MAIL_PATH
 echo " " >> $MAIL_PATH
fi

# Completing and sending email
echo "MonTbs completed successfully at $(date '+%d-%b-%Y %X')" >> $MAIL_PATH
(printf "%s\n" "MonTbs Report in the attachment..!" ; uuencode $MAIL_PATH $MAIL_FILE) | mailx -r "shauni@mon.it" -s "MonTbs Report" ${MAIL_LIST}

# Move and clean logs
mv ./tablespace/*.txt ./tablespace/log
find ./tablespace/log/ -name *.txt -mtime "+$LOG_RETENTION" -a -exec rm -f {} \; >/dev/null
