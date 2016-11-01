package sonicpricer

/**
  * Created by slowenth on 10/24/16.
  */


/** Class to find the optimal price of a collection of items
  * The class is initialized with a partial function to price an indiviual item.
  *
  * @param individualPrices Partial function to price an item
  * @param allBundles A Map of all available bundle.  The key is a set of LineItems (a SKU and Quantity); the value is the price of the bundle
  */

class SonicPricer (
                   individualPrices: PartialFunction[SkuType, PriceType],
                   allBundles: Map[LineItems, PriceType]
                 )
{
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

  /**
    * Simple function to price items indiviually
    * Start with 0, and peform a simple foldLeft, on the line items.
    *
    * @param items - I Map of SKU -> quantity
    * @return Sum of the price of the individual items
    */

  def priceItemsIndividually(items: LineItems) =
    items.foldLeft(BigDecimal(0)) { case (total, (sku, quantity)) => total + quantity * individualPrices(sku) }

  def price(items: LineItems): PriceType =
    price(items, allBundles)

  private def price(items: LineItems, bundles: Map[LineItems, PriceType]): PriceType = {

    if (items.isEmpty) 0
    else {

      // Reduce the number of bundles to the ones that are possible
      // It may be sufficient to filter the bundles only once in the parent
      val applicableBundles = filterBundles(items, bundles)

      // Price the items as individuals
      // This makes a good starting point
      val individualPrice = priceItemsIndividually(items)

      // Start with the individual price
      // recursively go through all combinations of bundles
      // and compute every price.  return the minimub of the lowest found or lowest of the bundle price plus the lowest price of the remaining items.
      // We use foldLeft instead of a loop
      applicableBundles.foldLeft[BigDecimal](individualPrice) {
        case (lowest, (bundleItems, _)) =>
          val bundlePrice = applicableBundles(bundleItems)
          val remainder = subtractQuantities(items, bundleItems)
          val newPrice = remainder.map(r => bundlePrice + price(r, applicableBundles)).getOrElse(lowest)
          lowest min newPrice
      }
    }
  }
}
