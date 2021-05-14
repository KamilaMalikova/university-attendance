package com.university;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

/**
 * Программа подсчитывает данные публикационной активности сотрудников и студентов университетов и статистику посещаемости организации за год.
 * Результат записывается в Cassandra
 * */
public class Main {
    /**
     * @param args - args[0]: host
     */
    public static void main(String[] args) {
        if (args.length < 1){
            System.out.println("Require host");
        }
        String master = "local";
        String host = args[0];

        SparkConf conf = new SparkConf();
        conf.setAppName("university");
        conf.setMaster(master);
        conf.set("spark.cassandra.connection.host", host);

        SparkContext sc = new SparkContext(conf);
        App app = new App(sc);
        app.compute();
        sc.stop();
    }

}

