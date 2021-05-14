#!/bin/bash

if [[ $# -eq 0 ]] ; then
    echo 'You should specify output folder and cassandra ip!'
    exit 1
fi

rm "$1""attendance.csv"
rm "$1""publications.csv"

MAX_DATE=$(date +'%s')
MIN_DATE=$(date "+%s" -d "01/01/2019 00:00:00")
DATE_DIFF=$(($MAX_DATE-$MIN_DATE))
echo "$DATE_DIFF"",""$MIN_DATE"

MAX_WORKING_SECONDS=8*60*60

ATTENDANCE=$1"attendance.csv"
PUBLICATIONS=$1"publications.csv"

IN_TEMP=$(($MIN_DATE + $(($RANDOM % $DATE_DIFF))))
for i in {1..1000}
	do
	    UNIV_ID=$((RANDOM % 10))
	    USER_ID=$((RANDOM % 50))
	    TYPE=true
	    IN_TEMP=$((IN_TEMP + $(($RANDOM % $DATE_DIFF))))
	    IN_DATE=$(date -d @"$IN_TEMP" +"%Y-%m-%d %H:%M:%S")
	    STR=$UNIV_ID","$USER_ID","$IN_DATE","$TYPE
      echo "$STR">>"$ATTENDANCE"

      OUT_TEMP=$(($IN_TEMP + ($RANDOM % $MAX_WORKING_SECONDS)))
      TYPE=false
	    OUT_DATE=$(date -d @"$OUT_TEMP" +"%Y-%m-%d %H:%M:%S")
	    STR=$UNIV_ID","$USER_ID","$OUT_DATE","$TYPE
      echo "$STR">>"$ATTENDANCE"
	done

for i in {1..3000}
	do
	    UNIV_ID=$((RANDOM % 10))
	    USER_ID=$((RANDOM % 50))
	    PUBLIC_ID=$((RANDOM % 1000))
	    DATE_TEMP=$((MIN_DATE + $(($RANDOM % $DATE_DIFF))))
	    IN_DATE=$(date -d @"$DATE_TEMP" +"%Y-%m-%d %H:%M:%S")
	    STR=$UNIV_ID","$USER_ID","$PUBLIC_ID","$IN_DATE
      echo "$STR">>"$PUBLICATIONS"
	done

cqlsh -f cassandra.cql

cqlsh -e "COPY university.attendance (univ_id , user_id , date , is_in ) FROM '""$ATTENDANCE""' WITH HEADER = true AND DELIMITER = ',' AND DATETIMEFORMAT = '%Y-%m-%d %H:%M:%S';"

cqlsh -e "COPY university.publications (univ_id , user_id , publication_id , date ) FROM '""$PUBLICATIONS""' WITH HEADER = true AND DELIMITER = ',' AND DATETIMEFORMAT = '%Y-%m-%d %H:%M:%S';"

/opt/spark-latest/bin/spark-submit --class com.university.Main --master local --deploy-mode client --executor-memory 1g --name wordcount --conf "spark.app.id=Main"  "$1"target/university-attendance-1.0-SNAPSHOT-jar-with-dependencies.jar "$2"
