#!/bin/bash

if [[ $# -eq 0 ]] ; then
    echo 'You should specify output folder!'
    exit 1
fi

ATTENDANCE=$1"/attendance.csv"
PUBLICATIONS=$1"/publications.csv"
echo "$ATTENDANCE"", - ""$PUBLICATIONS"

cqlsh -f cassandra.cql

cqlsh -e "COPY university.attendance (univid , userid , date , isin ) FROM '""$ATTENDANCE""' WITH HEADER = true AND DELIMITER = ',' AND DATETIMEFORMAT = '%Y-%m-%d %H:%M:%S';"

cqlsh -e "COPY university.publications (univid , userid , publicationid , date ) FROM '""$PUBLICATIONS""' WITH HEADER = true AND DELIMITER = ',' AND DATETIMEFORMAT = '%Y-%m-%d %H:%M:%S';"
