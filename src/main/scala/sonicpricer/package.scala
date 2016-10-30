/**
  * Created by slowenth on 10/24/16.
  */


package object sonicpricer {

  type SkuType = Int
  type PriceType = BigDecimal

  case class LineItem (
      SKU:   SkuType,
      quantity: Int
  )

  case class Bundle (
      items: Map[SkuType, Int],
      price: BigDecimal
  )

}

