import java.awt.{Color, Graphics2D, BasicStroke, Font}
import java.io.File
import java.awt.image._
import javax.imageio.ImageIO

import scala.collection.Map

object Imaginario {
	def writeRARImage(file: String, inst: Instance, rotos: List[Customer], sol: List[List[Customer]]) = {
		val size = 650

		val scaleX = size / inst.customers.sort(_.x > _.x).head.x
		val scaleY = size / inst.customers.sort(_.y > _.y).head.y
		
		val buff = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
		
		val g = buff.createGraphics
		g.setColor(Color.WHITE)
		g.fillRect(0, 0, size, size)
		
		
		def drawCustomer(cust: Customer, color: Color) = {
			// el punto
			g.setColor(color)
			g.fillRect(cust.x * scaleX - 3, cust.y * scaleY - 3, 6, 6)
		
			// el num
			g.setColor(Color.BLACK)
			g.drawString(""+cust.num, cust.x * scaleX + 4, cust.y * scaleY + 4)
		}
		
		def drawCustTau(cust: Customer, v: Int) = {
			def s = v + 6
			
			// el punto
			g.setColor(Color.YELLOW)
			g.drawOval(cust.x * scaleX - s/2 + 3, cust.y * scaleY - s/2 + 3, s, s)
		}

		def drawLine(par: (Customer, Customer), color: Color) = {
			g.setColor(color)
//			g.setStroke(new BasicStroke(2))
			g.drawLine(par._1.x * scaleX, par._1.y * scaleY, par._2.x * scaleX, par._2.y * scaleY)
		}
	
		// para los customers		
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 10))

//		g.setColor(Color.LIGHT_GRAY)
//		g.fillOval((c.x - dist).toInt * scaleX, (c.y - dist).toInt * scaleY, (dist * scaleX * 2).toInt, (dist * scaleY * 2).toInt)
		
		// nodos
		drawCustomer(inst.source, Color.BLACK)
		(inst.customers.drop(1)--rotos).foreach(
			drawCustomer(_, Color.BLUE)
		)

		rotos.tail.foreach(drawCustomer(_, Color.RED))
		
		def colStream: Stream[Color] = {
			val colores = List(Color.GREEN, Color.YELLOW, Color.CYAN,  
									Color.MAGENTA, Color.ORANGE, Color.PINK,
									Color.WHITE, Color.GRAY,
									Color.GREEN.brighter(), Color.YELLOW.brighter(), Color.CYAN.brighter(),  
									Color.MAGENTA.brighter(), Color.ORANGE.brighter(), Color.PINK.brighter(),
									Color.GRAY.brighter())
			colores.toStream append colStream
		}

		// soluciones
		sol.toStream.zip(colStream).foreach { stream =>
			val l = stream _1
			val col = stream _2

			l.zip(l.tail ::: List(l.head)).foreach(
				drawLine(_, col)
			)
		}

		val customers = sol.foldLeft(0)(_ + _.size - 1)
		val vehiculos = sol.length

		g.setColor(Color.BLACK)
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15))
		g.drawString("" + vehiculos + " | " + customers, 20, size-50)

		ImageIO.write(buff, "jpeg", new File(file));
	}

	def writeImage(file: String, inst: Instance, sol: List[List[Customer]]) = {
		val size = 650

		val scaleX = size / inst.customers.sort(_.x > _.x).head.x
		val scaleY = size / inst.customers.sort(_.y > _.y).head.y
		
		val buff = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
		
		val g = buff.createGraphics
		g.setColor(Color.WHITE)
		g.fillRect(0, 0, size, size)
		
		def drawCustomer(cust: Customer, color: Color) = {
			// el punto
			g.setColor(color)
			g.fillRect(cust.x * scaleX, cust.y * scaleY, 6, 6)
		
			// el num
//			g.setColor(Color.WHITE)
//			g.drawString(""+cust.num, cust.x * scaleX, cust.y * scaleY)
		}
	
		def drawLine(par: (Customer, Customer), color: Color) = {
			g.setColor(color)
			g.setStroke(new BasicStroke(2))
			g.drawLine(par._1.x * scaleX, par._1.y * scaleY, par._2.x * scaleX, par._2.y * scaleY)
		}

		// para los customers		
//		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 10))
		
		// nodos
//		drawCustomer(inst.source, Color.BLACK)
      g.setColor(Color.BLACK)
		g.fillOval(inst.source.x * scaleX, inst.source.y * scaleY, 10, 10)
      
		inst.customers.drop(1).foreach(
			drawCustomer(_, Color.BLUE)
		)
		
		def colStream: Stream[Color] = {
			val colores = List(Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN,  
									Color.MAGENTA, Color.ORANGE, Color.PINK,
									Color.BLACK, Color.GRAY)
			colores.toStream append colStream
		}

		// soluciones
		sol.toStream.zip(colStream).foreach { stream =>
			val l = stream _1
			val col = stream _2

			l.zip(l.tail ::: List(l.head)).foreach(
				drawLine(_, col)
			)
		}
		
		// footnote
//		val faltantes = (inst.customers -- sol.foldLeft(List[Customer]())(_ ++ _)).map(_.num).mkString(", ")
//		val customers = sol.foldLeft(0)(_ + _.size - 1)
//		val vehiculos = sol.length

//		g.setColor(Color.WHITE)
//		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15))
//		g.drawString("" + vehiculos + " | " + customers + " | " + faltantes, 20, size-50)
		
		ImageIO.write(buff, "jpeg", new File(file));
	}
	
	def writeTauImage(file: String, inst: Instance) = {
		val size = 650

		val scaleX = size / inst.customers.sort(_.x > _.x).head.x
		val scaleY = size / inst.customers.sort(_.y > _.y).head.y

		val buff = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
		
		val g = buff.createGraphics
		
		def drawCustomer(cust: Customer, color: Color) = {
			// el punto
			g.setColor(color)
			g.fillRect(cust.x * scaleX, cust.y * scaleY, 6, 6)
		
			// el num
			g.setColor(Color.WHITE)
			g.drawString(""+cust.num, cust.x * scaleX, cust.y * scaleY)
		}
	
		def drawLine(par: (Customer, Customer), color: Color) = {
			g.setColor(color)
			g.setStroke(new BasicStroke(2))
			g.drawLine(par._1.x * scaleX, par._1.y * scaleY, par._2.x * scaleX, par._2.y * scaleY)
		}
		
		// nodos
		drawCustomer(inst.source, Color.WHITE)
		inst.customers.drop(1).foreach(
			drawCustomer(_, Color.BLUE)
		)
		
		val maxTau = inst.maxTau

		// tau
		inst.tauMap.keys.foreach { par =>
			val tau = inst.tau(par._1, par._2)
			if (tau != 0) {
				val f = (tau / maxTau).toFloat
				val color = new Color(f, f, f)
			
				drawLine(par, color)
			}
		}
		
		ImageIO.write(buff, "jpeg", new File(file));
	}

	def writeTauAndAnt(file: String, inst: Instance, sol: List[List[Customer]]) = {
		val size = 650

		val scaleX = size / inst.customers.sort(_.x > _.x).head.x
		val scaleY = size / inst.customers.sort(_.y > _.y).head.y

		val buff = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
		
		val g = buff.createGraphics
		
		def drawCustomer(cust: Customer, color: Color) = {
			// el punto
			g.setColor(color)
			g.fillRect(cust.x * scaleX, cust.y * scaleY, 6, 6)
		
			// el num
			g.setColor(Color.WHITE)
			g.drawString(""+cust.num, cust.x * scaleX, cust.y * scaleY)
		}
	
		def drawLine(par: (Customer, Customer), color: Color) = {
			g.setColor(color)
			g.setStroke(new BasicStroke(2))
			g.drawLine(par._1.x * scaleX, par._1.y * scaleY, par._2.x * scaleX, par._2.y * scaleY)
		}
		
		// nodos
		drawCustomer(inst.source, Color.WHITE)
		inst.customers.drop(1).foreach(
			drawCustomer(_, Color.BLUE)
		)
		
		val maxTau = inst.maxTau

		// tau
		inst.tauMap.keys.foreach { par =>
			val tau = inst.tau(par._1, par._2)
			if (tau != 0) {
				val f = (tau / maxTau).toFloat
				val color = new Color(f, f, f)
			
				drawLine(par, color)
			}
		}
		
		def colStream: Stream[Color] = {
			val colores = List(Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN,  
									Color.MAGENTA, Color.ORANGE, Color.PINK,
									Color.WHITE, Color.GRAY)
			colores.toStream append colStream
		}

		// soluciones
		sol.toStream.zip(colStream).foreach { stream =>
			val l = stream _1
			val col = stream _2

			l.zip(l.tail ::: List(l.head)).foreach(
				drawLine(_, col)
			)
		}
		
		// footnote
		val faltantes = (inst.customers -- sol.foldLeft(List[Customer]())(_ ++ _)).map(_.num).mkString(", ")
		val customers = sol.foldLeft(0)(_ + _.size - 1)
		val vehiculos = sol.length
		
		g.setColor(Color.WHITE)
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15))
		g.drawString("" + vehiculos + " | " + customers + " | " + faltantes, 20, size-50)
		
		ImageIO.write(buff, "jpeg", new File(file));
	}

	def writeTauCSV(file: String, inst: Instance) = {
		val nf = new java.text.DecimalFormat("#.##")
		var outFile = new java.io.FileOutputStream(file)
		var out = new java.io.PrintStream(outFile)

		out.println((1 to inst.customers.length-1).mkString(",", ",", ""))
		
		for(i <- inst.customers.tail) {
			out.print(i.num + ",")
			for( j <- inst.customers.tail) {
				val tau = inst.tau(i,j)
				out.print(nf.format(tau) + ",")
			}
			out.println()
		}
		
		out.close
	}
}
