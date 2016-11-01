/**
  * Created by slowenth on 10/24/16.
  */


package object sonicpricer {

  // Some Named Types

  type SkuType = Int
  type PriceType = BigDecimal
  type LineItems = Map[SkuType, Int]


  case class Bundle (
      items: Map[SkuType, Int],
      price: BigDecimal
  )

  // Start with an empty map, and append any items with a difference in quantity > 0
  // Could be rewritten as an implicit method

  /** Utility method to subtract the quantities of one LineItems collection from Another
    *
    * @param items1 - LineItems
    * @param items2 - LineItems to subtract
    * @return - Option[LineItems] - None if any quantity of item2 exceeds that of items1.  Otherwise LineItems with the quantities of itemss1 less that of items2
    *         If the quantity of any line items results in 0, it is removed from the map.
    */
  def subtractQuantities(items1: LineItems, items2: LineItems): Option[LineItems] = {
    items1.foldLeft[Option[Map[SkuType, Int]]](Some(Map.empty[SkuType, Int])) { case (newMap, (sku, quantity)) =>
      val newQuantity = quantity - items2.getOrElse(sku, 0)
      newQuantity match {
        case 0 => newMap
        case n if n < 0 => None
        case _ => newMap.map(m => m + (sku -> newQuantity))  // If newMap is None, this will keep the result as None
      }
    }
  }

}

