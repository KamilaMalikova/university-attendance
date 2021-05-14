package com.university;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppTest {
    SparkSession ss;
    List<UserAttendance> attendanceList = new ArrayList<>();
    List<UserPublication> publicationList = new ArrayList<>();
    @Before
    public void setUp(){

        attendanceList.add(new UserAttendance(7, 2, new Date(2019, 0, 1, 14, 13, 5), true));
        attendanceList.add(new UserAttendance(7, 2, new Date(2019, 0, 1, 16, 47, 13), false));
        attendanceList.add(new UserAttendance(9, 31, new Date(2019, 0, 1, 18, 47, 5), true));
        attendanceList.add(new UserAttendance(9, 31, new Date(2019, 0, 1, 23, 47, 13), false));
        attendanceList.add(new UserAttendance(4, 41, new Date(2019, 0, 1, 23, 51, 5), true));
        attendanceList.add(new UserAttendance(4, 41, new Date(2019, 0, 2, 4, 41, 13), false));
        attendanceList.add(new UserAttendance(4, 23, new Date(2019, 0, 2, 6, 42, 5), true));
        attendanceList.add(new UserAttendance(4, 23, new Date(2019, 0, 2, 10, 42, 31), false));

        publicationList.add(new UserPublication(7, 2, 767, new Date(2019, 0, 1,3, 18, 6)));
        publicationList.add(new UserPublication(7, 2, 336, new Date(2019, 0, 1,6, 41, 6)));
        publicationList.add(new UserPublication(9, 31, 804, new Date(2019, 0, 1,1, 24, 6)));
        publicationList.add(new UserPublication(9, 31, 535, new Date(2019, 0, 1,2, 41, 6)));
        publicationList.add(new UserPublication(4, 41, 648, new Date(2019, 0, 1,6, 41, 6)));
        publicationList.add(new UserPublication(4, 41, 861, new Date(2019, 0, 1,0, 41, 6)));
        publicationList.add(new UserPublication(4, 23, 737, new Date(2019, 0, 1,3, 41, 6)));

        ss = SparkSession
                .builder()
                .master("local")
                .appName("Test")
                .getOrCreate();


    }

    @Test
    public void testComputation(){
        JavaSparkContext sc = new JavaSparkContext(ss.sparkContext());

        App app = new App(ss.sparkContext());
        JavaRDD<UserAttendance> userAttendanceJavaRDD = sc.parallelize(attendanceList);
        JavaRDD<UserPublication> userPublicationJavaRDD = sc.parallelize(publicationList);

        JavaRDD<Result> resultJavaRDD = app.getResultJavaRDD(userAttendanceJavaRDD, userPublicationJavaRDD);
        List<Result> resultList = resultJavaRDD.collect();
        Result result1 = resultList.stream()
                .filter(result -> result.getUnivId() == 7 && result.getUserId() == 2)
                .findFirst().orElse(null);
        assertEquals(4, resultList.size());
        assert result1 != null;
        assertEquals(2, result1.getPublications());
        assertEquals(2, result1.getHourInUniv());
    }
}
