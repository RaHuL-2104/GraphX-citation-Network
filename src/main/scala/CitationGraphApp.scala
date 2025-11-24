import org.apache.spark.sql.SparkSession
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object CitationGraphApp {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("GraphX Academic Citation Network")
      .master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext

    // ============================
    // 1. LOAD CITATION DATA
    // ============================
    val raw = sc.textFile("data/citations.txt").filter(_.trim.nonEmpty)

    val edges: RDD[Edge[Int]] = raw.map { line =>
      val parts = line.trim.split("\\s+")
      Edge(parts(0).toLong, parts(1).toLong, 1)
    }

    val defaultAttr = 1
    val graph = Graph.fromEdges(edges, defaultAttr).cache()

    println("=======================================")
    println(" Graph Loaded Successfully ")
    println("=======================================")
    println(s"Total vertices: ${graph.numVertices}")
    println(s"Total edges:    ${graph.numEdges}")
    println("=======================================\n")


    // ============================
    // 2. IN-DEGREE (CITATION COUNT)
    // ============================
    val inDeg = graph.inDegrees.cache()

    println("\nTop 15 Most Cited Papers (In-Degree):")
    inDeg.sortBy(_._2, ascending = false).take(15).foreach(println)

    println("\n=======================================\n")


    // ============================
    // 3. PAGE RANK
    // ============================
    println("Running PageRank...")
    val ranks = graph.pageRank(0.0001).vertices

    val joined = ranks.join(inDeg).map {
      case (paperId, (rank, indegree)) => (paperId, rank, indegree)
    }

    println("\nTop 15 Influential Papers by PageRank:")
    joined.sortBy(_._2, ascending = false).take(15).foreach(println)

    println("\n=======================================\n")


    // ============================
    // 4. OUT-DEGREES
    // ============================
    val outDeg = graph.outDegrees.cache()


    // ============================
    // 5. ISOLATED NODES (SAFE METHOD)
    // ============================

    // all vertex IDs
    val allVerts = graph.vertices.map(_._1)

    // vertices referenced in in- or out- degree
    val connected = inDeg.map(_._1).union(outDeg.map(_._1)).distinct()

    // isolated = allVerts - connected
    val isolated = allVerts.subtract(connected)

    println("\nSample Isolated Vertices:")
    isolated.take(20).foreach(println)

    println("\n=======================================\n")


    // ============================
    // 6. EXPORT EDGES FOR VISUALIZATION
    // ============================
    graph.edges
      .map(e => s"${e.srcId},${e.dstId}")
      .coalesce(1)
      .saveAsTextFile("output/graph_edges")

    println("Visualization edges saved to: output/graph_edges/")
    println("Python NetworkX to visualize.")

    println("\n************* TASKS COMPLETE *************\n")

    spark.stop()
  }
}
