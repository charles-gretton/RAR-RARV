case class Customer(val num: Int, val pos: Tuple2[Int, Int], 
	  val demand: Int, val ready: Int, val due: Int, val service: Int) {
	val x = pos _1
	val y = pos _2
}
