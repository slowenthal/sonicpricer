package sonicpricer

/**
  * Created by slowenth on 10/24/16.
  */
class SonicPricer(
                   individualPrices: Map[SkuType, PriceType],
                   allBundles: Map[LineItems, PriceType]
                 ) {
  // Lets treat bundles as a partial function of  Set[LineItem] -> Price
  // We'll use a map as it implements PartialFunction

  def filterBundles(items: LineItems, bundles: Map[LineItems, PriceType]) =
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

  def priceItemsIndividually(items: LineItems) =
    items.foldLeft(BigDecimal(0)) { case (total, (sku, quantity)) => total + quantity * individualPrices(sku) }


  def subtractQuantities(items: LineItems, possibleBundle: (LineItems, PriceType)): Option[LineItems] = {
    items.foldLeft[Option[Map[SkuType, Int]]](Some(Map.empty[SkuType, Int])) { case (newMap, (sku, quantity)) =>
      val newQuantity = quantity - possibleBundle._1.getOrElse(sku, 0)
      newQuantity match {
        case 0 => newMap
        case n if n < 0 => None
        case _ => newMap.map(m => m + (sku -> newQuantity))
      }
    }
  }

  def price(items: LineItems): PriceType =
    price(items, allBundles)

  private def price(items: LineItems, bundles: Map[LineItems, PriceType]): PriceType = {

    if (items.isEmpty) 0
    else {
      // Reduce the number of bundles to the ones that are possible
      val applicableBundles = filterBundles(items, bundles)

      // Price the items as individuals
      val individualPrice = priceItemsIndividually(items)

      applicableBundles.foldLeft[BigDecimal](individualPrice) {
        case (lowest, bundle) =>
          val bundlePrice = applicableBundles(bundle._1)
          val remainder = subtractQuantities(items, bundle)
          val newPrice = remainder.map(r => bundlePrice + price(r, applicableBundles)).getOrElse(lowest)
          lowest min newPrice
      }
    }
  }
}
