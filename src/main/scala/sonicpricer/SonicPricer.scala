package sonicpricer

/**
  * Created by slowenth on 10/24/16.
  */
class SonicPricer(
                   individualPrices: Map[SkuType, PriceType],
                   bundles: Map[Map[SkuType, Int], PriceType]
                 ) {
  // Lets treat bundles as a partial function of  Set[LineItem] -> Price
  // We'll use a map as it implements PartialFunction

  def filterBundles(items: Map[SkuType, Int], bundles: Map[Map[SkuType, Int], PriceType]) =
    bundles.filter {
      case (bundleItems, price) => bundleItems
        // for each item in the bundle - determine if there is sufficient item quantity
        .map { case (sku, bundleQuantity) => items.get(sku)
      match {
        case Some(itemQuantity) => itemQuantity >= bundleQuantity
        case _ => false
      }
      }
        // ensure all items in that bundle are true
        .reduce(_ && _)
    }


  def priceItemsIndividually(items: Map[SkuType, Int]) =
    items.foldLeft(BigDecimal(0)) { case (total, (sku, quantity)) => total + quantity * individualPrices(sku) }


  def subtractQuantities(items: Map[SkuType, SkuType], possibleBundle: (Map[SkuType, Int], PriceType)): Option[Map[SkuType, SkuType]] = {
    items.foldLeft[Option[Map[SkuType, SkuType]]](Some(Map.empty[SkuType, SkuType])) { case (newMap, (sku, quantity)) =>
      val newQuantity = quantity - possibleBundle._1.getOrElse(sku, 0)
      newQuantity match {
        case 0 => newMap
        case n if n < 0 => None
        case _ => newMap.map(m => m + (sku -> newQuantity))
      }
    }
  }

  def price(items: Map[SkuType, Int]): PriceType =
    price(items, bundles)

  private def price(items: Map[SkuType, Int], bundles: Map[Map[SkuType, Int], PriceType]): PriceType = {

    if (items.isEmpty)
      0
    else {
      // Reduce the number of bundles to the ones that are possible
      val possibleBundles = filterBundles(items, bundles)

      // Price the items as individuals
      var lowest = priceItemsIndividually(items)

      for (possibleBundle <- possibleBundles) {
        val bundlePrice = possibleBundles(possibleBundle._1)
        val remainder = subtractQuantities(items, possibleBundle)

        // If we have a remainder then price is bundle + price of remainder otherwise ignore so keep lowest
        val newPrice = remainder.map(r => bundlePrice + price(r, possibleBundles)).getOrElse(lowest)

        lowest = lowest min newPrice
      }
      lowest
    }
  }
}
