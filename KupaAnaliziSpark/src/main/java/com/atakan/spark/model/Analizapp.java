package com.atakan.spark.model;

import com.google.common.collect.Iterators;
import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;
import scala.Tuple2;

import java.util.Iterator;


/**
 * Created by Atakan on 23.05.2023.
public class Analizapp {
    public static void main(String[] args) {

        System.setProperty("hadoop.home.dir","C:\\Users\\Atakan\\Desktop\\Big Data Program Atakan\\hadoop-common-2.2.0-bin-master");
        SparkSession spark= SparkSession.builder()
                .master("local")
                .appName("MongoSpark")
                .config("spark.mongodb.input.uri","mongodb://127.0.0.1/atacup.Atacollection")
                .config("spark.mongodb.output.uri","mongodb://127.0.0.1/atacup.Atacollection")
                .getOrCreate();


        JavaSparkContext ac=new JavaSparkContext(spark.sparkContext());

        JavaRDD<String> Raw_Data = ac.textFile("C:\\Users\\Atakan\\Desktop\\Big Data Program Atakan\\WorldCup\\WorldCupPlayers.csv");
        //System.out.println(Raw_Data.count());
        
        //Stringi dönüştür
        final JavaRDD<OyuncuModel> oyuncuRDD = Raw_Data.map(new Function<String, OyuncuModel>() {
            public OyuncuModel call(String satir) throws Exception {
                String[] dizi = satir.split(",",-1);
                return new OyuncuModel(dizi[0], dizi[1],
                        dizi[2], dizi[3], dizi[4], dizi[5],
                        dizi[6], dizi[7], dizi[8]);
            }
        });


        JavaRDD<OyuncuModel> tur = oyuncuRDD.filter(new Function<OyuncuModel, Boolean>() {
            public Boolean call(OyuncuModel oyuncuModel) throws Exception {
                return oyuncuModel.getTakim().equals("TUR");
            }
        });

        //Filtreleme kontrol -
        // Herhangi bir oyuncunun kaç maçta oynadığını sorgulama-filtre(Ronaldo)
        /*JavaRDD<OyuncuModel> ronaldoRDD = oyuncuRDD.filter(new Function<OyuncuModel, Boolean>() {
            public Boolean call(OyuncuModel oyuncuModel) throws Exception {
                return oyuncuModel.getOyuncuisim().equals("RONALDO");
            }
        });
        System.out.println("Ronaldo dünya kupalarında "+ronaldoRDD.count()+" maç yapmıştır");
*/


        //Oyuncu adı+ maç id sorgula - derle
        JavaPairRDD<String, String> mapRDD = tur.mapToPair(new PairFunction<OyuncuModel, String, String>() {
            public Tuple2<String, String> call(OyuncuModel oyuncuModel) throws Exception {
                return new Tuple2<String, String>(oyuncuModel.getOyuncuisim(), oyuncuModel.getMacID());
            }
        });
        mapRDD.foreach(new VoidFunction<Tuple2<String, String>>() {
            public void call(Tuple2<String, String> line) throws Exception {
                //System.out.println(line._1+" "+line._2);
            }
        });
        //oyuncu-maç id gruplama
        JavaPairRDD<String, Iterable<String>> groupOyuncu = mapRDD.groupByKey();

        JavaRDD<groupOyuncu> sonucRDD = groupOyuncu.map(new Function<Tuple2<String, Iterable<String>>, groupOyuncu>() {
            public groupOyuncu call(Tuple2<String, Iterable<String>> dizi) throws Exception {
                Iterator<String> iteratorraw = dizi._2().iterator();
                int size = Iterators.size(iteratorraw);
                return new groupOyuncu(dizi._1, size);
            }
        });
        //Sonucları konsol ekranına yazdır kontrol
        /*
        sonucRDD.foreach(new VoidFunction<com.atakan.spark.model.groupOyuncu>() {
            public void call(groupOyuncu groupOyuncu) throws Exception {
                System.out.println(groupOyuncu.getOyuncuİsim()+" "+groupOyuncu.getMatchcount());

            }
        });*/



          //sonuçları jsona çevir
        JavaRDD<Document> MongoRDD = sonucRDD.map(new Function<groupOyuncu, Document>() {
            public Document call(groupOyuncu groupOyuncu) throws Exception {
                return Document.parse("{OyuncuAdi: "+"'"+groupOyuncu.getOyuncuİsim()
                +"'"
                +","+"MacSayisi: "+groupOyuncu.getMatchcount());

            }
        });


        MongoSpark.save(MongoRDD);

    }



    }

