package com.university;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

import scala.Tuple2;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;

@Slf4j
public class App implements Serializable {
    private transient SparkContext sc;
    private final String keyspace = "university";
    private final String attendanceTable = "attendance";
    private final String publicationsTable = "publications";
    private final String resultTable = "result";

    public App(SparkContext sc) {
        this.sc = sc;
    }

    /**
     * Функция выгрузки публикаций и посещаемости из Cassandra и сохранения полученного результата
     */
    public void compute() {
        JavaRDD<UserAttendance> attendanceRDD = javaFunctions(sc)
                .cassandraTable(keyspace, attendanceTable, mapRowTo(UserAttendance.class));

        JavaRDD<UserPublication> publicationsRDD = javaFunctions(sc)
                .cassandraTable(keyspace, publicationsTable, mapRowTo(UserPublication.class));

        JavaRDD<Result> resultRDD = getResultJavaRDD(attendanceRDD, publicationsRDD);
        log.info("------------------ Results {} ! ------------------", resultRDD.collect().size());
        javaFunctions(resultRDD)
                .writerBuilder(keyspace, resultTable, mapToRow(Result.class))
                .saveToCassandra();
        log.info("------------------ Finishing ! ------------------");
    }
    /**
     * Функция посчитывает количество публикаций и общее количество часов, проведенных в учебном заведении по ключу год - университет - сотрудник / студент
     * @param attendanceRDD - входной RDD посещаемости для анализа
     * @param publicationsRDD - входной RDD публикаций для анализа
     * @return результат подсчета в формате JavaRDD
     */
    public JavaRDD<Result> getResultJavaRDD(JavaRDD<UserAttendance> attendanceRDD, JavaRDD<UserPublication> publicationsRDD) {
        log.info("------------------ Attendance analyzing! ------------------");
        JavaPairRDD<UserKey, UserAttendance> attendance = attendanceRDD
                .mapToPair((PairFunction<UserAttendance, UserKey, UserAttendance>) userAttendance ->
                        new Tuple2<>(new UserKey(userAttendance.getUnivId(), userAttendance.getUserId(),
                                LocalDateTime.ofInstant(userAttendance.getDate(), ZoneId.systemDefault()).getYear()), userAttendance))
                .reduceByKey((Function2<UserAttendance, UserAttendance, UserAttendance>) (v1, v2) -> {
                    if (v2.isIn()){
                        v1.addToIn(v2.getDate());
                    }else {
                        v1.addToOut(v2.getDate());
                    }
                    return v1;
                });

        log.info("------------------ Publications analyzing! ------------------");
        JavaPairRDD<UserKey, UserPublication> publications = publicationsRDD
                .mapToPair((PairFunction<UserPublication, UserKey, UserPublication>) userPublication ->
                        new Tuple2<>(new UserKey(userPublication.getUnivId(), userPublication.getUserId(),
                                LocalDateTime.ofInstant(userPublication.getDate(), ZoneId.systemDefault()).getYear()), userPublication))
                .reduceByKey((Function2<UserPublication, UserPublication, UserPublication>) (v1, v2) -> {
                    v1.addPublication(v2.getPublicationId());
                    return v1;
                });

        JavaPairRDD<UserKey, Tuple2<UserAttendance, UserPublication>> joined = attendance.join(publications);

        log.info("------------------ Summarizing! ------------------");
        JavaRDD<Result> resultRDD = joined
                .map((Function<Tuple2<UserKey, Tuple2<UserAttendance, UserPublication>>, Result>) v1 -> {
            UserKey key = v1._1;
            UserAttendance userAttendance = v1._2._1;
            UserPublication publication = v1._2._2;
            return new Result(key.getYear(), key.getUnivId(), key.getUserId(), publication.getPublicationsNumber(), userAttendance.getHours());
        });

        return resultRDD;
    }
}
