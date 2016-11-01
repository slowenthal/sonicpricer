package sonicpricer

import org.junit.Test
import org.junit.Assert._

/**
  * Created by slowenth on 10/29/16.
  */


class TestSonicPricer {

  val skus1 = Map(
    1 -> BigDecimal(100),
    2 -> BigDecimal(200),
    3 -> BigDecimal(300),
    4 -> BigDecimal(400)
  )


  val bundle1 = Map(Map( 1 -> 2) -> BigDecimal(150) )

  val bundle2 = Map(Map( 1 -> 2) -> BigDecimal(150),
                    Map( 2 -> 4) -> BigDecimal(125))

  val bundle3 = Map(Map( 1 -> 2) -> BigDecimal(150),
                    Map( 1 -> 2, 2 -> 2) -> BigDecimal(179),
                    Map( 2 -> 4) -> BigDecimal(125))

  @Test
  def testIndividualPrices(): Unit = {

    val sp = new SonicPricer(skus1, Map.empty)

    val p1 = sp.priceItemsIndividually( Map(1 -> 3, 3 -> 2))
    println(p1)



  }


  @Test
  def testSubtractItems(): Unit = {

    assertEquals(Some(Map(1 -> 2, 2 -> 3)), subtractQuantities( Map(1 -> 2, 2 -> 3) , Map.empty))
    assertEquals(None, subtractQuantities( Map(1 -> 2, 2 -> 3) , Map(2 -> 4)))
    assertEquals(Some(Map(1 -> 2)), subtractQuantities( Map(1 -> 2, 2 -> 3) , Map(2 -> 3)))
    assertEquals(Some(Map(1 -> 1, 2 -> 1)), subtractQuantities( Map(1 -> 2, 2 -> 3) , Map(2 -> 2, 1 -> 1)))
  }

  @Test
  def testBundles1Sku(): Unit = {
    val sp = new SonicPricer(skus1, bundle1)

    assertEquals(BigDecimal(150), sp.price(Map(1 -> 2)))
    assertEquals(BigDecimal(250), sp.price(Map(1 -> 3)))
    assertEquals(BigDecimal(300), sp.price(Map(1 -> 4)))
    assertEquals(BigDecimal(100), sp.price(Map(1 -> 1)))

    assertEquals(BigDecimal(22600), sp.price(Map(1 -> 301)))

  }

  @Test
  def testBundles2Skus(): Unit = {
    val sp = new SonicPricer(skus1, bundle2)

    assertEquals(BigDecimal(150),sp.price(Map(1 -> 2)))
    assertEquals(BigDecimal(100),sp.price(Map(1 -> 1)))

    assertEquals(BigDecimal(275),sp.price(Map(1 -> 2, 2 -> 4)))
    assertEquals(BigDecimal(475),sp.price(Map(1 -> 2, 2 -> 5)))

  }

  @Test
  def testBundles2SkusB(): Unit = {
    val sp = new SonicPricer(skus1, bundle3)

    assertEquals(BigDecimal(150),sp.price(Map(1 -> 2)))
    assertEquals(BigDecimal(100),sp.price(Map(1 -> 1)))
    assertEquals(BigDecimal(200),sp.price(Map(2 -> 1)))
    assertEquals(BigDecimal(400),sp.price(Map(2 -> 2)))

    assertEquals(BigDecimal(179),sp.price(Map(1 -> 2, 2 -> 2)))
    assertEquals(BigDecimal(275),sp.price(Map(1 -> 2, 2 -> 4)))
    assertEquals(BigDecimal(358),sp.price(Map(1 -> 4, 2 -> 4)))
    assertEquals(BigDecimal(475),sp.price(Map(1 -> 2, 2 -> 5)))

  }

}


